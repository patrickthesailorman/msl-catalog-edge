package com.kenzan.msl.catalog.edge;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.kenzan.msl.catalog.edge.config.NettyServer;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.karyon.server.KaryonServer;

public class Main {

    /**
     * Runs jetty server to expose jersey API and initializes Karyon server
     *
     * @param args String array
     * @throws Exception if server doesn't start
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("archaius.deployment.applicationId", "catalogedge");

        System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
        KaryonServer karyonServer = new KaryonServer();
        karyonServer.initialize();
        karyonServer.start();

        NettyServer.init();
        JmxReporter.forRegistry(new MetricRegistry()).build().start();
    }
}
