package com.kenzan.msl.catalog.edge.config;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import io.swagger.api.CatalogEdgeApi;
import io.swagger.jaxrs.config.BeanConfig;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Kenzan
 */
public class NettyServer {

    private static Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private static  DynamicBooleanProperty ASYNC_SERVICE_ENABLED =
            DynamicPropertyFactory.getInstance().getBooleanProperty("netty.asyncJobServiceEnabled", false);
    private static  DynamicIntProperty ASYNC_SERVICE_MAX_JOB_RESULTS =
            DynamicPropertyFactory.getInstance().getIntProperty("netty.asyncJobServiceMaxJobResults", 100);
    private static  DynamicIntProperty ASYNC_SERVICE_MAX_WAIT =
            DynamicPropertyFactory.getInstance().getIntProperty("netty.asyncJobServiceMaxWait", 300000);
    private static  DynamicIntProperty ASYNC_SERVICE_THREAD_POOL_SIZE =
            DynamicPropertyFactory.getInstance().getIntProperty("netty.asyncJobServiceThreadPoolSize", 100);
    private static  DynamicIntProperty SERVER_PORT =
            DynamicPropertyFactory.getInstance().getIntProperty("netty.port", 9003);

    private static  DynamicStringProperty SWAGGER_VERSION =
            DynamicPropertyFactory.getInstance().getStringProperty("swagger.version", "1.0.0");
    private static  DynamicStringProperty SWAGGER_HOST =
            DynamicPropertyFactory.getInstance().getStringProperty("swagger.host", "localhost");
    private static  DynamicIntProperty SWAGGER_PORT =
            DynamicPropertyFactory.getInstance().getIntProperty("swagger.port", 8081);
    private static  DynamicStringProperty SWAGGER_TITLE =
            DynamicPropertyFactory.getInstance().getStringProperty("swagger.title", "");

    @Inject
    private static CatalogEdgeApi service;

    /**
     * Initializes the netty server
     */
    public static void init() {
        ResteasyDeployment deployment = new ResteasyDeployment();
        initSwagger(deployment);

        deployment.setAsyncJobServiceEnabled(ASYNC_SERVICE_ENABLED.get());
        deployment.setAsyncJobServiceMaxJobResults(ASYNC_SERVICE_MAX_JOB_RESULTS.get());
        deployment.setAsyncJobServiceMaxWait(ASYNC_SERVICE_MAX_WAIT.get());
        deployment.setAsyncJobServiceThreadPoolSize(ASYNC_SERVICE_THREAD_POOL_SIZE.get());

        int nettyPort = SERVER_PORT.get();

        deployment.setResources(Arrays.<Object>asList(service));

        NettyJaxrsServer netty = new NettyJaxrsServer();
        netty.setDeployment(deployment);
        netty.setPort(nettyPort);
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();
    }

    /**
     * Initializes the swagger docs
     * @param deployment
     */
    private static void initSwagger(ResteasyDeployment deployment) {
        BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setVersion(SWAGGER_VERSION.get());
        swaggerConfig.setBasePath("http://" + SWAGGER_HOST.get() + ":" + SWAGGER_PORT.get());
        swaggerConfig.setTitle(SWAGGER_TITLE.get());
        swaggerConfig.setScan(true);
        swaggerConfig.setResourcePackage("io.swagger.api");

        deployment.setProviderClasses(Lists.newArrayList(
                "io.swagger.jaxrs.listing.ApiListingResource",
                "io.swagger.jaxrs.listing.SwaggerSerializers"));
        deployment.setSecurityEnabled(false);
    }
}
