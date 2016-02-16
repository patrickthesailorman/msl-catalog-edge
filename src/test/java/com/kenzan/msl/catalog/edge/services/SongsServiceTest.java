package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.AlbumArtistBySongDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.ratings.client.dto.AverageRatingsDto;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import java.util.UUID;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CassandraRatingsService.class, CassandraAccountService.class, CassandraCatalogService.class })
public class SongsServiceTest {
    private TestConstants tc = TestConstants.getInstance();

    private CassandraCatalogService cassandraCatalogService;

    private CassandraRatingsService cassandraRatingsService;
    private CassandraAccountService cassandraAccountService;
    private Observable<ResultSet> observableResultSet;

    @Mock
    private LibraryHelper libraryhelper;
    @Mock
    private QueryAccessor queryAccessor;
    @InjectMocks
    private SongsService songsService = new SongsService();

    @Before
    public void init()
        throws Exception {
        ResultSet resultSet = createMock(ResultSet.class);
        observableResultSet = Observable.just(resultSet);
        queryAccessor = mock(QueryAccessor.class);

        PowerMock.mockStatic(CassandraCatalogService.class);
        cassandraCatalogService = createMock(CassandraCatalogService.class);

        PowerMock.mockStatic(CassandraAccountService.class);
        cassandraAccountService = createMock(CassandraAccountService.class);
        PowerMock.expectNew(CassandraAccountService.class).andReturn(cassandraAccountService);

        expect(CassandraAccountService.getInstance()).andReturn(cassandraAccountService).anyTimes();
    }

    @Test
    public void testGetSong()
        throws Exception {
        expect(cassandraCatalogService.getAlbumArtistBySong(tc.SONG_ID, Optional.absent()))
            .andReturn(observableResultSet);

        Result<AlbumArtistBySongDto> albumArtistBySongDtoResult = PowerMockito.mock(Result.class);
        expect(cassandraCatalogService.mapAlbumArtistBySong(observableResultSet))
            .andReturn(Observable.just(albumArtistBySongDtoResult));

        PowerMockito.when(albumArtistBySongDtoResult.one()).thenReturn(tc.albumArtistBySongDto);

        mockRatingsHelper();

        AverageRatingsDto averageRatingsDto = new AverageRatingsDto();
        averageRatingsDto.setNumRating(new Long(2));
        averageRatingsDto.setSumRating(new Long(4));
        expect(cassandraRatingsService.getAverageRating(EasyMock.anyObject(UUID.class), EasyMock.anyString()))
            .andReturn(Observable.just(Optional.of(averageRatingsDto)));

        EasyMock.replay(cassandraRatingsService);
        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        /* *************************************************** */

        Optional<SongBo> results = songsService.getSong(cassandraCatalogService, Optional.absent(), tc.SONG_ID);
        assertNotNull(results);
        assertTrue(results.isPresent());
        assertEquals(results.get().getArtistId(), tc.ARTIST_ID);
        assertEquals(results.get().getAlbumId(), tc.ALBUM_ID);
        assertEquals(results.get().getSongId(), tc.SONG_ID);
        assertEquals(results.get().getAlbumName(), tc.ALBUM_NAME);
        assertEquals(results.get().getArtistName(), tc.ARTIST_NAME);
        assertEquals(results.get().getSongName(), tc.SONG_NAME);
        assertEquals(results.get().getAverageRating(), new Integer(2));
    }

    @Test
    public void testGetNullSong() {
        expect(cassandraCatalogService.getAlbumArtistBySong(tc.SONG_ID, Optional.absent()))
            .andReturn(observableResultSet);
        expect(cassandraCatalogService.mapAlbumArtistBySong(observableResultSet)).andReturn(Observable.just(null));

        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        Optional<SongBo> result = songsService.getSong(cassandraCatalogService, Optional.of(tc.USER_ID), tc.SONG_ID);
        assertEquals(result, Optional.absent());
    }

    @Test
    @Ignore
    public void testGetSongList() {
        songsService.getSongsList(cassandraCatalogService, Optional.absent(), tc.ITEMS, tc.FACETS,
                                  Optional.of(tc.PAGING_STATE_ID));
    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    @Test
    public void testPrepareFacetedQuery() {
        songsService.prepareFacetedQuery(queryAccessor, "~");
        verify(queryAccessor, atLeastOnce()).songsByFacet("~");
    }

    @Test
    public void testPrepareFeaturedQuery() {
        songsService.prepareFeaturedQuery(queryAccessor);
        verify(queryAccessor, atLeastOnce()).featuredSongs();
    }

    @Test
    public void testGetFacetedQueryString() {
        String returned = songsService.getFacetedQueryString("~");
        String expected = "SELECT * FROM songs_by_facet WHERE facet_name = '~' AND content_type = 'Song'";
        assertEquals(expected, returned);
    }

    @Test
    public void testGetFeaturedQueryString() {
        String returned = songsService.getFeaturedQueryString();
        String expected = "SELECT * FROM featured_songs WHERE hotness_bucket = 'Hotness01' AND content_type = 'Song'";
        assertEquals(expected, returned);
    }

    private void mockRatingsHelper()
        throws Exception {
        PowerMock.mockStatic(CassandraRatingsService.class);
        cassandraRatingsService = createMock(CassandraRatingsService.class);
        PowerMock.expectNew(CassandraRatingsService.class).andReturn(cassandraRatingsService);

        expect(CassandraRatingsService.getInstance()).andReturn(cassandraRatingsService).anyTimes();
    }
}
