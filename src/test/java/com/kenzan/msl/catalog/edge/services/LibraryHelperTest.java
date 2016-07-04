package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.catalog.edge.services.impl.LibraryHelper;
import org.easymock.EasyMock;
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
@PrepareForTest({ResultSet.class, CassandraAccountService.class})
public class LibraryHelperTest extends TestConstants {

  private CassandraAccountService cassandraAccountService;
  private Observable<ResultSet> observableResultSet;

  @Before
  public void init() throws Exception {
    ResultSet resultSet = createMock(ResultSet.class);
    observableResultSet = Observable.just(resultSet);

    PowerMock.mockStatic(CassandraAccountService.class);
    cassandraAccountService = createMock(CassandraAccountService.class);
    PowerMock.expectNew(CassandraAccountService.class).andReturn(cassandraAccountService);

    expect(CassandraAccountService.getInstance(EasyMock.anyObject())).andReturn(
        cassandraAccountService).anyTimes();
  }

  @Test
  public void testGetUserArtists() {
    Result<ArtistsByUserDto> artistsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(cassandraAccountService.getArtistsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);

    expect(cassandraAccountService.mapArtistByUser(observableResultSet)).andReturn(
        Observable.just(artistsByUserDtoResult));

    replay(cassandraAccountService);
    PowerMock.replayAll();

    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    Result<ArtistsByUserDto> result = lh.getUserArtists(USER_ID);
    assertEquals(result, artistsByUserDtoResult);
  }

  @Test
  public void testGetUserAlbums() {
    Result<AlbumsByUserDto> albumsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(cassandraAccountService.getAlbumsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);
    expect(cassandraAccountService.mapAlbumsByUser(observableResultSet)).andReturn(
        Observable.just(albumsByUserDtoResult));
    replay(cassandraAccountService);
    PowerMock.replayAll();

    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    Result<AlbumsByUserDto> result = lh.getUserAlbums(USER_ID);
    assertEquals(result, albumsByUserDtoResult);
  }

  @Test
  public void testGetUserSongs() {
    Result<SongsByUserDto> songsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(cassandraAccountService.getSongsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);

    expect(cassandraAccountService.mapSongsByUser(observableResultSet)).andReturn(
        Observable.just(songsByUserDtoResult));
    replay(cassandraAccountService);
    PowerMock.replayAll();

    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    Result<SongsByUserDto> result = lh.getUserSongs(USER_ID);
    assertEquals(result, songsByUserDtoResult);
  }

  @Test
  public void testProcessLibraryAlbumInfo() {
    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    lh.processLibraryAlbumInfo(albumsByUserDtoList, ALBUM_BO);
    assertTrue(ALBUM_BO.isInMyLibrary());
    assertEquals(ALBUM_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

  @Test
  public void testProcessLibraryArtistInfo() {
    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    lh.processLibraryArtistInfo(artistsByUserDtoList, ARTIST_BO);
    assertTrue(ARTIST_BO.isInMyLibrary());
    assertEquals(ARTIST_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

  @Test
  public void testProcessLibrarySongInfo() {
    LibraryHelper lh = new LibraryHelper(cassandraAccountService);
    lh.processLibrarySongInfo(songsByUserDtoList, SONG_BO);
    assertTrue(SONG_BO.isInMyLibrary());
    assertEquals(SONG_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

}
