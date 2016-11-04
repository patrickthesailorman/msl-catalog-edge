package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.catalog.client.dto.SongsArtistByAlbumDto;
import com.kenzan.msl.catalog.client.services.CatalogDataClientService;
import com.kenzan.msl.catalog.edge.TestConstants;
import com.kenzan.msl.catalog.edge.services.impl.AlbumsServiceImpl;
import com.kenzan.msl.catalog.edge.services.impl.LibraryHelperImpl;
import com.kenzan.msl.catalog.edge.services.impl.Paginator;
import com.kenzan.msl.catalog.edge.translate.Translators;
import com.kenzan.msl.common.ContentType;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.ratings.client.services.RatingsDataClientService;
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
public class AlbumsServiceImplTest extends TestConstants {


  @Mock
  private Result<SongsArtistByAlbumDto> songsArtistByAlbumDtos;
  @Mock
  private Result<AlbumsByUserDto> albumsByUserDtos;
  @Mock
  private AlbumListBo albumListBo;
  @Mock
  private QueryAccessor queryAccessor;
  @Mock
  private Paginator paginator;
  @Mock
  private ResultSet resultSet;

  @Mock
  private CatalogDataClientService catalogDataClientService;
  @Mock
  private RatingsDataClientService ratingsDataClientService;
  @Mock
  private LibraryHelperImpl libraryHelperImpl;
  @InjectMocks
  private AlbumsServiceImpl albumsServiceImpl;

  @Before
  public void init() throws Exception {
    PowerMockito.mockStatic(Translators.class);
  }

  @Test
  public void getAlbumTest() {
    Mockito.when(catalogDataClientService.getSongsArtistByAlbum(ALBUM_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(catalogDataClientService.mapSongsArtistByAlbum(anyObject())).thenReturn(
        Observable.just(songsArtistByAlbumDtos));
    Mockito.when(songsArtistByAlbumDtos.one()).thenReturn(songsArtistByAlbumDto);

    Mockito.when(libraryHelperImpl.getUserAlbums(eq(USER_ID))).thenReturn(albumsByUserDtos);
    Mockito.when(ratingsDataClientService.getAverageRating(ALBUM_ID, ContentType.ALBUM.value))
        .thenReturn(
            Observable.just(Optional
                .of(getMockAverageRatingsDto(ALBUM_ID, ContentType.ALBUM.value))));
    Mockito.when(ratingsDataClientService.getUserRating(USER_ID, ContentType.ALBUM.value, ALBUM_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(ALBUM_ID, ContentType.ALBUM.value))));

    Optional<AlbumBo> response = albumsServiceImpl.getAlbum(Optional.of(USER_ID), ALBUM_ID);

    Mockito.verify(libraryHelperImpl, times(1)).processLibraryAlbumInfo(anyObject(), anyObject());
    assertTrue(response.get().getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
    assertEquals(response.get().getPersonalRating(), Integer.valueOf(10));
  }

  @Test
  public void getAlbumTestEmptyMappingResults() {
    Mockito.when(catalogDataClientService.getSongsArtistByAlbum(ALBUM_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(catalogDataClientService.mapSongsArtistByAlbum(anyObject())).thenReturn(
        Observable.just(null));
    Optional<AlbumBo> response = albumsServiceImpl.getAlbum(Optional.of(USER_ID), ALBUM_ID);
    assertFalse(response.isPresent());
  }

  @Test
  public void getAlbumTestEmptyMappingResults2() {
    Mockito.when(catalogDataClientService.getSongsArtistByAlbum(ALBUM_ID, Optional.absent()))
        .thenReturn(Observable.just(resultSet));
    Mockito.when(catalogDataClientService.mapSongsArtistByAlbum(anyObject())).thenReturn(
        Observable.just(songsArtistByAlbumDtos));
    Mockito.when(songsArtistByAlbumDtos.one()).thenReturn(null);
    Optional<AlbumBo> response = albumsServiceImpl.getAlbum(Optional.of(USER_ID), ALBUM_ID);
    assertFalse(response.isPresent());
  }

  @Test
  @Ignore
  public void GetAlbumsListTest() throws Exception {

    PowerMockito.whenNew(AlbumListBo.class).withAnyArguments().thenReturn(albumListBo);
    Mockito.when(albumListBo.getBoList()).thenReturn(albumBoList);

    PowerMockito.whenNew(Paginator.class).withAnyArguments().thenReturn(paginator);

    Mockito.when(libraryHelperImpl.getUserAlbums(eq(USER_ID))).thenReturn(albumsByUserDtos);

    PowerMockito.when(Translators.translateAlbumsByUserDto(albumsByUserDtos)).thenReturn(
        albumsByUserDtoList);

    Mockito.when(ratingsDataClientService.getAverageRating(ALBUM_ID, ContentType.ALBUM.value))
        .thenReturn(
            Observable.just(Optional
                .of(getMockAverageRatingsDto(ALBUM_ID, ContentType.ALBUM.value))));
    Mockito.when(ratingsDataClientService.getUserRating(USER_ID, ContentType.ALBUM.value, ALBUM_ID))
        .thenReturn(
            Observable.just(Optional.of(getMockUserRatings(ALBUM_ID, ContentType.ALBUM.value))));

    AlbumListBo result =
        albumsServiceImpl.getAlbumsList(Optional.of(USER_ID), 10, "", Optional.absent());
    Mockito.verify(libraryHelperImpl, times(1)).processLibraryAlbumInfo(anyObject(), anyObject());

    for (AlbumBo albumBo : result.getBoList()) {
      assertTrue(albumBo.getAverageRating() == (int) (Long.valueOf(123) / Long.valueOf(123)));
      assertEquals(albumBo.getPersonalRating(), Integer.valueOf(10));
    }
  }

  @Test
  public void prepareFacetedQueryTest() {
    albumsServiceImpl.prepareFacetedQuery(queryAccessor, FACETS);
    Mockito.verify(queryAccessor, times(1)).albumsByFacet(FACETS);
  }

  @Test
  public void prepareFeaturedQueryTest() {
    albumsServiceImpl.prepareFeaturedQuery(queryAccessor);
    Mockito.verify(queryAccessor, times(1)).featuredAlbums();
  }

  @Test
  public void getFacetedQueryStringTest() {
    String response = albumsServiceImpl.getFacetedQueryString(FACETS);
    assertTrue(response.contains(FACETS));
  }

  @Test
  public void getFeaturedQueryStringTest() {
    String response = albumsServiceImpl.getFeaturedQueryString();
    assertFalse(response.isEmpty());
  }

}
