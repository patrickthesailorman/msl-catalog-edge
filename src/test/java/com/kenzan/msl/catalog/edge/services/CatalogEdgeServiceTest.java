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
@PrepareForTest({ CassandraCatalogService.class })
public class CatalogEdgeServiceTest {

    private TestConstants tc = TestConstants.getInstance();
    private CassandraCatalogService cassandraCatalogService;

    @Mock
    private AlbumsService albumsService;
    @Mock
    private ArtistsService artistsService;
    @Mock
    private SongsService songsService;

    @Before
    public void init()
        throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMock.mockStatic(CassandraCatalogService.class);
        cassandraCatalogService = createMock(CassandraCatalogService.class);
        PowerMock.expectNew(CassandraCatalogService.class).andReturn(cassandraCatalogService);
        expect(CassandraCatalogService.getInstance()).andReturn(cassandraCatalogService).anyTimes();
    }

    // ==========================================================================================================
    // ALBUMS
    // =================================================================================================================

    @Test
    public void testBrowseAlbums() {
        Mockito.when(albumsService.getAlbumsList(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ITEMS, tc.FACETS,
                                                 Optional.of(tc.PAGING_STATE_ID))).thenReturn(tc.ALBUM_LIST_BO);
        PowerMock.replayAll();

        /* *********************************** */
        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<AlbumList> results = catalogEdgeService.browseAlbums(tc.PAGING_STATE_ID.toString(), tc.ITEMS,
                                                                        tc.FACETS, tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first().getAlbums());
        assertEquals(results.toBlocking().first().getAlbums().size(), 0);
    }

    @Test
    public void testGetAlbum() {
        Mockito.when(albumsService.getAlbum(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ALBUM_ID))
            .thenReturn(Optional.of(tc.ALBUM_BO));
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<AlbumInfo>> results = catalogEdgeService.getAlbum(tc.ALBUM_ID.toString(),
                                                                              tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertEquals(results.toBlocking().first().get().getArtistId(), tc.ARTIST_ID.toString());
        assertEquals(results.toBlocking().first().get().getAlbumId(), tc.ALBUM_ID.toString());
    }

    @Test
    public void testGetNullAlbum() {
        Mockito.when(albumsService.getAlbum(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ALBUM_ID))
            .thenReturn(Optional.absent());
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<AlbumInfo>> results = catalogEdgeService.getAlbum(tc.ALBUM_ID.toString(),
                                                                              tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertFalse(results.toBlocking().first().isPresent());
    }

    // =========================================================================================================
    // ARTISTS
    // =================================================================================================================

    @Test
    public void testBrowseArtists() {
        Mockito.when(artistsService.getArtistsList(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ITEMS,
                                                   tc.FACETS, Optional.of(tc.PAGING_STATE_ID)))
            .thenReturn(tc.ARTIST_LIST_BO);
        PowerMock.replayAll();

        /* *********************************** */
        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<ArtistList> results = catalogEdgeService.browseArtists(tc.PAGING_STATE_ID.toString(), tc.ITEMS,
                                                                          tc.FACETS, tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertNull(results.toBlocking().first().getArtists());
    }

    @Test
    public void testGetArtist() {
        Mockito.when(artistsService.getArtist(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ARTIST_ID))
            .thenReturn(Optional.of(tc.ARTIST_BO));
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<ArtistInfo>> results = catalogEdgeService.getArtist(tc.ARTIST_ID.toString(),
                                                                                tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertEquals(results.toBlocking().first().get().getArtistId(), tc.ARTIST_ID.toString());
        assertEquals(results.toBlocking().first().get().getArtistName(), tc.ARTIST_NAME);
    }

    @Test
    public void testGetNullArtist() {
        Mockito.when(artistsService.getArtist(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ARTIST_ID))
            .thenReturn(Optional.absent());
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<ArtistInfo>> results = catalogEdgeService.getArtist(tc.ARTIST_ID.toString(),
                                                                                tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertFalse(results.toBlocking().first().isPresent());
    }

    // =========================================================================================================
    // SONGS
    // =================================================================================================================

    @Test
    public void testBrowseSongs() {
        Mockito.when(songsService.getSongsList(cassandraCatalogService, Optional.of(tc.USER_ID), tc.ITEMS, tc.FACETS,
                                               Optional.of(tc.PAGING_STATE_ID))).thenReturn(tc.SONG_LIST_BO);
        PowerMock.replayAll();

        /* *********************************** */
        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<SongList> results = catalogEdgeService.browseSongs(tc.PAGING_STATE_ID.toString(), tc.ITEMS,
                                                                      tc.FACETS, tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertNull(results.toBlocking().first().getSongs());
    }

    @Test
    public void testGetSong() {
        Mockito.when(songsService.getSong(cassandraCatalogService, Optional.of(tc.USER_ID), tc.SONG_ID))
            .thenReturn(Optional.of(tc.SONG_BO));
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<SongInfo>> results = catalogEdgeService.getSong(tc.SONG_ID.toString(),
                                                                            tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertEquals(results.toBlocking().first().get().getArtistId(), tc.ARTIST_ID.toString());
        assertEquals(results.toBlocking().first().get().getArtistName(), tc.ARTIST_NAME);
        assertEquals(results.toBlocking().first().get().getAlbumId(), tc.ALBUM_ID.toString());
        assertEquals(results.toBlocking().first().get().getSongId(), tc.SONG_ID.toString());
    }

    @Test
    public void testGetNullSong() {
        Mockito.when(songsService.getSong(cassandraCatalogService, Optional.of(tc.USER_ID), tc.SONG_ID))
            .thenReturn(Optional.absent());
        PowerMock.replayAll();

        /* *********************************** */

        CatalogEdgeService catalogEdgeService = new CatalogEdgeService(albumsService, artistsService, songsService);

        Observable<Optional<SongInfo>> results = catalogEdgeService.getSong(tc.SONG_ID.toString(),
                                                                            tc.USER_ID.toString());
        assertNotNull(results.toBlocking().first());
        assertFalse(results.toBlocking().first().isPresent());
    }

}
