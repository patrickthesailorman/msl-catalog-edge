package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.Statement;
import com.kenzan.msl.common.bo.AlbumListBo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.catalog.client.dao.SongsArtistByAlbumDao;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import org.easymock.EasyMock;
import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CassandraRatingsService.class, CassandraAccountService.class, CassandraCatalogService.class, Paginator.class})
public class AlbumsServiceTest {

    private TestConstants tc = TestConstants.getInstance();

    private CassandraCatalogService cassandraCatalogService;

    private CassandraRatingsService cassandraRatingsService;
    private CassandraAccountService cassandraAccountService;

    private ResultSet resultSet;
    private Observable<ResultSet> observableResultSet;

    @Mock
    private LibraryHelper libraryhelper;
    @Mock
    private QueryAccessor queryAccessor;
    @InjectMocks
    private AlbumsService albumsService = new AlbumsService();

    @Before
    public void init() throws Exception {
        resultSet = createMock(ResultSet.class);
        observableResultSet = Observable.just(resultSet);
        queryAccessor = mock(QueryAccessor.class);

        PowerMock.mockStatic(CassandraCatalogService.class);
        cassandraCatalogService = createMock(CassandraCatalogService.class);

        PowerMock.mockStatic(CassandraAccountService.class);
        cassandraAccountService = createMock(CassandraAccountService.class);
        PowerMock.expectNew(CassandraAccountService.class).andReturn(cassandraAccountService);

        expect(CassandraAccountService.getInstance()).andReturn(cassandraAccountService).anyTimes();
    }

    @Test
    public void testGetAlbum() throws Exception {
        expect(cassandraCatalogService.getSongsArtistByAlbum(tc.ALBUM_ID, Optional.absent()))
                .andReturn(observableResultSet);

        Result<SongsArtistByAlbumDao> songsArtistByAlbumDaoResult = PowerMockito.mock(Result.class);
        expect(cassandraCatalogService.mapSongsArtistByAlbum(observableResultSet))
                .andReturn(Observable.just(songsArtistByAlbumDaoResult));

        PowerMockito.when(songsArtistByAlbumDaoResult.one()).thenReturn(tc.songsArtistByAlbumDao);

        mockRatingsHelper();

        AverageRatingsDao averageRatingsDao = new AverageRatingsDao();
        averageRatingsDao.setNumRating(new Long(2));
        averageRatingsDao.setSumRating(new Long(4));
        expect(cassandraRatingsService.getAverageRating(EasyMock.anyObject(UUID.class), EasyMock.anyString()))
                .andReturn(Observable.just(averageRatingsDao));

        EasyMock.replay(cassandraRatingsService);
        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        /* *************************************************** */

        Optional<AlbumBo> results = albumsService.getAlbum(cassandraCatalogService, Optional.absent(), tc.ALBUM_ID);
        assertNotNull(results);
        assertTrue(results.isPresent());
        assertEquals(results.get().getAlbumId(), tc.ALBUM_ID);
        assertEquals(results.get().getArtistId(), tc.ARTIST_ID);
        assertEquals(results.get().getAverageRating(), new Integer(2));
    }

    @Test
    public void testGetNullAlbum() {
        expect(cassandraCatalogService.getSongsArtistByAlbum(tc.ALBUM_ID, Optional.absent()))
                .andReturn(observableResultSet);
        expect(cassandraCatalogService.mapSongsArtistByAlbum(observableResultSet))
                .andReturn(Observable.just(null));

        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        Optional<AlbumBo> result = albumsService.getAlbum(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ALBUM_ID);
        assertEquals(result, Optional.absent());
    }

    @Test
    @Ignore
    public void testGetAlbumsList() throws Exception {

        Paginator paginator = EasyMock.createMock(Paginator.class);
        PowerMock.expectNew(Paginator.class,
                EasyMock.eq(CatalogEdgeConstants.MSL_CONTENT_TYPE.ALBUM),
                EasyMock.eq(cassandraCatalogService),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject())
                .andReturn(paginator);

        mockRatingsHelper();

        EasyMock.expect(cassandraCatalogService.getPagingState(tc.PAGING_STATE_ID))
                .andReturn(Observable.just(tc.pagingStateDao));

        EasyMock.expect(cassandraCatalogService.mappingManager.getSession().execute(EasyMock.anyObject(Statement.class)))
                .andReturn(resultSet);

//        expect(cassandraCatalogService.mappingManager.mapper(EasyMock.anyObject()).map(resultSet)).andReturn(tc.featuredAlbumDaoList);
        expect(resultSet.getAvailableWithoutFetching()).andReturn(0);
        expect(resultSet.isFullyFetched()).andReturn(false);

        EasyMock.replay(paginator);
        EasyMock.replay(cassandraRatingsService);
        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);

        PowerMock.replayAll();

        /* ********************************************************* */

        System.out.println("=================================");
        System.out.println(tc.albumBoList);
        System.out.println("=================================");

        AlbumListBo albumListBo = albumsService.getAlbumsList(cassandraCatalogService, Optional.absent(), tc.ITEMS, tc.FACETS, Optional.of(tc.PAGING_STATE_ID));

    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    @Test
    public void testPrepareFacetedQuery() {
        albumsService.prepareFacetedQuery(queryAccessor, "~");
        verify(queryAccessor, atLeastOnce()).albumsByFacet("~");
    }

    @Test
    public void testPrepareFeaturedQuery() {
        albumsService.prepareFeaturedQuery(queryAccessor);
        verify(queryAccessor, atLeastOnce()).featuredAlbums();
    }

    @Test
    public void testGetFacetedQueryString() {
        String returned = albumsService.getFacetedQueryString("~");
        String expected = "SELECT * FROM albums_by_facet WHERE facet_name = '~' AND content_type = 'Album'";
        assertEquals(expected, returned);
    }

    @Test
    public void testGetFeaturedQueryString() {
        String returned = albumsService.getFeaturedQueryString();
        String expected = "SELECT * FROM featured_albums WHERE hotness_bucket = 'Hotness01' AND content_type = 'Album'";
        assertEquals(expected, returned);
    }

    private void mockRatingsHelper()
            throws Exception {
        PowerMock.mockStatic(CassandraRatingsService.class);
        cassandraRatingsService = createMock(CassandraRatingsService.class);
        PowerMock.expectNew(CassandraRatingsService.class).andReturn(cassandraRatingsService);

        expect(CassandraRatingsService.getInstance()).andReturn(cassandraRatingsService).anyTimes();
    }
}
