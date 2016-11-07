package com.kenzan.msl.catalog.edge.config;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.kenzan.msl.catalog.client.config.CatalogDataClientModule;
import com.netflix.governator.guice.LifecycleInjector;

/**
 * @author Kenzan
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
                .withModules(
                    new RestModule(),
                    new CatalogDataClientModule(),
                    new CatalogEdgeModule())
                .build()
                .createInjector();
    }
}
