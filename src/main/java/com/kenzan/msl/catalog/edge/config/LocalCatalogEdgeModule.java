package com.kenzan.msl.catalog.edge.config;


import com.google.inject.AbstractModule;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.account.client.services.AccountDataClientServiceStub;
import com.kenzan.msl.catalog.client.services.CatalogDataClientService;
import com.kenzan.msl.catalog.client.services.CatalogDataClientServiceStub;
import com.kenzan.msl.catalog.edge.services.*;
import com.kenzan.msl.catalog.edge.services.impl.*;
import com.kenzan.msl.catalog.edge.services.stub.StubAlbumService;
import com.kenzan.msl.catalog.edge.services.stub.StubArtistService;
import com.kenzan.msl.catalog.edge.services.stub.StubCatalogEdgeService;
import com.kenzan.msl.catalog.edge.services.stub.StubSongService;
import com.kenzan.msl.ratings.client.services.RatingsDataClientService;
import com.kenzan.msl.ratings.client.services.RatingsDataClientServiceStub;
import com.netflix.governator.guice.lazy.LazySingletonScope;
import io.swagger.api.CatalogEdgeApiService;
import io.swagger.api.factories.CatalogEdgeApiServiceFactory;
import io.swagger.api.impl.CatalogEdgeApiServiceImpl;

public class LocalCatalogEdgeModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(CatalogEdgeApiServiceFactory.class);
        bind(RatingsDataClientService.class).to(RatingsDataClientServiceStub.class).in(LazySingletonScope.get());
        bind(AccountDataClientService.class).to(AccountDataClientServiceStub.class).in(LazySingletonScope.get());

        bind(AlbumService.class).to(StubAlbumService.class).in(LazySingletonScope.get());
        bind(ArtistService.class).to(StubArtistService.class).in(LazySingletonScope.get());
        bind(SongService.class).to(StubSongService.class).in(LazySingletonScope.get());
        bind(LibraryHelper.class).to(LibraryHelperImpl.class).in(LazySingletonScope.get());

        bind(CatalogEdgeService.class).to(StubCatalogEdgeService.class).in(LazySingletonScope.get());
        bind(CatalogEdgeApiService.class).to(CatalogEdgeApiServiceImpl.class).in(LazySingletonScope.get());
    }
}
