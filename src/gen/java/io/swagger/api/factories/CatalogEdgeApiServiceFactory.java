package io.swagger.api.factories;

import com.google.inject.Inject;
import io.swagger.api.CatalogEdgeApiService;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2016-01-25T12:48:08.000-06:00")
public class CatalogEdgeApiServiceFactory {

   public CatalogEdgeApiService service;

   @Inject
   public CatalogEdgeApiServiceFactory (final CatalogEdgeApiService catalogEdgeApiService) {
      service = catalogEdgeApiService;
   }
}
