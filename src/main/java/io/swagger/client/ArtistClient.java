package io.swagger.client;

import io.swagger.api.impl.CatalogEdgeApiResponseMessage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ArtistClient {

    private ResteasyClient client;

    public ArtistClient() {
        client = new ResteasyClientBuilder().build();
    }

    /**
     * Fetch an Artist from its ID
     * 
     * @param id String
     * @return CatalogEdgeApiResponseMessage
     */
    public CatalogEdgeApiResponseMessage get(String id) {

        ResteasyWebTarget target = client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/");
        Response response = target.path("artist/" + id).request().get();

        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.readEntity(CatalogEdgeApiResponseMessage.class);
    }

    /**
     * Browse through the artists using query items
     * 
     * @param items String
     * @return CatalogEdgeApiResponseMessage
     */
    public CatalogEdgeApiResponseMessage browse(String items) {
        ResteasyWebTarget target;
        target = client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/browse/artist?items=" + items);
        Response response = target.request().get();

        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.readEntity(CatalogEdgeApiResponseMessage.class);
    }

}
