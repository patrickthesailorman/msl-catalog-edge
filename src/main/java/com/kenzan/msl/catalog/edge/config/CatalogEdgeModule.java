package com.kenzan.msl.catalog.edge.config;

import com.google.inject.AbstractModule;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.account.client.services.AccountDataClientServiceImpl;
import com.kenzan.msl.catalog.edge.services.*;
import com.kenzan.msl.catalog.edge.services.impl.*;
import com.kenzan.msl.ratings.client.services.RatingsDataClientService;
import com.kenzan.msl.ratings.client.services.RatingsDataClientServiceImpl;
import com.netflix.governator.guice.lazy.LazySingletonScope;
import io.swagger.api.CatalogEdgeApiService;
import io.swagger.api.impl.CatalogEdgeApiServiceImpl;

/**
 * @author Kenzan
 */
public class CatalogEdgeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RatingsDataClientService.class).to(RatingsDataClientServiceImpl.class).in(LazySingletonScope.get());
        bind(AccountDataClientService.class).to(AccountDataClientServiceImpl.class).in(LazySingletonScope.get());

        bind(AlbumService.class).to(AlbumsServiceImpl.class).in(LazySingletonScope.get());
        bind(ArtistService.class).to(ArtistsServiceImpl.class).in(LazySingletonScope.get());
        bind(SongService.class).to(SongsServiceImpl.class).in(LazySingletonScope.get());
        bind(LibraryHelper.class).to(LibraryHelperImpl.class).in(LazySingletonScope.get());

        bind(CatalogEdgeService.class).to(CatalogEdgeServiceImpl.class).in(LazySingletonScope.get());
        bind(CatalogEdgeApiService.class).to(CatalogEdgeApiServiceImpl.class).in(LazySingletonScope.get());
    }
}
