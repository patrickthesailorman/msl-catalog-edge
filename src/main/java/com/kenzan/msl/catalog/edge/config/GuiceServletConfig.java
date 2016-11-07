package com.kenzan.msl.catalog.edge.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.kenzan.msl.catalog.client.config.CatalogDataClientModule;

/**
 * @author Kenzan
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        // TODO replace Guice.createInjector for bootstrap LifecycleInjector
//        return LifecycleInjector.builder()
//                .withModules(
//                    new RestModule(),
//                    new CatalogDataClientModule(),
//                    new CatalogEdgeModule())
//                .build()
//                .createInjector();

//        LifecycleManager manager = injector.getInstance(LifecycleManager.class);
//        manager.start();

//        return LifecycleInjector.bootstrap(Bootstrap.class);
        return Guice.createInjector(
                new CatalogDataClientModule(),
                new CatalogEdgeModule(),
                new RestModule());
    }
}
