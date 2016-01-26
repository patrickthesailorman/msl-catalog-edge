package io.swagger.api.impl;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.edge.manager.FacetManager;
import com.kenzan.msl.catalog.edge.services.*;

import io.swagger.api.MslApiService;
import io.swagger.model.AlbumInfo;
import io.swagger.model.NotFoundResponse;
import io.swagger.model.ErrorResponse;
import io.swagger.model.ArtistInfo;
import io.swagger.model.AlbumList;
import io.swagger.model.ArtistList;
import io.swagger.model.SongList;
import io.swagger.model.SongInfo;

import io.swagger.api.NotFoundException;
import org.apache.commons.lang.StringUtils;
import javax.ws.rs.core.Response;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-23T16:59:19.184-06:00")
public class MslApiServiceImpl extends MslApiService {

      private CatalogEdge catalogEdge = new CatalogEdgeService(
              new AlbumsService(),
              new ArtistsService(),
              new SongsService()
      );
  
      @Override
      public Response getAlbum(String albumId)
              throws NotFoundException {
          // Validate required parameters
          if (StringUtils.isEmpty(albumId)) {
              return Response.status(Response.Status.BAD_REQUEST).entity(new MslApiResponseMessage(MslApiResponseMessage.ERROR, "Required parameter 'albumId' is null or empty.")).build();
          }

          Optional<AlbumInfo> optAlbumInfo;
          try {
              optAlbumInfo = catalogEdge.getAlbum(albumId, null).toBlocking().first();
          } catch (Exception e) {
              e.printStackTrace();

              ErrorResponse errorResponse = new ErrorResponse();
              errorResponse.setMessage("Server error: " + e.getMessage());
              return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
          }

          if (!optAlbumInfo.isPresent()) {
              NotFoundResponse notFoundResponse = new NotFoundResponse();
              notFoundResponse.setMessage("Unable to find album with id=" + albumId);
              return Response.status(Response.Status.NOT_FOUND).entity(notFoundResponse).build();
          }

          return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", optAlbumInfo.get())).build();
      }
  
          @Override
          public Response getArtist(String artistId)
                  throws NotFoundException {
              // Validate required parameters
              if (StringUtils.isEmpty(artistId)) {
                  return Response.status(Response.Status.BAD_REQUEST).entity(new MslApiResponseMessage(MslApiResponseMessage.ERROR, "Required parameter 'artistId' is null or empty.")).build();
              }

              Optional<ArtistInfo> optArtistInfo;
              try {
                  optArtistInfo = catalogEdge.getArtist(artistId, null).toBlocking().first();
              } catch (Exception e) {
                  e.printStackTrace();

                  ErrorResponse errorResponse = new ErrorResponse();
                  errorResponse.setMessage("Server error: " + e.getMessage());
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
              }

              if (!optArtistInfo.isPresent()) {
                  NotFoundResponse notFoundResponse = new NotFoundResponse();
                  notFoundResponse.setMessage("Unable to find artist with id=" + artistId);
                  return Response.status(Response.Status.NOT_FOUND).entity(notFoundResponse).build();
              }

              return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", optArtistInfo.get())).build();
          }
  
       @Override
       public Response browseAlbums(Integer items, String pagingState, String facets)
               throws NotFoundException {
           AlbumList albumList;
           try {
               albumList = catalogEdge.browseAlbums(pagingState, items, facets, MslSessionToken.getInstance().getTokenValue())
                       .toBlocking()
                       .first();
           } catch (Exception e) {
               e.printStackTrace();

               ErrorResponse errorResponse = new ErrorResponse();
               errorResponse.setMessage("Server error: " + e.getMessage());
               return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
           }

           return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", albumList)).build();
       }
  
      @Override
          public Response browseArtists(Integer items, String pagingState, String facets)
                  throws NotFoundException {
              ArtistList artistList;
              try {
                  artistList = catalogEdge.browseArtists(pagingState, items, facets, MslSessionToken.getInstance().getTokenValue())
                          .toBlocking()
                          .first();
              } catch (Exception e) {
                  e.printStackTrace();

                  ErrorResponse errorResponse = new ErrorResponse();
                  errorResponse.setMessage("Server error: " + e.getMessage());
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
              }

              return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", artistList)).build();
          }
  
      @Override
          public Response browseSongs(Integer items, String pagingState, String facets)
                  throws NotFoundException {
              SongList songList;
              try {
                  songList = catalogEdge.browseSongs(pagingState, items, facets, MslSessionToken.getInstance().getTokenValue())
                          .toBlocking()
                          .first();
              } catch (Exception e) {
                  e.printStackTrace();

                  ErrorResponse errorResponse = new ErrorResponse();
                  errorResponse.setMessage("Server error: " + e.getMessage());
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
              }

              return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", songList)).build();
          }
  
      @Override
          public Response getFacet(String facetId)
                  throws NotFoundException {
              // Validate required parameters
              if (StringUtils.isEmpty(facetId)) {
                  return Response.status(Response.Status.BAD_REQUEST).entity(new MslApiResponseMessage(MslApiResponseMessage.ERROR, "Required parameter 'facetId' is null or empty.")).build();
              }
              return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", FacetManager.getInstance().getRestFacets(facetId))).build();
          }
  
     @Override
         public Response getSong(String songId)
                 throws NotFoundException {
             // Validate required parameters
             if (StringUtils.isEmpty(songId)) {
                 return Response.status(Response.Status.BAD_REQUEST).entity(new MslApiResponseMessage(MslApiResponseMessage.ERROR, "Required parameter 'songId' is null or empty.")).build();
             }

             Optional<SongInfo> optSongInfo;
             try {
                 optSongInfo = catalogEdge.getSong(songId, null).toBlocking().first();
             } catch (Exception e) {
                 e.printStackTrace();
                 ErrorResponse errorResponse = new ErrorResponse();
                 errorResponse.setMessage("Server error: " + e.getMessage());
                 return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
             }

             if (!optSongInfo.isPresent()) {
                 NotFoundResponse notFoundResponse = new NotFoundResponse();
                 notFoundResponse.setMessage("Unable to find song with id=" + songId);
                 return Response.status(Response.Status.NOT_FOUND).entity(notFoundResponse).build();
             }

             return Response.ok().entity(new MslApiResponseMessage(MslApiResponseMessage.OK, "success", optSongInfo.get())).build();
         }
  
}
