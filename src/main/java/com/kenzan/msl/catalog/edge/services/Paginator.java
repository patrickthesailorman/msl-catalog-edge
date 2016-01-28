/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.common.dao.AbstractDao;
import com.kenzan.msl.catalog.client.dao.FacetDao;
import com.kenzan.msl.catalog.client.dao.PagingStateDao;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.common.bo.AbstractBo;
import com.kenzan.msl.common.bo.AbstractListBo;
import com.kenzan.msl.catalog.edge.manager.FacetManager;
import org.apache.commons.lang3.StringUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handle paginated browse queries of any content (Album, Artist, Song).
 */
public class Paginator {
    private final CatalogEdgeConstants.MSL_CONTENT_TYPE contentType;
    private final CassandraCatalogService cassandraCatalogService;
    private final PaginatorHelper paginatorHelper;
    private final Optional<UUID> pagingStateUuid;
    private final Integer items;
    private final List<FacetDao> facets;

    /**
     * Constructor
     *
     * @param contentType the type of content to be retrieved
     * @param cassandraCatalogService the Datastax QueryAccessor declaring our prepared queries
     * @param paginatorHelper a PaginatorHelper instance to make queries to the appropriate tables
     * @param pagingStateUuid a UUID identifier of the current paging location. Will be null for the
     *            first page and non-null for subsequent pages)
     * @param items the number of items to be included in each page
     * @param facets a comma delimited list of zero or more facet Ids to use to filter the results
     */
    public Paginator( final CatalogEdgeConstants.MSL_CONTENT_TYPE contentType,
                      final CassandraCatalogService cassandraCatalogService, final PaginatorHelper paginatorHelper,
                      final Optional<UUID> pagingStateUuid, final Integer items, final String facets ) {

        this.contentType = contentType;
        this.cassandraCatalogService = cassandraCatalogService;
        this.paginatorHelper = paginatorHelper;
        this.pagingStateUuid = pagingStateUuid;

        this.items = (null == items || items < CatalogEdgeConstants.MSL_BROWSE_MIN_PAGE_SIZE || items > CatalogEdgeConstants.MSL_BROWSE_MAX_PAGE_SIZE)
                                                                                                                                                      ? CatalogEdgeConstants.MSL_BROWSE_DEFAULT_PAGE_SIZE
                                                                                                                                                      : items;

        this.facets = new ArrayList<>();
        if ( !StringUtils.isEmpty(facets) ) {
            String[] facetIds = facets.split(",");
            for ( String facetId : facetIds ) {
                Optional<FacetDao> optFacetDao = FacetManager.getInstance().getFacet(facetId);
                if ( optFacetDao.isPresent() ) {
                    this.facets.add(optFacetDao.get());
                }
            }
        }
    }

    /**
     * Retrieves a page of content and populates the AbstractListBo accordingly.
     *
     * @param abstractListBo the contentListBo that will be populated with the page's data.
     */
    public void getPage(AbstractListBo<? extends AbstractBo> abstractListBo) {
        if ( pagingStateUuid.isPresent() ) {
            getSubsequentPage(abstractListBo);
        }
        else {
            getFirstPage(abstractListBo);
        }
    }

    /**
     * Retrieves the first page of content and populates the AbstractListBo accordingly.
     *
     * @param abstractListBo the AbstractListBo that will be populated with the page's data.
     */
    private void getFirstPage(AbstractListBo<? extends AbstractBo> abstractListBo) {
        Statement statement;
        String queryString;
        final UUID pagingStateUuid = UUID.randomUUID();

        if ( hasFacets() ) {
            statement = paginatorHelper.prepareFacetedQuery(cassandraCatalogService.queryAccessor, this.facets.get(0)
                .getFacetName());
            queryString = paginatorHelper.getFacetedQueryString(this.facets.get(0).getFacetName());
        }
        else {
            statement = paginatorHelper.prepareFeaturedQuery(cassandraCatalogService.queryAccessor);
            queryString = paginatorHelper.getFeaturedQueryString();
        }

        statement.setFetchSize(items);
        ResultSet resultSet = cassandraCatalogService.mappingManager.getSession().execute(statement);

        // Populate the AbstractListBo with the results of the query
        buildAbstractListBo(resultSet, pagingStateUuid, abstractListBo);

        // If there is a subsequent page, then add row to paging_state table
        if ( abstractListBo.getPagingState() != null ) {
            addPagingState(pagingStateUuid, queryString, resultSet);
        }

        // TODO Queue background thread to retrieve next page
    }

    /**
     * Retrieves a subsequent page (that is: not the first page) of content and populates the
     * AbstractListBo accordingly.
     *
     * @param abstractListBo the AbstractListBo that will be populated with the page's data.
     */
    private void getSubsequentPage(AbstractListBo<? extends AbstractBo> abstractListBo) {

        Optional<PagingStateDao> optPagingStateDao = retrievePagingState(pagingStateUuid.get());

        if ( optPagingStateDao.isPresent() ) {

            PagingStateDao pagingStateDao = optPagingStateDao.get();

            Statement statement = new SimpleStatement(pagingStateDao.getPagingState().getQuery())
                .setPagingStateUnsafe(pagingStateDao.getPagingState().getPageStateBlob())
                .setFetchSize(pagingStateDao.getPagingState().getPageSize());

            ResultSet resultSet = cassandraCatalogService.mappingManager.getSession().execute(statement);

            // Populate the AbstractListBo with the results of the query
            buildAbstractListBo(resultSet, pagingStateUuid.get(), abstractListBo);

            // If there is a subsequent page, then update row in paging_state table, otherwise
            // delete
            // the row
            if ( null == abstractListBo.getPagingState() ) {
                deletePagingState(pagingStateUuid);
            }
            else {
                savePagingState(pagingStateDao, resultSet);
            }
            // TODO Queue background thread to retrieve next page
        }
    }

