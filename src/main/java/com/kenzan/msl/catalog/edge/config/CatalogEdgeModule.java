package com.kenzan.msl.catalog.edge.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.account.client.services.AccountDataClientServiceImpl;
import com.kenzan.msl.catalog.edge.services.*;
import com.kenzan.msl.catalog.edge.services.impl.*;
import com.kenzan.msl.ratings.client.services.RatingsDataClientService;
import com.kenzan.msl.ratings.client.services.RatingsDataClientServiceImpl;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.governator.guice.lazy.LazySingletonScope;
import io.swagger.api.CatalogEdgeApiService;
import io.swagger.api.factories.CatalogEdgeApiServiceFactory;
import io.swagger.api.impl.CatalogEdgeApiOriginFilter;
import io.swagger.api.impl.CatalogEdgeApiServiceImpl;
import io.swagger.api.impl.CatalogEdgeSessionToken;
import io.swagger.api.impl.CatalogEdgeSessionTokenImpl;

/**
 * Catalog Edge Module, a support class for Modules which reduces repetition and results in a more readable configuration
 * if no archaius.configurationSource.additionalUrls property is passed in, archaius uses default configuration. See readme to
 * understand how to pass in these variables
 *
 * @author Kenzan
 */
public class CatalogEdgeModule extends AbstractModule {

    private final String DEFAULT_CLIENT_PORT = "3000";

    private DynamicStringProperty CLIENT_PORT =
            DynamicPropertyFactory.getInstance().getStringProperty("clientPort", DEFAULT_CLIENT_PORT);

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("clientPort")).to(CLIENT_PORT.get());

        requestStaticInjection(CatalogEdgeApiServiceFactory.class);
        requestStaticInjection(CatalogEdgeApiOriginFilter.class);
        bind(CatalogEdgeSessionToken.class).to(CatalogEdgeSessionTokenImpl.class).in(LazySingletonScope.get());

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
