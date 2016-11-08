package com.kenzan.msl.catalog.edge;

import com.google.inject.Injector;
import com.kenzan.msl.catalog.client.config.CatalogDataClientModule;
import com.kenzan.msl.catalog.edge.config.CatalogEdgeModule;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import io.swagger.api.CatalogEdgeApi;
import io.swagger.api.impl.CatalogEdgeApiOriginFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class Main {

  /**
   * Runs jetty server to expose jersey API
   *
   * @param args String array
   * @throws Exception if server doesn't start
   */
  public static void main(String[] args) throws Exception {
    Injector injector =  LifecycleInjector.builder()
            .withModules(
                    new CatalogDataClientModule(),
                    new CatalogEdgeModule())
            .build()
            .createInjector();

    LifecycleManager manager = injector.getInstance(LifecycleManager.class);

    Server jettyServer = new Server(9003);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    context.addFilter(CatalogEdgeApiOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    jettyServer.setHandler(context);

    ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
    jerseyServlet.setInitOrder(0);

    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
        CatalogEdgeApi.class.getCanonicalName());

    try {
      manager.start();
      jettyServer.start();
      jettyServer.join();
    } finally {
      manager.close();
      jettyServer.destroy();
    }
  }
}
