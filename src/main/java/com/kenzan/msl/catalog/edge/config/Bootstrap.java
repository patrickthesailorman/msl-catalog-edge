package com.kenzan.msl.catalog.edge.config;

import com.kenzan.msl.account.client.config.AccountDataClientModule;
import com.kenzan.msl.catalog.client.config.CatalogDataClientModule;
import com.kenzan.msl.ratings.client.config.RatingsDataClientModule;
import com.netflix.governator.guice.BootstrapBinder;
import com.netflix.karyon.server.ServerBootstrap;

/**
 * @author Kenzan
 */
public class Bootstrap extends ServerBootstrap {

    @Override
    protected void configureBootstrapBinder(BootstrapBinder binder) {
        binder.install(new RestModule());
        binder.install(new CatalogDataClientModule());
        binder.install(new AccountDataClientModule());
        binder.install(new RatingsDataClientModule());
        binder.install(new CatalogEdgeModule());
    }
}
