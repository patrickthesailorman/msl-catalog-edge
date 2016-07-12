/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services.stub;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.edge.services.AlbumService;
import com.kenzan.msl.catalog.edge.services.ArtistService;
import com.kenzan.msl.catalog.edge.services.CatalogEdgeService;

import com.kenzan.msl.catalog.edge.services.SongService;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.bo.ArtistBo;
import io.swagger.model.AlbumInfo;
import io.swagger.model.AlbumList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.SongInfo;
import io.swagger.model.SongList;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;

import java.util.UUID;

/**
 * Implementation of the CatalogEdgeService interface that mocks the data it returns.
 *
 * @author kenzan
 */
public class StubCatalogEdgeService implements CatalogEdgeService {

  private final AlbumService albumService;
  private final ArtistService artistService;
  private final SongService songService;

  public StubCatalogEdgeService(final AlbumService albumService, final ArtistService artistService,
      final SongService songService) {
    this.albumService = albumService;
    this.artistService = artistService;
    this.songService = songService;
  }

  // ==========================================================================================================
  // ALBUMS
  // =================================================================================================================

  /**
   * Get browsing data for albums in the catalog.
   * <p>
   * This is a paginated query - it returns data one page at a time. The first page is retrieved by
   * passing <code>null</code> as the <code>pagingState</code>. Subsequent pages are retrieved by
   * passing the <code>pagingState</code> that accompanied the previously retrieved page.
   * <p>
   * The page size is determined by the <code>items</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>items</code> parameter is ignored when
   * retrieving subsequent pages).
   * <p>
   * Data can be filtered using the <code>facets</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>facets</code> parameter is ignored when
   * retrieving subsequent pages).
   *
   * @param pagingState Used for pagination control. To retrieve the first page, use
   *        <code>null</code>. To retrieve subsequent pages, use the <code>pagingState</code> that
   *        accompanied the previous page.
   * @param items Specifies the number of items to include in each page. This value is only
   *        necessary on the retrieval of the first page, and will be used for all subsequent pages.
   * @param facets Specifies a comma delimited list of search facet Ids to filter the results. Pass
   *        null or an empty string to not filter.
   * @param userId Specifies a user UUID identifying the currently logged-in user. Will be null for
   *        unauthenticated requests.
   * @return Observable&lt;AlbumList&gt;
   */
  public Observable<AlbumList> browseAlbums(String pagingState, Integer items, String facets,
      String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID pageUuid = StringUtils.isNotEmpty(pagingState) ? UUID.fromString(pagingState) : null;
      AlbumList response =
          Translators.translate(albumService.getAlbumsList(Optional.of(userUuid), items, facets,
              Optional.of(pageUuid)));
      return Observable.just(response);
    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

  /**
   * Get data on an album in the catalog.
   *
   * @param albumId Specifies the UUID of the album to retrieve.
   * @return Observable&lt;AlbumInfo&gt;
   */
  public Observable<Optional<AlbumInfo>> getAlbum(String albumId, String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID albumUuid = UUID.fromString(albumId);
      AlbumInfo repsonse =
          Translators.translate(albumService.getAlbum(Optional.fromNullable(userUuid), albumUuid)
              .get());
      return Observable.just(Optional.fromNullable(repsonse));
    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

  // =========================================================================================================
  // ARTISTS
  // =================================================================================================================

  /**
   * Get browsing data for artists in the catalog.
   * <p>
   * This is a paginated query - it returns data one page at a time. The first page is retrieved by
   * passing <code>null</code> as the <code>pagingState</code>. Subsequent pages are retrieved by
   * passing the <code>pagingState</code> that accompanied the previously retrieved page.
   * <p>
   * The page size is determined by the <code>items</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>items</code> parameter is ignored when
   * retrieving subsequent pages).
   * <p>
   * Data can be filtered using the <code>facets</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>facets</code> parameter is ignored when
   * retrieving subsequent pages).
   *
   * @param pagingState Used for pagination control. To retrieve the first page, use
   *        <code>null</code>. To retrieve subsequent pages, use the <code>pagingState</code> that
   *        accompanied the previous page.
   * @param items Specifies the number of items to include in each page. This value is only
   *        necessary on the retrieval of the first page, and will be used for all subsequent pages.
   * @param facets Specifies a comma delimited list of search facet Ids to filter the results. Pass
   *        null or an empty string to not filter.
   * @param userId Specifies a user UUID identifying the currently logged-in user. Will be null for
   *        unauthenticated requests.
   * @return Observable&lt;ArtistList&gt;
   */
  public Observable<ArtistList> browseArtists(String pagingState, Integer items, String facets,
      String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID pageUuid = StringUtils.isNotEmpty(pagingState) ? UUID.fromString(pagingState) : null;
      ArtistList response =
          Translators.translate(artistService.getArtistsList(Optional.fromNullable(userUuid),
              items, facets, Optional.fromNullable(pageUuid)));
      return Observable.just(response);
    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

  /**
   * Get data on an artist in the catalog.
   *
   * @param artistId Specifies the UUID of the artist to retrieve.
   * @param userId Specifies the UUID of the authenticated user
   * @return Observable&lt;ArtistInfo&gt;
   */
  public Observable<Optional<ArtistInfo>> getArtist(String artistId, String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID artistUuid = UUID.fromString(artistId);
      ArtistBo artistBo =
          artistService.getArtist(Optional.fromNullable(userUuid), artistUuid).get();
      ArtistInfo response = Translators.translate(artistBo);
      return Observable.just(Optional.of(response));

    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

  // ===========================================================================================================
  // SONGS
  // =================================================================================================================

  /**
   * Get browsing data for songs in the catalog.
   * <p>
   * This is a paginated query - it returns data one page at a time. The first page is retrieved by
   * passing <code>null</code> as the <code>pagingState</code>. Subsequent pages are retrieved by
   * passing the <code>pagingState</code> that accompanied the previously retrieved page.
   * <p>
   * The page size is determined by the <code>items</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>items</code> parameter is ignored when
   * retrieving subsequent pages).
   * <p>
   * Data can be filtered using the <code>facets</code> parameter when retrieving the first page.
   * This value is used for all subsequent pages, (the <code>facets</code> parameter is ignored when
   * retrieving subsequent pages).
   *
   * @param pagingState Used for pagination control. To retrieve the first page, use
   *        <code>null</code>. To retrieve subsequent pages, use the <code>pagingState</code> that
   *        accompanied the previous page.
   * @param items Specifies the number of items to include in each page. This value is only
   *        necessary on the retrieval of the first page, and will be used for all subsequent pages.
   * @param facets Specifies a comma delimited list of search facet Ids to filter the results. Pass
   *        null or an empty string to not filter.
   * @param userId Specifies a user UUID identifying the currently logged-in user. Will be null for
   *        unauthenticated requests.
   * @return Observable&lt;SongList&gt;
   */
  public Observable<SongList> browseSongs(String pagingState, Integer items, String facets,
      String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID pageUuid = StringUtils.isNotEmpty(pagingState) ? UUID.fromString(pagingState) : null;
      SongList response =
          Translators.translate(songService.getSongsList(Optional.fromNullable(userUuid), items,
              facets, Optional.fromNullable(pageUuid)));
      return Observable.just(response);
    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

  /**
   * Get data on a song in the catalog.
   *
   * @param songId Specifies the UUID of the song to retrieve.
   * @param userId Specifies the UUID of the authenticated user
   * @return Observable&lt;SongInfo&gt;
   */
  public Observable<Optional<SongInfo>> getSong(String songId, String userId) {
    try {
      UUID userUuid = StringUtils.isNotEmpty(userId) ? UUID.fromString(userId) : null;
      UUID songUuid = UUID.fromString(songId);
      SongInfo response =
          Translators.translate(songService.getSong(Optional.fromNullable(userUuid), songUuid)
              .get());
      return Observable.just(Optional.of(response));
    } catch (Exception e) {
      e.printStackTrace();
      return Observable.empty();
    }
  }

}
