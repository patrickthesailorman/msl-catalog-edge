/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.AlbumArtistBySongDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.dto.UserRatingsDto;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.List;
import java.util.UUID;

public class SongsService
    implements PaginatorHelper {

    /**
     * Get a song from the given catalog using the service
     *
     * @param cassandraCatalogService CassandraCatalogService
     * @param userUuid Optional&lt;UUID&gt;
     * @param songUuid java.util.UUID
     * @return Optional&lt;SongBo&gt;
     */
    public Optional<SongBo> getSong(final CassandraCatalogService cassandraCatalogService,
                                    final Optional<UUID> userUuid, final UUID songUuid) {
        Observable<ResultSet> queryResults = cassandraCatalogService.getAlbumArtistBySong(songUuid, Optional.absent());

        Result<AlbumArtistBySongDto> mappingResults = cassandraCatalogService.mapAlbumArtistBySong(queryResults)
            .toBlocking().first();

        if ( null == mappingResults ) {
            return Optional.absent();
        }

        SongBo songBo = new SongBo();
        AlbumArtistBySongDto albumArtistBySongDto = mappingResults.one();

        if ( albumArtistBySongDto == null ) {
            return Optional.of(songBo);
        }

        songBo.setSongId(albumArtistBySongDto.getSongId());
        songBo.setSongName(albumArtistBySongDto.getSongName());
        songBo.setAlbumId(albumArtistBySongDto.getAlbumId());
        songBo.setAlbumName(albumArtistBySongDto.getAlbumName());
        songBo.setArtistId(albumArtistBySongDto.getArtistId());
        songBo.setArtistName(albumArtistBySongDto.getArtistName());
        songBo.setDuration(albumArtistBySongDto.getSongDuration());
        songBo.setYear(albumArtistBySongDto.getAlbumYear());
        songBo.setImageLink(albumArtistBySongDto.getImageLink());

        if ( albumArtistBySongDto.getArtistGenres() != null && albumArtistBySongDto.getArtistGenres().size() > 0 ) {
            songBo.setGenre(albumArtistBySongDto.getArtistGenres().iterator().next());
        }

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            libraryHelper.processLibrarySongInfo(libraryHelper.getUserSongs(userUuid.get()), songBo);
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        Optional<AverageRatingsDto> averageRatingsDto = cassandraRatingsService.getAverageRating(songUuid, "Song")
            .toBlocking().first();
        if ( averageRatingsDto.isPresent() ) {
            songBo.setAverageRating((int) (averageRatingsDto.get().getSumRating() / averageRatingsDto.get()
                .getNumRating()));
        }

        if ( userUuid.isPresent() ) {
            Optional<UserRatingsDto> userRatingsDto = cassandraRatingsService
                .getUserRating(userUuid.get(), "Song", songUuid).toBlocking().first();
            if ( userRatingsDto.isPresent() ) {
                songBo.setPersonalRating(userRatingsDto.get().getRating());
            }
        }

        return Optional.of(songBo);
    }

    /**
     * Get a list of songs filtered by facet and using pagination
     *
     * @param cassandraCatalogService CassandraCatalogService
     * @param userUuid Optional&lt;UUID&gt;
     * @param items Integer
     * @param facets String
     * @param pagingStateUuid Optional&lt;UUID&gt;
     * @return SongListBo
     */
    public SongListBo getSongsList(final CassandraCatalogService cassandraCatalogService,
                                   final Optional<UUID> userUuid, final Integer items, final String facets,
                                   final Optional<UUID> pagingStateUuid) {
        SongListBo songListBo = new SongListBo();

        new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.SONG, cassandraCatalogService, this, pagingStateUuid,
                      items, facets).getPage(songListBo);

        if ( userUuid.isPresent() ) {
            LibraryHelper libraryHelper = new LibraryHelper();
            List<SongsByUserDto> userSongs = Translators.translateSongsByUserDto(libraryHelper.getUserSongs(userUuid
                .get()));
            for ( SongBo songBo : songListBo.getBoList() ) {
                libraryHelper.processLibrarySongInfo(userSongs, songBo);
            }
        }

        CassandraRatingsService cassandraRatingsService = CassandraRatingsService.getInstance();

        // Process ratings
        for ( SongBo songBo : songListBo.getBoList() ) {
            Optional<AverageRatingsDto> averageRatingsDto = cassandraRatingsService
                .getAverageRating(songBo.getSongId(), "Song").toBlocking().first();

            if ( averageRatingsDto.isPresent() ) {
                long average = averageRatingsDto.get().getNumRating() / averageRatingsDto.get().getSumRating();
                songBo.setAverageRating((int) average);
            }

            if ( userUuid.isPresent() ) {
                Optional<UserRatingsDto> userRatingsDto = cassandraRatingsService
                    .getUserRating(userUuid.get(), "Song", songBo.getSongId()).toBlocking().first();
                if ( userRatingsDto.isPresent() ) {
                    songBo.setPersonalRating(userRatingsDto.get().getRating());
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