    /**
     * Populates the AbstractListBo with the results from <code>resultSet</code> and, optionally,
     * the <code>pagingStateUuid</code>. If the results are the last results in the table, then
     * DON'T include the <code>pagingStateUuid</code> in the AbstractListBo. This is the flag to the
     * caller that the last page of data has been retrieved.
     *
     * @param resultSet the query results that should be used to build the page
     * @param pagingStateUuid the pagingState to include in the AbstractListBo if this is NOT the
     *            last page of data.
     * @param abstractListBo the AbstractListBo that will be populated with the page's data.
     */
    // TODO Resolve the issue with adding elements to a generic List<? extends AbstractBo>. This is
    // the reason for the SuppressWarnings annotation.
    private AbstractListBo<? extends AbstractBo> buildAbstractListBo(ResultSet resultSet, final UUID pagingStateUuid,
                                                                     AbstractListBo<? extends AbstractBo> abstractListBo) {
        // Map the results from the resultSet to our BO POJO
        Class<? extends AbstractDao> boClass;
        if ( hasFacets() ) {
            boClass = contentType.facetContentDaoClass;
        }
        else {
            boClass = contentType.featuredContentDaoClass;
        }

        Result<? extends AbstractDao> mappedResults = cassandraCatalogService.mappingManager.mapper(boClass)
            .map(resultSet);

        for ( AbstractDao dao : mappedResults ) {
            abstractListBo.add(dao);

            /*
             * Have we reached the end of the page (set via the fetch size on the Statement)? If
             * this check were not performed, the Datastax driver would silently get the next page
             * of results. This is a cool feature in some use cases, we don't want the Datastax
             * driver to do it in this case.
             */
            if ( resultSet.getAvailableWithoutFetching() == 0 ) {
                break;
            }
        }

        // Have we reached the end of the table?
        if ( !resultSet.isFullyFetched() ) {
            // If not, then include the paging state UUID in the response so the caller knows there
            // is a subsequent page.
            abstractListBo.setPagingState(pagingStateUuid);
        }

        return abstractListBo;
    }

    /**
     * Add a row to the Cassandra paging_state table with the info appropriate to query for the
     * subsequent page
     *
     * @param pagingStateUuid the UUID to assign to this paging state row. This is the UUID that
     *            will be sent to the client and expected in requests for subsequent pages.
     * @param query the actual query string. This is required to use the Cassandra PageState.
     * @param resultSet the resultSet that has just been consumed
     */
    private void addPagingState(final UUID pagingStateUuid, final String query, final ResultSet resultSet) {

        // Build the paging state user defined type (UDT)
        PagingStateDao.PagingStateUdt pagingStateUdt = new PagingStateDao.PagingStateUdt();
        pagingStateUdt.setPageSize(this.items);
        pagingStateUdt.setContentType(this.contentType.name());
        pagingStateUdt.setQuery(query);
        pagingStateUdt.setEnd(false);
        pagingStateUdt.setBuffer(null);

        // Build the paging state DAO
        PagingStateDao pagingStateDao = new PagingStateDao();
        pagingStateDao.setUserId(pagingStateUuid);
        pagingStateDao.setPagingState(pagingStateUdt);

        // Add the paging state DAO to Cassandra
        savePagingState(pagingStateDao, resultSet);
    }

    /**
     * Add/update a row in the Cassandra paging_state table. This method is used for both adding new
     * rows and updating existing rows.
     *
     * @param pagingStateDao the DAO that will receive updated Cassandra page state info then be
     *            written to the DB
     * @param resultSet the current result set from which the Cassandra page state info will be
     *            extracted
     */
    private void savePagingState(PagingStateDao pagingStateDao, final ResultSet resultSet) {
        // Put the Cassandra PageState into the PagingStateUdt
        byte[] cassandraPageState = resultSet.getExecutionInfo().getPagingStateUnsafe();
        if ( null == cassandraPageState ) {
            pagingStateDao.getPagingState().setPageState(null);
        }
        else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(cassandraPageState.length);
            byteBuffer.put(cassandraPageState);
            byteBuffer.flip(); // Have to do this to reset the internals of the ByteBuff to prepare
            // it to be consumed
            pagingStateDao.getPagingState().setPageState(byteBuffer);
        }
        cassandraCatalogService.addOrUpdatePagingState(pagingStateDao);
    }

    /**
     * Retrieve a paging state DAO using the paging state UUID received from the client. Will retry
     * multiple times if the background thread has not yet populated the buffer.
     *
     * @param pagingStateUuid the paging state UUID sent to the client as a response to the query
     *            for the previous page
     * @return Optional<PagingStateDao>
     */
    private Optional<PagingStateDao> retrievePagingState(UUID pagingStateUuid) {
        PagingStateDao pagingStateDao = cassandraCatalogService.getPagingState(pagingStateUuid).toBlocking().first();
        if ( pagingStateDao != null ) {
            return Optional.of(pagingStateDao);
        }

        return Optional.absent();
    }

    private void deletePagingState(Optional<UUID> pagingStateUuid) {
        if ( pagingStateUuid.isPresent() ) {
            cassandraCatalogService.deletePagingState(pagingStateUuid.get());
        }
    }

    /**
     * Helper method to see if any facets exist
     *
     * @return true if facets exist, false otherwise
     */
    private boolean hasFacets() {
        return !(null == facets || facets.size() == 0);
    }

}
