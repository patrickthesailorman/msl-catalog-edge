package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResultSet.class, CassandraAccountService.class })
public class LibraryHelperTest {

    private TestConstants tc = TestConstants.getInstance();
    private CassandraAccountService cassandraAccountService;
    private Observable<ResultSet> observableResultSet;

    @Before
    public void init()
        throws Exception {
        ResultSet resultSet = createMock(ResultSet.class);
        observableResultSet = Observable.just(resultSet);

        PowerMock.mockStatic(CassandraAccountService.class);
        cassandraAccountService = createMock(CassandraAccountService.class);
        PowerMock.expectNew(CassandraAccountService.class).andReturn(cassandraAccountService);

        expect(CassandraAccountService.getInstance()).andReturn(cassandraAccountService).anyTimes();
    }

    @Test
    public void testGetUserArtists() {
        Result<ArtistsByUserDto> artistsByUserDtoResult = PowerMockito.mock(Result.class);

        expect(cassandraAccountService.getArtistsByUser(tc.USER_ID, Optional.absent(), Optional.absent()))
            .andReturn(observableResultSet);

        expect(cassandraAccountService.mapArtistByUser(observableResultSet))
            .andReturn(Observable.just(artistsByUserDtoResult));

        replay(cassandraAccountService);
        PowerMock.replayAll();

        LibraryHelper lh = new LibraryHelper();
        Result<ArtistsByUserDto> result = lh.getUserArtists(tc.USER_ID);
        assertEquals(result, artistsByUserDtoResult);
    }

    @Test
    public void testGetUserAlbums() {
        Result<AlbumsByUserDto> albumsByUserDtoResult = PowerMockito.mock(Result.class);

        expect(cassandraAccountService.getAlbumsByUser(tc.USER_ID, Optional.absent(), Optional.absent()))
            .andReturn(observableResultSet);
        expect(cassandraAccountService.mapAlbumsByUser(observableResultSet))
            .andReturn(Observable.just(albumsByUserDtoResult));
        replay(cassandraAccountService);
        PowerMock.replayAll();

        LibraryHelper lh = new LibraryHelper();
        Result<AlbumsByUserDto> result = lh.getUserAlbums(tc.USER_ID);
        assertEquals(result, albumsByUserDtoResult);
    }

    @Test
    public void testGetUserSongs() {
        Result<SongsByUserDto> songsByUserDtoResult = PowerMockito.mock(Result.class);

        expect(cassandraAccountService.getSongsByUser(tc.USER_ID, Optional.absent(), Optional.absent()))
            .andReturn(observableResultSet);

        expect(cassandraAccountService.mapSongsByUser(observableResultSet)).andReturn(Observable
                                                                                          .just(songsByUserDtoResult));
        replay(cassandraAccountService);
        PowerMock.replayAll();

        LibraryHelper lh = new LibraryHelper();
        Result<SongsByUserDto> result = lh.getUserSongs(tc.USER_ID);
        assertEquals(result, songsByUserDtoResult);
    }

    @Test
    public void testProcessLibraryAlbumInfo() {
        LibraryHelper lh = new LibraryHelper();
        lh.processLibraryAlbumInfo(tc.albumsByUserDtoList, tc.ALBUM_BO);
        assertTrue(tc.ALBUM_BO.isInMyLibrary());
        assertEquals(tc.ALBUM_BO.getFavoritesTimestamp(), Long.toString(tc.TIMESTAMP.getTime()));
    }

    @Test
    public void testProcessLibraryArtistInfo() {
        LibraryHelper lh = new LibraryHelper();
        lh.processLibraryArtistInfo(tc.artistsByUserDtoList, tc.ARTIST_BO);
        assertTrue(tc.ARTIST_BO.isInMyLibrary());
        assertEquals(tc.ARTIST_BO.getFavoritesTimestamp(), Long.toString(tc.TIMESTAMP.getTime()));
    }

    @Test
    public void testProcessLibrarySongInfo() {
        LibraryHelper lh = new LibraryHelper();
        lh.processLibrarySongInfo(tc.songsByUserDtoList, tc.SONG_BO);
        assertTrue(tc.SONG_BO.isInMyLibrary());
        assertEquals(tc.SONG_BO.getFavoritesTimestamp(), Long.toString(tc.TIMESTAMP.getTime()));
    }

}
