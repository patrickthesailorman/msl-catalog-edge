package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.catalog.client.dto.SongsAlbumsByArtistDto;
import com.kenzan.msl.catalog.client.services.CassandraCatalogService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.catalog.edge.services.impl.ArtistsServiceImpl;
import com.kenzan.msl.catalog.edge.services.impl.LibraryHelper;
import com.kenzan.msl.catalog.edge.services.impl.Paginator;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.ContentType;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
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
public class ArtistsServiceImplTest extends TestConstants {


  @Mock
  private Result<SongsAlbumsByArtistDto> songsAlbumsByArtistDtos;
  @Mock
  private Result<ArtistsByUserDto> artistsByUserDtos;
  @Mock
  private ArtistListBo artistListBo;
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
  private ArtistsServiceImpl artistsServiceImpl;

  @Before
  public void init() throws Exception {
    PowerMockito.mockStatic(Translators.class);
  }

  @Test
  public void getArtistTest() {
    Mockito.when(cassandraCatalogService.getSongsAlbumsByArtist(ARTIST_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapSongsAlbumsByArtist(anyObject())).thenReturn(
        Observable.just(songsAlbumsByArtistDtos));
    Mockito.when(songsAlbumsByArtistDtos.one()).thenReturn(songsAlbumsByArtistDto);

    Mockito.when(libraryHelper.getUserArtists(eq(USER_ID))).thenReturn(artistsByUserDtos);
    Mockito.when(cassandraRatingsService.getAverageRating(ARTIST_ID, ContentType.ARTIST.value))
        .thenReturn(
            Observable.just(Optional.of(getMockAverageRatingsDto(ARTIST_ID,
                ContentType.ARTIST.value))));
    Mockito.when(
        cassandraRatingsService.getUserRating(USER_ID, ContentType.ARTIST.value, ARTIST_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(ARTIST_ID, ContentType.ARTIST.value))));

    Optional<ArtistBo> response = artistsServiceImpl.getArtist(Optional.of(USER_ID), ARTIST_ID);

    Mockito.verify(libraryHelper, times(1)).processLibraryArtistInfo(anyObject(), anyObject());
    assertTrue(response.get().getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
    assertEquals(response.get().getPersonalRating(), Integer.valueOf(10));
  }

  @Test
  public void getArtistTestEmptyMappingResults() {
    Mockito.when(cassandraCatalogService.getSongsAlbumsByArtist(ARTIST_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapSongsAlbumsByArtist(anyObject())).thenReturn(
        Observable.just(null));
    Optional<ArtistBo> response = artistsServiceImpl.getArtist(Optional.of(USER_ID), ARTIST_ID);
    assertFalse(response.isPresent());
  }

  @Test
  public void getAlbumTestEmptyMappingResults2() {
    Mockito.when(cassandraCatalogService.getSongsAlbumsByArtist(ARTIST_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(cassandraCatalogService.mapSongsAlbumsByArtist(anyObject())).thenReturn(
        Observable.just(songsAlbumsByArtistDtos));
    Mockito.when(songsAlbumsByArtistDtos.one()).thenReturn(null);
    Optional<ArtistBo> response = artistsServiceImpl.getArtist(Optional.of(USER_ID), ARTIST_ID);
    assertFalse(response.isPresent());
  }

  @Test
  @Ignore
  public void getArtistListTest() throws Exception {

    PowerMockito.whenNew(ArtistListBo.class).withAnyArguments().thenReturn(artistListBo);
    Mockito.when(artistListBo.getBoList()).thenReturn(artistBoList);

    PowerMockito.whenNew(Paginator.class).withAnyArguments().thenReturn(paginator);

    Mockito.when(libraryHelper.getUserArtists(eq(USER_ID))).thenReturn(artistsByUserDtos);

    PowerMockito.when(Translators.translateArtistsByUserDto(artistsByUserDtos)).thenReturn(
        artistsByUserDtoList);

    Mockito.when(cassandraRatingsService.getAverageRating(ALBUM_ID, ContentType.ARTIST.value))
        .thenReturn(
            Observable.just(Optional.of(getMockAverageRatingsDto(ARTIST_ID,
                ContentType.ARTIST.value))));
    Mockito
        .when(cassandraRatingsService.getUserRating(USER_ID, ContentType.ARTIST.value, ALBUM_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(ARTIST_ID, ContentType.ARTIST.value))));

    ArtistListBo result =
        artistsServiceImpl.getArtistsList(Optional.of(USER_ID), 10, "", Optional.absent());
    Mockito.verify(libraryHelper, times(1)).processLibraryAlbumInfo(anyObject(), anyObject());

    for (ArtistBo artistBo : result.getBoList()) {
      assertTrue(artistBo.getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
      assertEquals(artistBo.getPersonalRating(), Integer.valueOf(10));
    }
  }

  @Test
  public void prepareFacetedQueryTest() {
    artistsServiceImpl.prepareFacetedQuery(queryAccessor, FACETS);
    Mockito.verify(queryAccessor, times(1)).artistsByFacet(FACETS);
  }

  @Test
  public void prepareFeaturedQueryTest() {
    artistsServiceImpl.prepareFeaturedQuery(queryAccessor);
    Mockito.verify(queryAccessor, times(1)).featuredArtists();
  }

  @Test
  public void getFacetedQueryStringTest() {
    String response = artistsServiceImpl.getFacetedQueryString(FACETS);
    assertTrue(response.contains(FACETS));
  }

  @Test
  public void getFeaturedQueryStringTest() {
    String response = artistsServiceImpl.getFeaturedQueryString();
    assertFalse(response.isEmpty());
  }

}
