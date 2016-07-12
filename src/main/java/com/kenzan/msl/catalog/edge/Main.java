package com.kenzan.msl.catalog.edge;

import com.google.common.base.Optional;
import io.swagger.api.CatalogEdgeApi;
import io.swagger.api.impl.CatalogEdgeApiOriginFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.netflix.governator.annotations.Modules;
import netflix.karyon.archaius.ArchaiusBootstrap;
import netflix.karyon.servo.KaryonServoModule;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;

import netflix.adminresources.resources.KaryonWebAdminModule;

@ArchaiusBootstrap
@Modules(include = {KaryonWebAdminModule.class, // Uncomment this to enable
    // WebAdmin
    // KaryonEurekaModule.class, // Uncomment this to enable Eureka client.
    KaryonServoModule.class})
public class Main {

  public static HashMap archaiusProperties = new HashMap<String, Optional<String>>();

  /**
   * Runs jetty server to expose jersey API
   *
   * @param args String array
   * @throws Exception if server doesn't start
   */
  public static void main(String[] args) throws Exception {

    archaiusProperties.put("region",
        Optional.fromNullable(System.getProperty("archaius.deployment.region")));
    archaiusProperties.put("domainName",
        Optional.fromNullable(System.getProperty("archaius.deployment.domainName")));

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
