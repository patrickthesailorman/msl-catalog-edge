package com.kenzan.msl.catalog.edge.config;

import com.kenzan.msl.account.client.config.LocalAccountDataClientModule;
import com.kenzan.msl.catalog.client.config.LocalCatalogDataCientModule;
import com.kenzan.msl.ratings.client.config.LocalRatingsDataClientModule;
import com.netflix.governator.guice.BootstrapBinder;
import com.netflix.karyon.server.ServerBootstrap;

/**
 * @author Kenzan
 */
public class LocalBootstrap extends ServerBootstrap {

    @Override
    protected void configureBootstrapBinder(BootstrapBinder binder) {
        binder.install(new RestModule());
        binder.install(new LocalCatalogDataCientModule());
        binder.install(new LocalAccountDataClientModule());
        binder.install(new LocalRatingsDataClientModule());
        binder.install(new LocalCatalogEdgeModule());
    }
}
