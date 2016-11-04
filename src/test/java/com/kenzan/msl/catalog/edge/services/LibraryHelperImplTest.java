package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.edge.services.impl.LibraryHelperImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
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
@PrepareForTest({ResultSet.class, AccountDataClientService.class})
public class LibraryHelperImplTest extends TestConstants {

  private Observable<ResultSet> observableResultSet;

  @Mock
  private AccountDataClientService accountDataClientService;

  @Before
  public void init() throws Exception {
    ResultSet resultSet = createMock(ResultSet.class);
    observableResultSet = Observable.just(resultSet);
  }

  @Test
  public void testGetUserArtists() {
    Result<ArtistsByUserDto> artistsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(accountDataClientService.getArtistsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);

    expect(accountDataClientService.mapArtistByUser(observableResultSet)).andReturn(
        Observable.just(artistsByUserDtoResult));

    replay(accountDataClientService);
    PowerMock.replayAll();

    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    Result<ArtistsByUserDto> result = lh.getUserArtists(USER_ID);
    assertEquals(result, artistsByUserDtoResult);
  }

  @Test
  public void testGetUserAlbums() {
    Result<AlbumsByUserDto> albumsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(accountDataClientService.getAlbumsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);
    expect(accountDataClientService.mapAlbumsByUser(observableResultSet)).andReturn(
        Observable.just(albumsByUserDtoResult));
    replay(accountDataClientService);
    PowerMock.replayAll();

    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    Result<AlbumsByUserDto> result = lh.getUserAlbums(USER_ID);
    assertEquals(result, albumsByUserDtoResult);
  }

  @Test
  public void testGetUserSongs() {
    Result<SongsByUserDto> songsByUserDtoResult = PowerMockito.mock(Result.class);

    expect(accountDataClientService.getSongsByUser(USER_ID, Optional.absent(), Optional.absent()))
        .andReturn(observableResultSet);

    expect(accountDataClientService.mapSongsByUser(observableResultSet)).andReturn(
        Observable.just(songsByUserDtoResult));
    replay(accountDataClientService);
    PowerMock.replayAll();

    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    Result<SongsByUserDto> result = lh.getUserSongs(USER_ID);
    assertEquals(result, songsByUserDtoResult);
  }

  @Test
  public void testProcessLibraryAlbumInfo() {
    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    lh.processLibraryAlbumInfo(albumsByUserDtoList, ALBUM_BO);
    assertTrue(ALBUM_BO.isInMyLibrary());
    assertEquals(ALBUM_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

  @Test
  public void testProcessLibraryArtistInfo() {
    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    lh.processLibraryArtistInfo(artistsByUserDtoList, ARTIST_BO);
    assertTrue(ARTIST_BO.isInMyLibrary());
    assertEquals(ARTIST_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

  @Test
  public void testProcessLibrarySongInfo() {
    LibraryHelperImpl lh = new LibraryHelperImpl(accountDataClientService);
    lh.processLibrarySongInfo(songsByUserDtoList, SONG_BO);
    assertTrue(SONG_BO.isInMyLibrary());
    assertEquals(SONG_BO.getFavoritesTimestamp(), Long.toString(TIMESTAMP.getTime()));
  }

}
