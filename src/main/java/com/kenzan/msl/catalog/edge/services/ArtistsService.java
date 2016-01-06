/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dao.ArtistsByUserDao;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dao.SongsAlbumsByArtistDao;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
import com.kenzan.msl.ratings.client.dao.UserRatingsDao;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;

import java.util.UUID;

public class ArtistsService
    implements PaginatorHelper {

    private LibraryHelper libraryHelper = new LibraryHelper();

    public Optional<ArtistBo> getArtist(final CassandraCatalogService cassandraCatalogService,
                                        final Optional<UUID> userUuid, final UUID artistUuid) {

        ArtistBo artistBo = new ArtistBo();

        SongsAlbumsByArtistDao songsAlbumsByArtistDao = cassandraCatalogService
            .mapSongsAlbumsByArtist(cassandraCatalogService.getSongsAlbumsByArtist(artistUuid, Optional.absent()))
            .toBlocking().first().one();

        if ( null == songsAlbumsByArtistDao ) {
            return Optional.absent();
        }
        else {
            artistBo.setArtistId(songsAlbumsByArtistDao.getArtistId());
            artistBo.setArtistName(songsAlbumsByArtistDao.getArtistName());

            if ( songsAlbumsByArtistDao.getArtistGenres() != null
                && songsAlbumsByArtistDao.getArtistGenres().size() > 0 ) {
                artistBo.setGenre(songsAlbumsByArtistDao.getArtistGenres().iterator().next());
            }

            if ( songsAlbumsByArtistDao.getSimilarArtists() != null ) {
                for ( UUID similarArtistUuid : songsAlbumsByArtistDao.getSimilarArtists().keySet() ) {
                    artistBo.getSimilarArtistsList().add(similarArtistUuid.toString());
                }
            }

            // Add the album ID from this DAO if it is not already in the list
            if ( !artistBo.getAlbumsList().contains(songsAlbumsByArtistDao.getAlbumId().toString()) ) {
                artistBo.getAlbumsList().add(songsAlbumsByArtistDao.getAlbumId().toString());
            }

            // Add the song ID from this DAO if it is not already in the list
            if ( !artistBo.getSongsList().contains(songsAlbumsByArtistDao.getSongId().toString()) ) {
                artistBo.getSongsList().add(songsAlbumsByArtistDao.getSongId().toString());
            }

            if ( userUuid.isPresent() ) {
                libraryHelper.processLibraryArtistInfo(libraryHelper.getUserArtists(userUuid.get()), artistBo);
            }

            CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

            // Process ratings
            AverageRatingsDao averageRatingsDao = cassandraRatingsService.getAverageRating(artistUuid, "Artist")
                .toBlocking().first();
            if ( null != averageRatingsDao ) {
                artistBo.setAverageRating((int) (averageRatingsDao.getSumRating() / averageRatingsDao.getNumRating()));
            }

            if ( userUuid.isPresent() ) {
                UserRatingsDao userRatingsDao = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Artist", artistUuid).toBlocking().first();
                if ( null != userRatingsDao ) {
                    artistBo.setPersonalRating(userRatingsDao.getRating());
                }
            }

            return Optional.of(artistBo);
        }
    }

    public ArtistListBo getArtistsList(final CassandraCatalogService cassandraCatalogService,
                                       final Optional<UUID> userUuid, final Integer items, final String facets,
                                       final Optional<UUID> pagingStateUuid) {
        ArtistListBo artistListBo = new ArtistListBo();

        new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.ARTIST, cassandraCatalogService, this, pagingStateUuid,
                      items, facets).getPage(artistListBo);

        if ( userUuid.isPresent() ) {
            Result<ArtistsByUserDao> userArtists = libraryHelper.getUserArtists(userUuid.get());
            for ( ArtistBo artistBo : artistListBo.getBoList() ) {
                libraryHelper.processLibraryArtistInfo(userArtists, artistBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for ( ArtistBo artistBo : artistListBo.getBoList() ) {
            AverageRatingsDao averageRatingsDao = cassandraRatingsService
                .getAverageRating(artistBo.getArtistId(), "Artist").toBlocking().first();

            long average = averageRatingsDao.getNumRating() / averageRatingsDao.getSumRating();
            artistBo.setAverageRating((int) average);

            if ( userUuid.isPresent() ) {
                UserRatingsDao userRatingsDao = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Artist", artistBo.getArtistId()).toBlocking().first();
                artistBo.setPersonalRating(userRatingsDao.getRating());
            }
        }

        return artistListBo;
    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    public Statement prepareFacetedQuery(final QueryAccessor queryAccessor, final String facetName) {
        return queryAccessor.artistsByFacet(facetName);
    }

    public Statement prepareFeaturedQuery(final QueryAccessor queryAccessor) {
        return queryAccessor.featuredArtists();
    }

    public String getFacetedQueryString(final String facetName) {
        return QueryAccessor.FACETED_ARTISTS_QUERY.replace(":facet_name", "'" + facetName + "'");
    }

    public String getFeaturedQueryString() {
        return QueryAccessor.FEATURED_ARTISTS_QUERY;
    }
}
