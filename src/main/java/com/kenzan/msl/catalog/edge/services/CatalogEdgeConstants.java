/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.kenzan.msl.catalog.client.dao.*;
import com.kenzan.msl.common.dao.AbstractDao;

/**
 *
 *
 * @author billschwanitz
 */
public class CatalogEdgeConstants {

    public static final String MSL_KEYSPACE = "msl";

    public static final int MSL_BROWSE_MIN_PAGE_SIZE = 1;
    public static final int MSL_BROWSE_MAX_PAGE_SIZE = 100;
    public static final int MSL_BROWSE_DEFAULT_PAGE_SIZE = 25;

    public static enum MSL_CONTENT_TYPE {
        ALBUM(FeaturedAlbumsDao.class, AlbumsByFacetDao.class), ARTIST(FeaturedArtistsDao.class,
            ArtistsByFacetDao.class), SONG(FeaturedSongsDao.class, SongsByFacetDao.class);

        public final Class<? extends AbstractDao> featuredContentDaoClass;
        public final Class<? extends AbstractDao> facetContentDaoClass;

        MSL_CONTENT_TYPE( Class<? extends AbstractDao> featuredContentDaoClass,
                          Class<? extends AbstractDao> facetContentDaoClass ) {
            this.featuredContentDaoClass = featuredContentDaoClass;
            this.facetContentDaoClass = facetContentDaoClass;
        }

    }
}
