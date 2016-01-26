package io.swagger.api.factories;

import io.swagger.api.CatalogEdgeApiService;
import io.swagger.api.impl.CatalogEdgeApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2016-01-25T12:48:08.000-06:00")
public class CatalogEdgeApiServiceFactory {

   private final static CatalogEdgeApiService service = new CatalogEdgeApiServiceImpl();

   public static CatalogEdgeApiService getCatalogEdgeApi()
   {
      return service;
   }
}
