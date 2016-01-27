/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dao.SongsByUserDao;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dao.AlbumArtistBySongDao;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
import com.kenzan.msl.ratings.client.dao.UserRatingsDao;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.UUID;

public class SongsService
    implements PaginatorHelper {

    public Optional<SongBo> getSong(final CassandraCatalogService cassandraCatalogService,
                                    final Optional<UUID> userUuid, final UUID songUuid) {
        Observable<ResultSet> queryResults = cassandraCatalogService.getAlbumArtistBySong(songUuid, Optional.absent());

        Result<AlbumArtistBySongDao> mappingResults = cassandraCatalogService
            .mapAlbumArtistBySong(queryResults)
            .toBlocking().first();

        if ( null == mappingResults ) {
            return Optional.absent();
        }
        
		SongBo songBo = new SongBo();
		AlbumArtistBySongDao albumArtistBySongDao = mappingResults.one();

		songBo.setSongId(albumArtistBySongDao.getSongId());
		songBo.setSongName(albumArtistBySongDao.getSongName());
		songBo.setAlbumId(albumArtistBySongDao.getAlbumId());
		songBo.setAlbumName(albumArtistBySongDao.getAlbumName());
		songBo.setArtistId(albumArtistBySongDao.getArtistId());
		songBo.setArtistName(albumArtistBySongDao.getArtistName());
		songBo.setDuration(albumArtistBySongDao.getSongDuration());
		songBo.setYear(albumArtistBySongDao.getAlbumYear());

		if ( albumArtistBySongDao.getArtistGenres() != null && albumArtistBySongDao.getArtistGenres().size() > 0 ) {
		    songBo.setGenre(albumArtistBySongDao.getArtistGenres().iterator().next());
		}

		if ( userUuid.isPresent() ) {
		    LibraryHelper libraryHelper = new LibraryHelper();
		    libraryHelper.processLibrarySongInfo(libraryHelper.getUserSongs(userUuid.get()), songBo);
		}

		CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

		// Process ratings
		AverageRatingsDao averageRatingsDao = cassandraRatingsService.getAverageRating(songUuid, "Song")
		    .toBlocking().first();
		if ( null != averageRatingsDao ) {
		    songBo.setAverageRating((int) (averageRatingsDao.getSumRating() / averageRatingsDao.getNumRating()));
		}

		if ( userUuid.isPresent() ) {
		    UserRatingsDao userRatingsDao = cassandraRatingsService.getUserRating(userUuid.get(), "Song", songUuid)
		        .toBlocking().first();
		    if ( null != userRatingsDao ) {
		        songBo.setPersonalRating(userRatingsDao.getRating());
		    }
		}

		return Optional.of(songBo);
    }

    public SongListBo getSongsList(final CassandraCatalogService cassandraCatalogService,
                                   final Optional<UUID> userUuid, final Integer items, final String facets,
                                   final Optional<UUID> pagingStateUuid) {
        SongListBo songListBo = new SongListBo();

        new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.SONG, cassandraCatalogService, this, pagingStateUuid,
                      items, facets).getPage(songListBo);

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            Result<SongsByUserDao> userSongs = libraryHelper.getUserSongs(userUuid.get());
            for ( SongBo songBo : songListBo.getBoList() ) {
                libraryHelper.processLibrarySongInfo(userSongs, songBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for ( SongBo songBo : songListBo.getBoList() ) {
            AverageRatingsDao averageRatingsDao = cassandraRatingsService.getAverageRating(songBo.getSongId(), "Song")
                .toBlocking().first();

            if ( averageRatingsDao != null ) {
                long average = averageRatingsDao.getNumRating() / averageRatingsDao.getSumRating();
                songBo.setAverageRating((int) average);
            }

            if ( userUuid.isPresent() ) {
                UserRatingsDao userRatingsDao = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Song", songBo.getSongId()).toBlocking().first();
                if ( userRatingsDao != null ) {
                    songBo.setPersonalRating(userRatingsDao.getRating());
                }
            }
        }

        return songListBo;
    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    public Statement prepareFacetedQuery(final QueryAccessor queryAccessor, final String facetName) {
        return queryAccessor.songsByFacet(facetName);
    }

    public Statement prepareFeaturedQuery(final QueryAccessor queryAccessor) {
        return queryAccessor.featuredSongs();
    }

    public String getFacetedQueryString(final String facetName) {
        return QueryAccessor.FACETED_SONGS_QUERY.replace(":facet_name", "'" + facetName + "'");
    }

    public String getFeaturedQueryString() {
        return QueryAccessor.FEATURED_SONGS_QUERY;
    }

}
