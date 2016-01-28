package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dao.SongsAlbumsByArtistDao;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.ratings.client.dao.AverageRatingsDao;
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
public class ArtistsServiceTest {

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
    private ArtistsService artistsService = new ArtistsService();

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
    public void testGetArtist()
        throws Exception {
        expect(cassandraCatalogService.getSongsAlbumsByArtist(tc.ALBUM_ID, Optional.absent()))
            .andReturn(observableResultSet);

        Result<SongsAlbumsByArtistDao> songsAlbumsByArtistDaoResult = PowerMockito.mock(Result.class);
        expect(cassandraCatalogService.mapSongsAlbumsByArtist(observableResultSet))
            .andReturn(Observable.just(songsAlbumsByArtistDaoResult));

        PowerMockito.when(songsAlbumsByArtistDaoResult.one()).thenReturn(tc.songsAlbumsByArtistDao);

        mockRatingsHelper();

        AverageRatingsDao averageRatingsDao = new AverageRatingsDao();
        averageRatingsDao.setNumRating(new Long(2));
        averageRatingsDao.setSumRating(new Long(4));
        expect(cassandraRatingsService.getAverageRating(EasyMock.anyObject(UUID.class), EasyMock.anyString()))
            .andReturn(Observable.just(averageRatingsDao));

        EasyMock.replay(cassandraRatingsService);
        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        /* *************************************************** */

        Optional<ArtistBo> results = artistsService.getArtist(cassandraCatalogService, Optional.absent(), tc.ALBUM_ID);
        assertNotNull(results);
        assertTrue(results.isPresent());
        assertEquals(results.get().getArtistId(), tc.ARTIST_ID);
        assertEquals(results.get().getArtistName(), tc.ARTIST_NAME);
        assertEquals(results.get().getAverageRating(), new Integer(2));
    }

    @Test
    public void testGetNullArtist() {
        expect(cassandraCatalogService.getSongsAlbumsByArtist(tc.ARTIST_ID, Optional.absent()))
            .andReturn(observableResultSet);
        expect(cassandraCatalogService.mapSongsAlbumsByArtist(observableResultSet)).andReturn(Observable.just(null));

        EasyMock.replay(cassandraCatalogService);
        EasyMock.replay(cassandraAccountService);
        PowerMock.replayAll();

        Optional<ArtistBo> result = artistsService.getArtist(cassandraCatalogService, Optional.of(tc.USER_ID),
                                                             tc.ARTIST_ID);
        assertEquals(result, Optional.absent());
    }

    @Test
    @Ignore
    public void testGetArtistList() {
        artistsService.getArtistsList(cassandraCatalogService, Optional.absent(), tc.ITEMS, tc.FACETS,
                                      Optional.of(tc.PAGING_STATE_ID));
    }

    // ================================================================================================================
    // PAGINATION HELPER METHODS
    // ================================================================================================================

    @Test
    public void testPrepareFacetedQuery() {
        artistsService.prepareFacetedQuery(queryAccessor, "~");
        verify(queryAccessor, atLeastOnce()).artistsByFacet("~");
    }

    @Test
    public void testPrepareFeaturedQuery() {
        artistsService.prepareFeaturedQuery(queryAccessor);
        verify(queryAccessor, atLeastOnce()).featuredArtists();
    }

    @Test
    public void testGetFacetedQueryString() {
        String returned = artistsService.getFacetedQueryString("~");
        String expected = "SELECT * FROM artists_by_facet WHERE facet_name = '~' AND content_type = 'Artist'";
        assertEquals(expected, returned);
    }

    @Test
    public void testGetFeaturedQueryString() {
        String returned = artistsService.getFeaturedQueryString();
        String expected = "SELECT * FROM featured_artists WHERE hotness_bucket = 'Hotness01' AND content_type = 'Artist'";
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
