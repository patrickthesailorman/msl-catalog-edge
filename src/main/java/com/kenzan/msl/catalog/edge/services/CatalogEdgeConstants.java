/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.kenzan.msl.catalog.client.dto.FeaturedAlbumsDto;
import com.kenzan.msl.catalog.client.dto.AlbumsByFacetDto;
import com.kenzan.msl.catalog.client.dto.FeaturedArtistsDto;
import com.kenzan.msl.catalog.client.dto.ArtistsByFacetDto;
import com.kenzan.msl.catalog.client.dto.FeaturedSongsDto;
import com.kenzan.msl.catalog.client.dto.SongsByFacetDto;
import com.kenzan.msl.common.dto.AbstractDto;

/**
 *
 *
 * @author kenzan
 */
public class CatalogEdgeConstants {

  public static final int MSL_BROWSE_MIN_PAGE_SIZE = 1;
  public static final int MSL_BROWSE_MAX_PAGE_SIZE = 100;
  public static final int MSL_BROWSE_DEFAULT_PAGE_SIZE = 25;

  public static enum MSL_CONTENT_TYPE {
    ALBUM(FeaturedAlbumsDto.class, AlbumsByFacetDto.class), ARTIST(FeaturedArtistsDto.class,
        ArtistsByFacetDto.class), SONG(FeaturedSongsDto.class, SongsByFacetDto.class);

    public final Class<? extends AbstractDto> featuredContentDtoClass;
    public final Class<? extends AbstractDto> facetContentDtoClass;

    MSL_CONTENT_TYPE(Class<? extends AbstractDto> featuredContentDtoClass,
        Class<? extends AbstractDto> facetContentDtoClass) {
      this.featuredContentDtoClass = featuredContentDtoClass;
      this.facetContentDtoClass = facetContentDtoClass;
    }

  }
}
