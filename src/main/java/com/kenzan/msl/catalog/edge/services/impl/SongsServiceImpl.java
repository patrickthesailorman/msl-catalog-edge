/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.AlbumArtistBySongDto;
import com.kenzan.msl.catalog.client.services.CatalogDataClientService;
import com.kenzan.msl.catalog.edge.services.LibraryHelper;
import com.kenzan.msl.catalog.edge.services.PaginatorHelper;
import com.kenzan.msl.catalog.edge.services.SongService;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.ContentType;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.dto.UserRatingsDto;
import com.kenzan.msl.ratings.client.services.RatingsDataClientService;
import rx.Observable;

import java.util.List;
import java.util.UUID;

/**
 * @author kenzan
 */
public class SongsServiceImpl implements SongService, PaginatorHelper {

  private final CatalogDataClientService catalogDataClientService;
  private final RatingsDataClientService ratingsDataClientService;
  private final LibraryHelper libraryHelper;

  @Inject
  public SongsServiceImpl(final CatalogDataClientService catalogDataClientService,
                          final RatingsDataClientService ratingsDataClientService, final LibraryHelper libraryHelper) {
    this.catalogDataClientService = catalogDataClientService;
    this.ratingsDataClientService = ratingsDataClientService;
    this.libraryHelper = libraryHelper;
  }

  /**
   * Get a song from the given catalog using the service
   *
   * @param userUuid Optional&lt;UUID&gt;
   * @param songUuid java.util.UUID
   * @return Optional&lt;SongBo&gt;
   */
  public Optional<SongBo> getSong(final Optional<UUID> userUuid, final UUID songUuid) {
    Observable<ResultSet> queryResults =
        catalogDataClientService.getAlbumArtistBySong(songUuid, Optional.absent());

    Result<AlbumArtistBySongDto> mappingResults =
        catalogDataClientService.mapAlbumArtistBySong(queryResults).toBlocking().first();

    if (null == mappingResults) {
      return Optional.absent();
    }

    SongBo songBo = new SongBo();
    AlbumArtistBySongDto albumArtistBySongDto = mappingResults.one();

    if (albumArtistBySongDto == null) {
      return Optional.absent();
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

    if (albumArtistBySongDto.getArtistGenres() != null
        && albumArtistBySongDto.getArtistGenres().size() > 0) {
      songBo.setGenre(albumArtistBySongDto.getArtistGenres().iterator().next());
    }

    if (userUuid.isPresent()) {
      libraryHelper.processLibrarySongInfo(libraryHelper.getUserSongs(userUuid.get()), songBo);
    }

    // Process ratings
    Optional<AverageRatingsDto> averageRatingsDto =
        ratingsDataClientService.getAverageRating(songUuid, ContentType.SONG.value).toBlocking()
            .first();
    if (averageRatingsDto.isPresent()) {
      songBo.setAverageRating((int) (averageRatingsDto.get().getSumRating() / averageRatingsDto
          .get().getNumRating()));
    }

    if (userUuid.isPresent()) {
      Optional<UserRatingsDto> userRatingsDto =
          ratingsDataClientService.getUserRating(userUuid.get(), ContentType.SONG.value, songUuid)
              .toBlocking().first();
      if (userRatingsDto.isPresent()) {
        songBo.setPersonalRating(userRatingsDto.get().getRating());
      }
    }

    return Optional.of(songBo);
  }

  /**
   * Get a list of songs filtered by facet and using pagination
   *
   * @param userUuid Optional&lt;UUID&gt;
   * @param items Integer
   * @param facets String
   * @param pagingStateUuid Optional&lt;UUID&gt;
   * @return SongListBo
   */
  public SongListBo getSongsList(final Optional<UUID> userUuid, final Integer items,
      final String facets, final Optional<UUID> pagingStateUuid) {
    SongListBo songListBo = new SongListBo();

    new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.SONG, catalogDataClientService, this,
        pagingStateUuid, items, facets).getPage(songListBo);

    if (userUuid.isPresent()) {
      List<SongsByUserDto> userSongs =
          Translators.translateSongsByUserDto(libraryHelper.getUserSongs(userUuid.get()));
      for (SongBo songBo : songListBo.getBoList()) {
        libraryHelper.processLibrarySongInfo(userSongs, songBo);
      }
    }

    // Process ratings
    for (SongBo songBo : songListBo.getBoList()) {
      Optional<AverageRatingsDto> averageRatingsDto =
          ratingsDataClientService.getAverageRating(songBo.getSongId(), ContentType.SONG.value)
              .toBlocking().first();

      if (averageRatingsDto.isPresent()) {
        long average =
            averageRatingsDto.get().getSumRating() / averageRatingsDto.get().getNumRating();
        songBo.setAverageRating((int) average);
      }

      if (userUuid.isPresent()) {
        Optional<UserRatingsDto> userRatingsDto =
            ratingsDataClientService
                .getUserRating(userUuid.get(), ContentType.SONG.value, songBo.getSongId())
                .toBlocking().first();
        if (userRatingsDto.isPresent()) {
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
