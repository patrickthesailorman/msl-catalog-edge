package com.kenzan.msl.catalog.edge.services;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import io.swagger.model.AlbumList;
import io.swagger.model.AlbumInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.SongList;
import io.swagger.model.SongInfo;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CassandraCatalogService.class})
public class CatalogEdgeServiceTest extends TestConstants {

  private CassandraCatalogService cassandraCatalogService;

  @Mock
  private AlbumsService albumsService;
  @Mock
  private ArtistsService artistsService;
  @Mock
  private SongsService songsService;

  @Before
  public void init() throws Exception {
    MockitoAnnotations.initMocks(this);

    PowerMock.mockStatic(CassandraCatalogService.class);
    cassandraCatalogService = createMock(CassandraCatalogService.class);
    PowerMock.expectNew(CassandraCatalogService.class).andReturn(cassandraCatalogService);
    expect(CassandraCatalogService.getInstance(EasyMock.anyObject())).andReturn(
        cassandraCatalogService).anyTimes();
  }

  // ==========================================================================================================
  // ALBUMS
  // =================================================================================================================

  @Test
  public void testBrowseAlbums() {
    Mockito.when(
        albumsService.getAlbumsList(Optional.of(USER_ID), ITEMS, FACETS,
            Optional.of(PAGING_STATE_ID))).thenReturn(ALBUM_LIST_BO);
    PowerMock.replayAll();

    /* *********************************** */
    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<AlbumList> results =
        catalogEdgeService.browseAlbums(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first().getAlbums());
    assertEquals(results.toBlocking().first().getAlbums().size(), 0);
  }

  @Test
  public void testGetAlbum() {
    Mockito.when(albumsService.getAlbum(Optional.of(USER_ID), ALBUM_ID)).thenReturn(
        Optional.of(ALBUM_BO));
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<AlbumInfo>> results =
        catalogEdgeService.getAlbum(ALBUM_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getAlbumId(), ALBUM_ID.toString());
  }

  @Test
  public void testGetNullAlbum() {
    Mockito.when(albumsService.getAlbum(Optional.of(USER_ID), ALBUM_ID)).thenReturn(
        Optional.absent());
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<AlbumInfo>> results =
        catalogEdgeService.getAlbum(ALBUM_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

  // =========================================================================================================
  // ARTISTS
  // =================================================================================================================

  @Test
  public void testBrowseArtists() {
    Mockito.when(
        artistsService.getArtistsList(Optional.of(USER_ID), ITEMS, FACETS,
            Optional.of(PAGING_STATE_ID))).thenReturn(ARTIST_LIST_BO);
    PowerMock.replayAll();

    /* *********************************** */
    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<ArtistList> results =
        catalogEdgeService.browseArtists(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertNull(results.toBlocking().first().getArtists());
  }

  @Test
  public void testGetArtist() {
    Mockito.when(artistsService.getArtist(Optional.of(USER_ID), ARTIST_ID)).thenReturn(
        Optional.of(ARTIST_BO));
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<ArtistInfo>> results =
        catalogEdgeService.getArtist(ARTIST_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getArtistName(), ARTIST_NAME);
  }

  @Test
  public void testGetNullArtist() {
    Mockito.when(artistsService.getArtist(Optional.of(USER_ID), ARTIST_ID)).thenReturn(
        Optional.absent());
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<ArtistInfo>> results =
        catalogEdgeService.getArtist(ARTIST_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

  // =========================================================================================================
  // SONGS
  // =================================================================================================================

  @Test
  public void testBrowseSongs() {
    Mockito
        .when(
            songsService.getSongsList(Optional.of(USER_ID), ITEMS, FACETS,
                Optional.of(PAGING_STATE_ID))).thenReturn(SONG_LIST_BO);
    PowerMock.replayAll();

    /* *********************************** */
    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<SongList> results =
        catalogEdgeService.browseSongs(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertNull(results.toBlocking().first().getSongs());
  }

  @Test
  public void testGetSong() {
    Mockito.when(songsService.getSong(Optional.of(USER_ID), SONG_ID)).thenReturn(
        Optional.of(SONG_BO));
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<SongInfo>> results =
        catalogEdgeService.getSong(SONG_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getArtistName(), ARTIST_NAME);
    assertEquals(results.toBlocking().first().get().getAlbumId(), ALBUM_ID.toString());
    assertEquals(results.toBlocking().first().get().getSongId(), SONG_ID.toString());
  }

  @Test
  public void testGetNullSong() {
    Mockito.when(songsService.getSong(Optional.of(USER_ID), SONG_ID)).thenReturn(Optional.absent());
    PowerMock.replayAll();

    /* *********************************** */

    CatalogEdgeService catalogEdgeService =
        new CatalogEdgeService(albumsService, artistsService, songsService);

    Observable<Optional<SongInfo>> results =
        catalogEdgeService.getSong(SONG_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

}
