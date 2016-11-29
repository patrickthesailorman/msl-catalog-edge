package com.kenzan.msl.catalog.edge.config;

import com.kenzan.msl.catalog.client.config.LocalCatalogDataClientModule;
import com.netflix.governator.guice.BootstrapBinder;
import com.netflix.karyon.server.ServerBootstrap;

/**
 * @author Kenzan
 */
public class LocalBootstrap extends ServerBootstrap {

    @Override
    protected void configureBootstrapBinder(BootstrapBinder binder) {
        binder.install(new LocalCatalogDataClientModule());
        binder.install(new LocalCatalogEdgeModule());
        binder.install(new RestModule());
    }
}
