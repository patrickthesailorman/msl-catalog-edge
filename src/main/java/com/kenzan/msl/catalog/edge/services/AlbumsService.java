/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.SongsArtistByAlbumDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.dto.UserRatingsDto;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.List;
import java.util.UUID;

public class AlbumsService
    implements PaginatorHelper {

    public Optional<AlbumBo> getAlbum(final CassandraCatalogService cassandraCatalogService,
                                      final Optional<UUID> userUuid, final UUID albumUuid) {

        Observable<ResultSet> queryResults = cassandraCatalogService
            .getSongsArtistByAlbum(albumUuid, Optional.absent());

        Result<SongsArtistByAlbumDto> mappingResults = cassandraCatalogService.mapSongsArtistByAlbum(queryResults)
            .toBlocking().first();

        if ( mappingResults == null ) {
            return Optional.absent();
        }

        AlbumBo albumBo = new AlbumBo();
        SongsArtistByAlbumDto songsArtistByAlbumDto = mappingResults.one();

        if ( songsArtistByAlbumDto == null ) {
            return Optional.of(albumBo);
        }

        albumBo.setAlbumId(songsArtistByAlbumDto.getAlbumId());
        albumBo.setAlbumName(songsArtistByAlbumDto.getAlbumName());
        albumBo.setArtistId(songsArtistByAlbumDto.getArtistId());
        albumBo.setArtistName(songsArtistByAlbumDto.getArtistName());
        albumBo.setImageLink(songsArtistByAlbumDto.getImageLink());

        if ( songsArtistByAlbumDto.getArtistGenres() != null && songsArtistByAlbumDto.getArtistGenres().size() > 0 ) {
            albumBo.setGenre(songsArtistByAlbumDto.getArtistGenres().iterator().next());
        }

        // Add the song ID from this DTO if it is not already in the list
        if ( !albumBo.getSongsList().contains(songsArtistByAlbumDto.getSongId().toString()) ) {
            albumBo.getSongsList().add(songsArtistByAlbumDto.getSongId().toString());
        }

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            libraryHelper.processLibraryAlbumInfo(libraryHelper.getUserAlbums(userUuid.get()), albumBo);
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        AverageRatingsDto averageRatingsDto = cassandraRatingsService.getAverageRating(albumUuid, "Album").toBlocking()
            .first();
        if ( null != averageRatingsDto ) {
            albumBo.setAverageRating((int) (averageRatingsDto.getSumRating() / averageRatingsDto.getNumRating()));
        }

        if ( userUuid.isPresent() ) {
            UserRatingsDto userRatingsDto = cassandraRatingsService.getUserRating(userUuid.get(), "Album", albumUuid)
                .toBlocking().first();
            if ( null != userRatingsDto ) {
                albumBo.setPersonalRating(userRatingsDto.getRating());
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

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            List<AlbumsByUserDto> userAlbums = Translators.translateAlbumsByUserDto(libraryHelper
                .getUserAlbums(userUuid.get()));
            for ( AlbumBo albumBo : albumListBo.getBoList() ) {
                libraryHelper.processLibraryAlbumInfo(userAlbums, albumBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for ( AlbumBo albumBo : albumListBo.getBoList() ) {
            AverageRatingsDto averageRatingsDto = cassandraRatingsService
                .getAverageRating(albumBo.getAlbumId(), "Album").toBlocking().first();

            if ( averageRatingsDto != null ) {
                long average = averageRatingsDto.getNumRating() / averageRatingsDto.getSumRating();
                albumBo.setAverageRating((int) average);
            }

            if ( userUuid.isPresent() ) {
                UserRatingsDto userRatingsDto = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Album", albumBo.getAlbumId()).toBlocking().first();
                if ( userRatingsDto != null ) {
                    albumBo.setPersonalRating(userRatingsDto.getRating());
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
