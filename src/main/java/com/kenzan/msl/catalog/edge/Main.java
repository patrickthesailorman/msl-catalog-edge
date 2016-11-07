package com.kenzan.msl.catalog.edge;

import com.google.inject.Injector;
import com.kenzan.msl.catalog.client.config.CatalogDataClientModule;
import com.kenzan.msl.catalog.edge.config.CatalogEdgeModule;
import com.kenzan.msl.catalog.edge.config.RestModule;
import com.netflix.governator.guice.LifecycleInjector;
import io.swagger.api.CatalogEdgeApi;
import io.swagger.api.impl.CatalogEdgeApiOriginFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class Main {

  public static Injector injector;
  /**
   * Runs jetty server to expose jersey API
   *
   * @param args String array
   * @throws Exception if server doesn't start
   */
  public static void main(String[] args) throws Exception {
    // TODO
    injector =  LifecycleInjector.builder()
            .withModules(
                    new RestModule(),
                    new CatalogDataClientModule(),
                    new CatalogEdgeModule())
            .build()
            .createInjector();

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
      jettyServer.start();
      jettyServer.join();

    } finally {
      jettyServer.destroy();
    }
  }
}
