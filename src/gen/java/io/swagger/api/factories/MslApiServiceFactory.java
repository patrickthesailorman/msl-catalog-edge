package io.swagger.api.factories;

import io.swagger.api.MslApiService;
import io.swagger.api.impl.MslApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-23T16:59:19.184-06:00")
public class MslApiServiceFactory {

   private final static MslApiService service = new MslApiServiceImpl();

   public static MslApiService getMslApi()
   {
      return service;
   }
}
