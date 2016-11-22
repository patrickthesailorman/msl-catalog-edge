package com.kenzan.msl.catalog.edge.config;

import com.google.common.base.Throwables;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.karyon.server.KaryonServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import java.io.IOException;

/**
 * An extension of {@link KaryonServer} to hook on to the guice servlet module. In order for this to work you must have
 * the following entries in your web.xml for the webapp.
 *
 <PRE>
 &lt;filter&gt;
 &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
 &lt;filter-class&gt;com.google.inject.servlet.GuiceFilter&lt;/filter-class&gt;
 &lt;/filter&gt;
 &lt;filter-mapping&gt;
 &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
 &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 &lt;/filter-mapping&gt;
 &lt;listener&gt;
 &lt;listener-class&gt;com.kenzan.msl.catalog.edge.config.KaryonGuiceContextListener&lt;/listener-class&gt;
 &lt;/listener&gt;
 </PRE>
 *
 * @author Kenzan
 */
@SuppressWarnings("unused")
public class KaryonGuiceContextListener extends GuiceServletContextListener {

    protected static final Logger logger = LoggerFactory.getLogger(KaryonGuiceContextListener.class);

    private final KaryonServer server;

    @SuppressWarnings("unused")
    public KaryonGuiceContextListener() {
        server = new KaryonServer();
    }

    @Override
    protected Injector getInjector() {
        return server.initialize();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Error while starting karyon.", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
        try {
            server.close();
        } catch (IOException e) {
            logger.error("Error while stopping karyon.", e);
            throw Throwables.propagate(e);
        }
    }
}
