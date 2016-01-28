package io.swagger.client;

import io.swagger.api.impl.CatalogEdgeApiResponseMessage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SongClient {

    private ResteasyClient client;

    public SongClient() {
        client = new ResteasyClientBuilder().build();
    }

    public CatalogEdgeApiResponseMessage get(String id) {

        ResteasyWebTarget target = client.target(ClientConstants.BASE_URL + "/catalog-edge/");
        Response response = target.path("song/" + id).request().get();

        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.readEntity(CatalogEdgeApiResponseMessage.class);
    }

    public CatalogEdgeApiResponseMessage browse(String items) {
        WebTarget target;
        target = client.target(ClientConstants.BASE_URL + "/catalog-edge/browse/song?items=" + items);
        Response response = target.request().get();

        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return response.readEntity(CatalogEdgeApiResponseMessage.class);
    }

}
