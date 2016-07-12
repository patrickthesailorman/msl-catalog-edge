package com.kenzan.msl.catalog.edge.services;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.catalog.edge.services.impl.AlbumsServiceImpl;
import com.kenzan.msl.catalog.edge.services.impl.ArtistsServiceImpl;
import com.kenzan.msl.catalog.edge.services.impl.CatalogEdgeServiceImpl;
import com.kenzan.msl.catalog.edge.services.impl.SongsServiceImpl;
import io.swagger.model.AlbumList;
import io.swagger.model.AlbumInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.SongList;
import io.swagger.model.SongInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CatalogEdgeServiceImplTest extends TestConstants {

  @Mock
  private AlbumsServiceImpl albumsServiceImpl;
  @Mock
  private ArtistsServiceImpl artistsServiceImpl;
  @Mock
  private SongsServiceImpl songsServiceImpl;

  @InjectMocks
  private CatalogEdgeServiceImpl catalogEdgeServiceImpl;

  // ==========================================================================================================
  // ALBUMS
  // =================================================================================================================

  @Test
  public void testBrowseAlbums() {
    Mockito.when(
        albumsServiceImpl.getAlbumsList(Optional.of(USER_ID), ITEMS, FACETS,
            Optional.of(PAGING_STATE_ID))).thenReturn(ALBUM_LIST_BO);

    Observable<AlbumList> results =
        catalogEdgeServiceImpl.browseAlbums(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first().getAlbums());
    assertEquals(results.toBlocking().first().getAlbums().size(), 0);
  }

  @Test
  public void testGetAlbum() {
    Mockito.when(albumsServiceImpl.getAlbum(Optional.of(USER_ID), ALBUM_ID)).thenReturn(
        Optional.of(ALBUM_BO));

    Observable<Optional<AlbumInfo>> results =
        catalogEdgeServiceImpl.getAlbum(ALBUM_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getAlbumId(), ALBUM_ID.toString());
  }

  @Test
  public void testGetNullAlbum() {
    Mockito.when(albumsServiceImpl.getAlbum(Optional.of(USER_ID), ALBUM_ID)).thenReturn(
        Optional.absent());

    Observable<Optional<AlbumInfo>> results =
        catalogEdgeServiceImpl.getAlbum(ALBUM_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

  // =========================================================================================================
  // ARTISTS
  // =================================================================================================================

  @Test
  public void testBrowseArtists() {
    Mockito.when(
        artistsServiceImpl.getArtistsList(Optional.of(USER_ID), ITEMS, FACETS,
            Optional.of(PAGING_STATE_ID))).thenReturn(ARTIST_LIST_BO);

    Observable<ArtistList> results =
        catalogEdgeServiceImpl.browseArtists(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertNull(results.toBlocking().first().getArtists());
  }

  @Test
  public void testGetArtist() {
    Mockito.when(artistsServiceImpl.getArtist(Optional.of(USER_ID), ARTIST_ID)).thenReturn(
        Optional.of(ARTIST_BO));

    Observable<Optional<ArtistInfo>> results =
        catalogEdgeServiceImpl.getArtist(ARTIST_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getArtistName(), ARTIST_NAME);
  }

  @Test
  public void testGetNullArtist() {
    Mockito.when(artistsServiceImpl.getArtist(Optional.of(USER_ID), ARTIST_ID)).thenReturn(
        Optional.absent());

    Observable<Optional<ArtistInfo>> results =
        catalogEdgeServiceImpl.getArtist(ARTIST_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

  // =========================================================================================================
  // SONGS
  // =================================================================================================================

  @Test
  public void testBrowseSongs() {
    Mockito.when(
        songsServiceImpl.getSongsList(Optional.of(USER_ID), ITEMS, FACETS,
            Optional.of(PAGING_STATE_ID))).thenReturn(SONG_LIST_BO);

    Observable<SongList> results =
        catalogEdgeServiceImpl.browseSongs(PAGING_STATE_ID.toString(), ITEMS, FACETS,
            USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertNull(results.toBlocking().first().getSongs());
  }

  @Test
  public void testGetSong() {
    Mockito.when(songsServiceImpl.getSong(Optional.of(USER_ID), SONG_ID)).thenReturn(
        Optional.of(SONG_BO));

    Observable<Optional<SongInfo>> results =
        catalogEdgeServiceImpl.getSong(SONG_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertEquals(results.toBlocking().first().get().getArtistId(), ARTIST_ID.toString());
    assertEquals(results.toBlocking().first().get().getArtistName(), ARTIST_NAME);
    assertEquals(results.toBlocking().first().get().getAlbumId(), ALBUM_ID.toString());
    assertEquals(results.toBlocking().first().get().getSongId(), SONG_ID.toString());
  }

  @Test
  public void testGetNullSong() {
    Mockito.when(songsServiceImpl.getSong(Optional.of(USER_ID), SONG_ID)).thenReturn(
        Optional.absent());

    Observable<Optional<SongInfo>> results =
        catalogEdgeServiceImpl.getSong(SONG_ID.toString(), USER_ID.toString());
    assertNotNull(results.toBlocking().first());
    assertFalse(results.toBlocking().first().isPresent());
  }

}
