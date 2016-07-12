package io.swagger.client;

import io.swagger.api.impl.CatalogEdgeApiResponseMessage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.Response;

/**
 * @author kenzan
 */
public class FacetClient {

  private ResteasyClient client;

  public FacetClient() {
    client = new ResteasyClientBuilder().build();
  }

  /**
   * Get stored facets
   * 
   * @param facets String
   * @return CatalogEdgeApiResponseMessage
   */
  public CatalogEdgeApiResponseMessage getFacets(String facets) {
    ResteasyWebTarget target;
    target =
        client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/facet/" + facets);
    Response response = target.request().get();
    CatalogEdgeApiResponseMessage responseWrapper =
        response.readEntity(CatalogEdgeApiResponseMessage.class);
    if (response.getStatus() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
    }
    return responseWrapper;
  }

  /**
   * Browse through the albums using query items and facet to filter
   * 
   * @param items String
   * @param facets String
   * @return CatalogEdgeApiResponseMessage
   */
  public CatalogEdgeApiResponseMessage browseAlbums(String items, String facets) {
    ResteasyWebTarget target;
    target =
        client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/browse/album?items="
            + items + "&facets=" + facets);
    Response response = target.request().get();
    CatalogEdgeApiResponseMessage responseWrapper =
        response.readEntity(CatalogEdgeApiResponseMessage.class);
    if (response.getStatus() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
    }
    return responseWrapper;
  }

  /**
   * Browse through the artists using query items and facet to filter
   * 
   * @param items String
   * @param facets String
   * @return CatalogEdgeApiResponseMessage
   */
  public CatalogEdgeApiResponseMessage browseArtists(String items, String facets) {
    ResteasyWebTarget target;
    target =
        client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/browse/artist?items="
            + items + "&facets=" + facets);
    Response response = target.request().get();
    CatalogEdgeApiResponseMessage responseWrapper =
        response.readEntity(CatalogEdgeApiResponseMessage.class);
    if (response.getStatus() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
    }
    return responseWrapper;
  }

  /**
   * Browse through the songs using query items and facet to filter
   * 
   * @param items String
   * @param facets String
   * @return CatalogEdgeApiResponseMessage
   */
  public CatalogEdgeApiResponseMessage browseSongs(String items, String facets) {
    ResteasyWebTarget target;
    target =
        client.target(ClientConstants.getInstance().BASE_URL + "/catalog-edge/browse/song?items="
            + items + "&facets=" + facets);
    Response response = target.request().get();
    CatalogEdgeApiResponseMessage responseWrapper =
        response.readEntity(CatalogEdgeApiResponseMessage.class);
    if (response.getStatus() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
    }
    return responseWrapper;
  }

}
