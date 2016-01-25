package io.swagger.client;

import io.swagger.api.impl.CatalogEdgeApiResponseMessage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.Response;

public class FacetClient {

    private String baseUrl = "http://localhost:9003/msl";
    private ResteasyClient client;

    public FacetClient() {
        client = new ResteasyClientBuilder().build();
    }

    public CatalogEdgeApiResponseMessage getFacets(String facets) {
        ResteasyWebTarget target;
        target = client.target(baseUrl + "/v1/catalogedge/facet/" + facets);
        Response response = target.request().get();
        CatalogEdgeApiResponseMessage responseWrapper = response.readEntity(CatalogEdgeApiResponseMessage.class);
        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return responseWrapper;
    }

    public CatalogEdgeApiResponseMessage browseAlbums(String items, String facets) {
        ResteasyWebTarget target;
        target = client.target(baseUrl + "/v1/catalogedge/browse/album?items=" + items + "&facets=" + facets);
        Response response = target.request().get();
        CatalogEdgeApiResponseMessage responseWrapper = response.readEntity(CatalogEdgeApiResponseMessage.class);
        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return responseWrapper;
    }

    public CatalogEdgeApiResponseMessage browseArtists(String items, String facets) {
        ResteasyWebTarget target;
        target = client.target(baseUrl + "/v1/catalogedge/browse/artist?items=" + items + "&facets=" + facets);
        Response response = target.request().get();
        CatalogEdgeApiResponseMessage responseWrapper = response.readEntity(CatalogEdgeApiResponseMessage.class);
        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return responseWrapper;
    }

    public CatalogEdgeApiResponseMessage browseSongs(String items, String facets) {
        ResteasyWebTarget target;
        target = client.target(baseUrl + "/v1/catalogedge/browse/song?items=" + items + "&facets=" + facets);
        Response response = target.request().get();
        CatalogEdgeApiResponseMessage responseWrapper = response.readEntity(CatalogEdgeApiResponseMessage.class);
        if ( response.getStatus() != 200 ) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return responseWrapper;
    }

}
