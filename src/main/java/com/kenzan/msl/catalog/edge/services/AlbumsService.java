/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dao.AlbumsByUserDao;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dao.SongsArtistByAlbumDao;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
import com.kenzan.msl.ratings.client.dao.UserRatingsDao;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.UUID;

public class AlbumsService
        implements PaginatorHelper {


    public Optional<AlbumBo> getAlbum(final CassandraCatalogService cassandraCatalogService,
                                      final Optional<UUID> userUuid, final UUID albumUuid) {

        Observable<ResultSet> queryResults = cassandraCatalogService.getSongsArtistByAlbum(albumUuid, Optional.absent());

        Result<SongsArtistByAlbumDao> mappingResults = cassandraCatalogService
                .mapSongsArtistByAlbum(queryResults)
                .toBlocking()
                .first();

        if (mappingResults == null) {
            return Optional.absent();
        }
        
		AlbumBo albumBo = new AlbumBo();
		SongsArtistByAlbumDao songsArtistByAlbumDao = mappingResults.one();

		albumBo.setAlbumId(songsArtistByAlbumDao.getAlbumId());
		albumBo.setAlbumName(songsArtistByAlbumDao.getAlbumName());
		albumBo.setArtistId(songsArtistByAlbumDao.getArtistId());
		albumBo.setArtistName(songsArtistByAlbumDao.getArtistName());
		albumBo.setImageLink(songsArtistByAlbumDao.getImageLink());

		if (songsArtistByAlbumDao.getArtistGenres() != null && songsArtistByAlbumDao.getArtistGenres().size() > 0) {
		    albumBo.setGenre(songsArtistByAlbumDao.getArtistGenres().iterator().next());
		}

		// Add the song ID from this DAO if it is not already in the list
		if (!albumBo.getSongsList().contains(songsArtistByAlbumDao.getSongId().toString())) {
		    albumBo.getSongsList().add(songsArtistByAlbumDao.getSongId().toString());
		}

		if (userUuid.isPresent()) {
		    LibraryHelper libraryHelper = new LibraryHelper();
		    libraryHelper.processLibraryAlbumInfo(libraryHelper.getUserAlbums(userUuid.get()), albumBo);
		}

		CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

		// Process ratings
		AverageRatingsDao averageRatingsDao = cassandraRatingsService.getAverageRating(albumUuid, "Album")
		        .toBlocking().first();
		if (null != averageRatingsDao) {
		    albumBo.setAverageRating((int) (averageRatingsDao.getSumRating() / averageRatingsDao.getNumRating()));
		}

		if (userUuid.isPresent()) {
		    UserRatingsDao userRatingsDao = cassandraRatingsService
		            .getUserRating(userUuid.get(), "Album", albumUuid).toBlocking().first();
		    if (null != userRatingsDao) {
		        albumBo.setPersonalRating(userRatingsDao.getRating());
		    }
		}

		return Optional.of(albumBo);

    }

    public AlbumListBo getAlbumsList(final CassandraCatalogService cassandraCatalogService,
                                     final Optional<UUID> userUuid, final Integer items, final String facets,
                                     final Optional<UUID> pagingStateUuid) {

        AlbumListBo albumListBo = new AlbumListBo();

        new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.ALBUM, cassandraCatalogService, this, pagingStateUuid,
                items, facets).getPage(albumListBo);

        if (userUuid.isPresent()) {
            LibraryHelper libraryHelper = new LibraryHelper();
            Result<AlbumsByUserDao> userAlbums = libraryHelper.getUserAlbums(userUuid.get());
            for ( AlbumBo albumBo : albumListBo.getBoList() ) {
                libraryHelper.processLibraryAlbumInfo(userAlbums, albumBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for (AlbumBo albumBo : albumListBo.getBoList()) {
            AverageRatingsDao averageRatingsDao = cassandraRatingsService
                    .getAverageRating(albumBo.getAlbumId(), "Album").toBlocking().first();

            if (averageRatingsDao != null) {
                long average = averageRatingsDao.getNumRating() / averageRatingsDao.getSumRating();
                albumBo.setAverageRating((int) average);
            }

            if (userUuid.isPresent()) {
                UserRatingsDao userRatingsDao = cassandraRatingsService
                        .getUserRating(userUuid.get(), "Album", albumBo.getAlbumId()).toBlocking().first();
                if (userRatingsDao != null) {
                    albumBo.setPersonalRating(userRatingsDao.getRating());
                }
            }
        }

        return albumListBo;
    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    public Statement prepareFacetedQuery(final QueryAccessor queryAccessor, final String facetName) {
        return queryAccessor.albumsByFacet(facetName);
    }

    public Statement prepareFeaturedQuery(final QueryAccessor queryAccessor) {
        return queryAccessor.featuredAlbums();
    }

    public String getFacetedQueryString(final String facetName) {
        return QueryAccessor.FACETED_ALBUMS_QUERY.replace(":facet_name", "'" + facetName + "'");
    }

    public String getFeaturedQueryString() {
        return QueryAccessor.FEATURED_ALBUMS_QUERY;
    }

}
