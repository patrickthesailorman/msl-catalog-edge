package io.swagger.api.impl;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.kenzan.msl.catalog.edge.manager.FacetManager;
import com.kenzan.msl.catalog.edge.services.*;
import io.swagger.api.*;

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

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2016-01-25T12:48:08.000-06:00")
public class CatalogEdgeApiServiceImpl extends CatalogEdgeApiService {


    private CatalogEdgeService catalogEdgeService;

    @Inject
    public CatalogEdgeApiServiceImpl (final CatalogEdgeService catalogEdgeService) {
        this.catalogEdgeService = catalogEdgeService;
    }

    @Override
    public Response getAlbum(String albumId)
            throws NotFoundException {
        // Validate required parameters
        if (StringUtils.isEmpty(albumId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.ERROR, "Required parameter 'albumId' is null or empty.")).build();
        }

        Optional<AlbumInfo> optAlbumInfo;
        try {
            optAlbumInfo = catalogEdgeService.getAlbum(albumId, null).toBlocking().first();
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

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", optAlbumInfo.get())).build();
    }

    @Override
    public Response getArtist(String artistId)
            throws NotFoundException {
        // Validate required parameters
        if (StringUtils.isEmpty(artistId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.ERROR, "Required parameter 'artistId' is null or empty.")).build();
        }

        Optional<ArtistInfo> optArtistInfo;
        try {
            optArtistInfo = catalogEdgeService.getArtist(artistId, null).toBlocking().first();
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

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", optArtistInfo.get())).build();
    }

    @Override
    public Response browseAlbums(Integer items, String pagingState, String facets)
            throws NotFoundException {
        AlbumList albumList;
        try {
            albumList = catalogEdgeService.browseAlbums(pagingState, items, facets, CatalogEdgeSessionToken.getInstance().getTokenValue())
                    .toBlocking()
                    .first();
        } catch (Exception e) {
            e.printStackTrace();

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Server error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", albumList)).build();
    }

    @Override
    public Response browseArtists(Integer items, String pagingState, String facets)
            throws NotFoundException {
        ArtistList artistList;
        try {
            artistList = catalogEdgeService.browseArtists(pagingState, items, facets, CatalogEdgeSessionToken.getInstance().getTokenValue())
                    .toBlocking()
                    .first();
        } catch (Exception e) {
            e.printStackTrace();

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Server error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", artistList)).build();
    }

    @Override
    public Response browseSongs(Integer items, String pagingState, String facets)
            throws NotFoundException {
        SongList songList;
        try {
            songList = catalogEdgeService.browseSongs(pagingState, items, facets, CatalogEdgeSessionToken.getInstance().getTokenValue())
                    .toBlocking()
                    .first();
        } catch (Exception e) {
            e.printStackTrace();

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Server error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", songList)).build();
    }

    @Override
    public Response getFacet(String facetId)
            throws NotFoundException {
        // Validate required parameters
        if (StringUtils.isEmpty(facetId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.ERROR, "Required parameter 'facetId' is null or empty.")).build();
        }
        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", FacetManager.getInstance().getRestFacets(facetId))).build();
    }

    @Override
    public Response getSong(String songId)
            throws NotFoundException {
        // Validate required parameters
        if (StringUtils.isEmpty(songId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.ERROR, "Required parameter 'songId' is null or empty.")).build();
        }

        Optional<SongInfo> optSongInfo;
        try {
            optSongInfo = catalogEdgeService.getSong(songId, null).toBlocking().first();
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

        return Response.ok().entity(new CatalogEdgeApiResponseMessage(CatalogEdgeApiResponseMessage.OK, "success", optSongInfo.get())).build();
    }

}
