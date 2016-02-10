/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.SongsAlbumsByArtistDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.dto.UserRatingsDto;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.List;
import java.util.UUID;

public class ArtistsService
    implements PaginatorHelper {

    public Optional<ArtistBo> getArtist(final CassandraCatalogService cassandraCatalogService,
                                        final Optional<UUID> userUuid, final UUID artistUuid) {

        Observable<ResultSet> queryResults = cassandraCatalogService.getSongsAlbumsByArtist(artistUuid,
                                                                                            Optional.absent());

        Result<SongsAlbumsByArtistDto> mappingResults = cassandraCatalogService.mapSongsAlbumsByArtist(queryResults)
            .toBlocking().first();

        if ( null == mappingResults ) {
            return Optional.absent();
        }

        ArtistBo artistBo = new ArtistBo();
        SongsAlbumsByArtistDto songsAlbumsByArtistDto = mappingResults.one();

        if ( songsAlbumsByArtistDto == null ) {
            return Optional.of(artistBo);
        }

        artistBo.setArtistId(songsAlbumsByArtistDto.getArtistId());
        artistBo.setArtistName(songsAlbumsByArtistDto.getArtistName());
        artistBo.setImageLink(songsAlbumsByArtistDto.getImageLink());

        if ( songsAlbumsByArtistDto.getArtistGenres() != null && songsAlbumsByArtistDto.getArtistGenres().size() > 0 ) {
            artistBo.setGenre(songsAlbumsByArtistDto.getArtistGenres().iterator().next());
        }

        if ( songsAlbumsByArtistDto.getSimilarArtists() != null ) {
            for ( UUID similarArtistUuid : songsAlbumsByArtistDto.getSimilarArtists().keySet() ) {
                artistBo.getSimilarArtistsList().add(similarArtistUuid.toString());
            }
        }

        // Add the album ID from this DTO if it is not already in the list
        if ( !artistBo.getAlbumsList().contains(songsAlbumsByArtistDto.getAlbumId().toString()) ) {
            artistBo.getAlbumsList().add(songsAlbumsByArtistDto.getAlbumId().toString());
        }

        // Add the song ID from this DTO if it is not already in the list
        if ( !artistBo.getSongsList().contains(songsAlbumsByArtistDto.getSongId().toString()) ) {
            artistBo.getSongsList().add(songsAlbumsByArtistDto.getSongId().toString());
        }

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            libraryHelper.processLibraryArtistInfo(libraryHelper.getUserArtists(userUuid.get()), artistBo);
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        AverageRatingsDto averageRatingsDto = cassandraRatingsService.getAverageRating(artistUuid, "Artist")
            .toBlocking().first();
        if ( null != averageRatingsDto ) {
            artistBo.setAverageRating((int) (averageRatingsDto.getSumRating() / averageRatingsDto.getNumRating()));
        }

        if ( userUuid.isPresent() ) {
            UserRatingsDto userRatingsDto = cassandraRatingsService.getUserRating(userUuid.get(), "Artist", artistUuid)
                .toBlocking().first();
            if ( null != userRatingsDto ) {
                artistBo.setPersonalRating(userRatingsDto.getRating());
            }
        }

        return Optional.of(artistBo);
    }

    public ArtistListBo getArtistsList(final CassandraCatalogService cassandraCatalogService,
                                       final Optional<UUID> userUuid, final Integer items, final String facets,
                                       final Optional<UUID> pagingStateUuid) {
        ArtistListBo artistListBo = new ArtistListBo();

        new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.ARTIST, cassandraCatalogService, this, pagingStateUuid,
                      items, facets).getPage(artistListBo);

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            List<ArtistsByUserDto> userArtists = Translators.translateArtistsByUserDto(libraryHelper
                .getUserArtists(userUuid.get()));
            for ( ArtistBo artistBo : artistListBo.getBoList() ) {
                libraryHelper.processLibraryArtistInfo(userArtists, artistBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for ( ArtistBo artistBo : artistListBo.getBoList() ) {
            AverageRatingsDto averageRatingsDto = cassandraRatingsService
                .getAverageRating(artistBo.getArtistId(), "Artist").toBlocking().first();

            if ( averageRatingsDto != null ) {
                long average = averageRatingsDto.getNumRating() / averageRatingsDto.getSumRating();
                artistBo.setAverageRating((int) average);
            }

            if ( userUuid.isPresent() ) {
                UserRatingsDto userRatingsDto = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Artist", artistBo.getArtistId()).toBlocking().first();

                if ( userRatingsDto != null ) {
                    artistBo.setPersonalRating(userRatingsDto.getRating());
                }
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
