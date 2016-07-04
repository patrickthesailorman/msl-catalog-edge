/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.SongsAlbumsByArtistDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.services.ArtistService;
import com.kenzan.msl.catalog.edge.services.PaginatorHelper;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.ContentType;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.dto.UserRatingsDto;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import rx.Observable;

import java.util.List;
import java.util.UUID;

/**
 * @author kenzan
 */
public class ArtistsServiceImpl implements ArtistService, PaginatorHelper {

  private final CassandraCatalogService cassandraCatalogService;
  private final CassandraRatingsService cassandraRatingsService;
  private final LibraryHelper libraryHelper;

  public ArtistsServiceImpl(final CassandraCatalogService cassandraCatalogService,
      final CassandraRatingsService cassandraRatingsService, final LibraryHelper libraryHelper) {
    this.cassandraCatalogService = cassandraCatalogService;
    this.cassandraRatingsService = cassandraRatingsService;
    this.libraryHelper = libraryHelper;
  }

  /**
   * Get an artist from the given catalog using the service
   *
   * @param userUuid Optional&lt;UUID&gt;
   * @param artistUuid java.util.UUID
   * @return Optional&lt;ArtistBo&gt;
   */
  @Override
  public Optional<ArtistBo> getArtist(final Optional<UUID> userUuid, final UUID artistUuid) {

    Observable<ResultSet> queryResults =
        cassandraCatalogService.getSongsAlbumsByArtist(artistUuid, Optional.absent());

    Result<SongsAlbumsByArtistDto> mappingResults =
        cassandraCatalogService.mapSongsAlbumsByArtist(queryResults).toBlocking().first();

    if (null == mappingResults) {
      return Optional.absent();
    }

    ArtistBo artistBo = new ArtistBo();
    SongsAlbumsByArtistDto songsAlbumsByArtistDto = mappingResults.one();

    if (songsAlbumsByArtistDto == null) {
      return Optional.absent();
    }

    artistBo.setArtistId(songsAlbumsByArtistDto.getArtistId());
    artistBo.setArtistName(songsAlbumsByArtistDto.getArtistName());
    artistBo.setImageLink(songsAlbumsByArtistDto.getImageLink());

    if (songsAlbumsByArtistDto.getArtistGenres() != null
        && songsAlbumsByArtistDto.getArtistGenres().size() > 0) {
      artistBo.setGenre(songsAlbumsByArtistDto.getArtistGenres().iterator().next());
    }

    if (songsAlbumsByArtistDto.getSimilarArtists() != null) {
      for (UUID similarArtistUuid : songsAlbumsByArtistDto.getSimilarArtists().keySet()) {
        artistBo.getSimilarArtistsList().add(similarArtistUuid.toString());
      }
    }

    // Add the album ID from this DTO if it is not already in the list
    if (!artistBo.getAlbumsList().contains(songsAlbumsByArtistDto.getAlbumId().toString())) {
      artistBo.getAlbumsList().add(songsAlbumsByArtistDto.getAlbumId().toString());
    }

    // Add the song ID from this DTO if it is not already in the list
    if (!artistBo.getSongsList().contains(songsAlbumsByArtistDto.getSongId().toString())) {
      artistBo.getSongsList().add(songsAlbumsByArtistDto.getSongId().toString());
    }

    if (userUuid.isPresent()) {
      libraryHelper
          .processLibraryArtistInfo(libraryHelper.getUserArtists(userUuid.get()), artistBo);
    }

    // Process ratings
    Optional<AverageRatingsDto> averageRatingsDto =
        cassandraRatingsService.getAverageRating(artistUuid, ContentType.ARTIST.value).toBlocking()
            .first();

    if (averageRatingsDto.isPresent()) {
      artistBo.setAverageRating((int) (averageRatingsDto.get().getSumRating() / averageRatingsDto
          .get().getNumRating()));
    }

    if (userUuid.isPresent()) {
      Optional<UserRatingsDto> userRatingsDto =
          cassandraRatingsService
              .getUserRating(userUuid.get(), ContentType.ARTIST.value, artistUuid).toBlocking()
              .first();
      if (userRatingsDto.isPresent()) {
        artistBo.setPersonalRating(userRatingsDto.get().getRating());
      }
    }

    return Optional.of(artistBo);
  }

  /**
   * Get a list of artists filtered by facet and using pagination
   *
   * @param userUuid Optional&lt;UUID&gt;
   * @param items Integer
   * @param facets String
   * @param pagingStateUuid Optional&lt;UUID&gt;
   * @return ArtistListBo
   */
  @Override
  public ArtistListBo getArtistsList(final Optional<UUID> userUuid, final Integer items,
      final String facets, final Optional<UUID> pagingStateUuid) {
    ArtistListBo artistListBo = new ArtistListBo();

    new Paginator(CatalogEdgeConstants.MSL_CONTENT_TYPE.ARTIST, cassandraCatalogService, this,
        pagingStateUuid, items, facets).getPage(artistListBo);

    if (userUuid.isPresent()) {
      List<ArtistsByUserDto> userArtists =
          Translators.translateArtistsByUserDto(libraryHelper.getUserArtists(userUuid.get()));
      for (ArtistBo artistBo : artistListBo.getBoList()) {
        libraryHelper.processLibraryArtistInfo(userArtists, artistBo);
      }
    }

    // Process ratings
    for (ArtistBo artistBo : artistListBo.getBoList()) {
      Optional<AverageRatingsDto> averageRatingsDto =
          cassandraRatingsService
              .getAverageRating(artistBo.getArtistId(), ContentType.ARTIST.value).toBlocking()
              .first();

      if (averageRatingsDto.isPresent()) {
        long average =
            averageRatingsDto.get().getSumRating() / averageRatingsDto.get().getNumRating();
        artistBo.setAverageRating((int) average);
      }

      if (userUuid.isPresent()) {
        Optional<UserRatingsDto> userRatingsDto =
            cassandraRatingsService
                .getUserRating(userUuid.get(), ContentType.ARTIST.value, artistBo.getArtistId())
                .toBlocking().first();

        if (userRatingsDto.isPresent()) {
          artistBo.setPersonalRating(userRatingsDto.get().getRating());
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
