package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.AlbumArtistBySongDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.ContentType;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;
import com.kenzan.msl.ratings.client.services.CassandraRatingsService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class SongsServiceTest extends TestConstants {

  @Mock
  private Result<AlbumArtistBySongDto> albumArtistBySongDtos;
  @Mock
  private Result<SongsByUserDto> songsByUserDtos;
  @Mock
  private SongListBo songListBo;
  @Mock
  private QueryAccessor queryAccessor;
  @Mock
  private Paginator paginator;
  @Mock
  private ResultSet resultSet;

  @Mock
  private CassandraCatalogService cassandraCatalogService;
  @Mock
  private CassandraRatingsService cassandraRatingsService;
  @Mock
  private LibraryHelper libraryHelper;
  @InjectMocks
  private SongsService songsService;

  @Before
  public void init() throws Exception {
    PowerMockito.mockStatic(Translators.class);
  }

  @Test
  public void getArtistTest() {
    Mockito.when(cassandraCatalogService.getAlbumArtistBySong(SONG_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapAlbumArtistBySong(anyObject())).thenReturn(
        Observable.just(albumArtistBySongDtos));
    Mockito.when(albumArtistBySongDtos.one()).thenReturn(albumArtistBySongDto);

    Mockito.when(libraryHelper.getUserSongs(eq(USER_ID))).thenReturn(songsByUserDtos);
    Mockito
        .when(cassandraRatingsService.getAverageRating(SONG_ID, ContentType.SONG.value))
        .thenReturn(
            Observable.just(Optional.of(getMockAverageRatingsDto(SONG_ID, ContentType.SONG.value))));
    Mockito.when(cassandraRatingsService.getUserRating(USER_ID, ContentType.SONG.value, SONG_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(SONG_ID, ContentType.SONG.value))));

    Optional<SongBo> response = songsService.getSong(Optional.of(USER_ID), SONG_ID);

    Mockito.verify(libraryHelper, times(1)).processLibrarySongInfo(anyObject(), anyObject());
    assertTrue(response.get().getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
    assertEquals(response.get().getPersonalRating(), Integer.valueOf(10));
  }

  @Test
  public void getArtistTestEmptyMappingResults() {
    Mockito.when(cassandraCatalogService.getAlbumArtistBySong(SONG_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapAlbumArtistBySong(anyObject())).thenReturn(
        Observable.just(null));
    Optional<SongBo> response = songsService.getSong(Optional.of(USER_ID), SONG_ID);
    assertFalse(response.isPresent());
  }

  @Test
  public void getAlbumTestEmptyMappingResults2() {
    Mockito.when(cassandraCatalogService.getAlbumArtistBySong(SONG_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapAlbumArtistBySong(anyObject())).thenReturn(
        Observable.just(albumArtistBySongDtos));
    Mockito.when(albumArtistBySongDtos.one()).thenReturn(null);
    Optional<SongBo> response = songsService.getSong(Optional.of(USER_ID), SONG_ID);
    assertFalse(response.isPresent());
  }

  @Test
  @Ignore
  public void getSongsListTest() throws Exception {
    PowerMockito.whenNew(SongListBo.class).withAnyArguments().thenReturn(songListBo);
    Mockito.when(songListBo.getBoList()).thenReturn(songBoList);

    PowerMockito.whenNew(Paginator.class).withAnyArguments().thenReturn(paginator);

    Mockito.when(libraryHelper.getUserSongs(eq(USER_ID))).thenReturn(songsByUserDtos);

    PowerMockito.when(Translators.translateSongsByUserDto(songsByUserDtos)).thenReturn(
        songsByUserDtoList);

    Mockito.when(cassandraRatingsService.getAverageRating(ALBUM_ID, ContentType.SONG.value))
        .thenReturn(
            Observable.just(Optional
                .of(getMockAverageRatingsDto(ARTIST_ID, ContentType.SONG.value))));
    Mockito.when(cassandraRatingsService.getUserRating(USER_ID, ContentType.SONG.value, ALBUM_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(ARTIST_ID, ContentType.SONG.value))));

    SongListBo result = songsService.getSongsList(Optional.of(USER_ID), 10, "", Optional.absent());
    Mockito.verify(libraryHelper, times(1)).processLibraryAlbumInfo(anyObject(), anyObject());

    for (SongBo songBo : result.getBoList()) {
      assertTrue(songBo.getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
      assertEquals(songBo.getPersonalRating(), Integer.valueOf(10));
    }
  }

  @Test
  public void prepareFacetedQueryTest() {
    songsService.prepareFacetedQuery(queryAccessor, FACETS);
    Mockito.verify(queryAccessor, times(1)).songsByFacet(FACETS);
  }

  @Test
  public void prepareFeaturedQueryTest() {
    songsService.prepareFeaturedQuery(queryAccessor);
    Mockito.verify(queryAccessor, times(1)).featuredSongs();
  }

  @Test
  public void getFacetedQueryStringTest() {
    String response = songsService.getFacetedQueryString(FACETS);
    assertTrue(response.contains(FACETS));
  }

  @Test
  public void getFeaturedQueryStringTest() {
    String response = songsService.getFeaturedQueryString();
    assertFalse(response.isEmpty());
  }
}
