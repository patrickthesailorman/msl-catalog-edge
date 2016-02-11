/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.translate;

import com.datastax.driver.mapping.Result;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.client.dto.FacetDto;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import io.swagger.model.AlbumInfo;
import io.swagger.model.AlbumList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.FacetInfo;
import io.swagger.model.PagingState;
import io.swagger.model.SongInfo;
import io.swagger.model.SongList;

/**
 * @author kenzan
 */
public class Translators {

    /**
     * Translates an Album List BO to genuine Album List
     *
     * @param listBo AlbumListBo
     * @return AlbumList
     */
    public static AlbumList translate(AlbumListBo listBo) {
        AlbumList model = new AlbumList();
        if ( listBo != null ) {
            PagingState pagingState = new PagingState();
            pagingState.setPagingState(null == listBo.getPagingState() ? null : listBo.getPagingState().toString());
            model.setPagingState(pagingState);

            for ( AlbumBo albumBo : listBo.getBoList() ) {
                model.getAlbums().add(Translators.translate(albumBo));
            }
        }
        return model;
    }

    public static List<AlbumsByUserDto> translateAlbumsByUserDto(Result<AlbumsByUserDto> albums) {
        List<AlbumsByUserDto> model = new ArrayList<>();
        for ( AlbumsByUserDto album : albums ) {
            model.add(album);
        }
        return model;
    }

    /**
     * Translates an Album BO to genuine Album Info
     *
     * @param bo AlbumBo
     * @return AlbumInfo
     */
    public static AlbumInfo translate(AlbumBo bo) {
        AlbumInfo model = new AlbumInfo();

        if ( bo != null ) {
            model.setAlbumId(null == bo.getAlbumId() ? null : bo.getAlbumId().toString());
            model.setAlbumName(bo.getAlbumName());
            model.setArtistId(null == bo.getArtistId() ? null : bo.getArtistId().toString());
            model.setArtistMbid(null == bo.getArtistMbid() ? null : bo.getArtistMbid().toString());
            model.setArtistName(bo.getArtistName());
            model.setGenre(bo.getGenre());
            model.setYear(bo.getYear());
            model.setAverageRating(bo.getAverageRating());
            model.setPersonalRating(bo.getPersonalRating());
            model.setImageLink(bo.getImageLink());
            model.setInMyLibrary(bo.isInMyLibrary());
            model.setFavoritesTimestamp(bo.getFavoritesTimestamp());
            if ( null == bo.getSongsList() || bo.getSongsList().isEmpty() ) {
                model.setSongsList(null);
            }
            else {
                model.getSongsList().addAll(bo.getSongsList());
            }
        }

        return model;
    }

    /**
     * Translates an Artist List BO to genuine Artist List
     *
     * @param listBo ArtistListBo
     * @return ArtistList
     */
    public static ArtistList translate(ArtistListBo listBo) {
        ArtistList model = new ArtistList();

        if ( listBo != null ) {
            PagingState pagingState = new PagingState();
            pagingState.setPagingState(null == listBo.getPagingState() ? null : listBo.getPagingState().toString());
            model.setPagingState(pagingState);

            if ( null == listBo.getBoList() || listBo.getBoList().isEmpty() ) {
                model.setArtists(null);
            }
            else {
                for ( ArtistBo artistBo : listBo.getBoList() ) {
                    model.getArtists().add(Translators.translate(artistBo));
                }
            }
        }

        return model;
    }

    public static List<ArtistsByUserDto> translateArtistsByUserDto(Result<ArtistsByUserDto> artists) {
        List<ArtistsByUserDto> model = new ArrayList<>();
        for ( ArtistsByUserDto artist : artists ) {
            model.add(artist);
        }
        return model;
    }

    /**
     * Translates an Artist BO to genuine Artist Info
     *
     * @param bo ArtistBo
     * @return ArtistInfo
     */
    public static ArtistInfo translate(ArtistBo bo) {
        ArtistInfo model = new ArtistInfo();

        if ( bo != null ) {
            model.setArtistId(null == bo.getArtistId() ? null : bo.getArtistId().toString());
            model.setArtistMbid(null == bo.getArtistMbid() ? null : bo.getArtistMbid().toString());
            model.setArtistName(bo.getArtistName());
            model.setGenre(StringUtils.isEmpty(bo.getGenre()) ? null : bo.getGenre());
            model.setAverageRating(bo.getAverageRating());
            model.setPersonalRating(bo.getPersonalRating());
            model.setImageLink(bo.getImageLink());
            model.setAlbumsList((null == bo.getAlbumsList() || bo.getAlbumsList().isEmpty()) ? null : bo
                .getAlbumsList());
            model.setSongsList((null == bo.getSongsList() || bo.getSongsList().isEmpty()) ? null : bo.getSongsList());
            model.setInMyLibrary(bo.isInMyLibrary());
            model.setFavoritesTimestamp(bo.getFavoritesTimestamp());
            model
                .setSimilarArtistsList((null == bo.getSimilarArtistsList() || bo.getSimilarArtistsList().isEmpty())
                                                                                                                   ? null
                                                                                                                   : bo.getSimilarArtistsList());
        }

        return model;
    }

    /**
     * Translates a Song List BO to genuine Song List
     *
     * @param listBo SongListBo
     * @return SongList
     */
    public static SongList translate(SongListBo listBo) {
        SongList model = new SongList();

        if ( listBo != null ) {
            PagingState pagingState = new PagingState();
            pagingState.setPagingState(null == listBo.getPagingState() ? null : listBo.getPagingState().toString());
            model.setPagingState(pagingState);

            if ( null == listBo.getBoList() || listBo.getBoList().isEmpty() ) {
                model.setSongs(null);
            }
            else {
                for ( SongBo songBo : listBo.getBoList() ) {
                    model.getSongs().add(Translators.translate(songBo));
                }
            }
        }

        return model;
    }

    public static List<SongsByUserDto> translateSongsByUserDto(Result<SongsByUserDto> songs) {
        List<SongsByUserDto> model = new ArrayList<>();
        for ( SongsByUserDto song : songs ) {
            model.add(song);
        }
        return model;
    }

    /**
     * Translates a Song BO to genuine Song Info
     *
     * @param bo SongBo
     * @return SongInfo
     */
    public static SongInfo translate(SongBo bo) {
        SongInfo model = new SongInfo();

        if ( bo != null ) {
            model.setSongId(null == bo.getSongId() ? null : bo.getSongId().toString());
            model.setSongName(bo.getSongName());
            model.setGenre(bo.getGenre());
            model.setDuration(bo.getDuration());
            model.setDanceability(bo.getDanceability());
            model.setSongHotttnesss(bo.getSongHotttnesss());
            model.setYear(bo.getYear());
            model.setAverageRating(bo.getAverageRating());
            model.setPersonalRating(bo.getPersonalRating());
            model.setImageLink(bo.getImageLink());
            model.setArtistId(null == bo.getArtistId() ? null : bo.getArtistId().toString());
            model.setArtistMbid(null == bo.getArtistMbid() ? null : bo.getArtistMbid().toString());
            model.setArtistName(bo.getArtistName());
            model.setAlbumId(null == bo.getAlbumId() ? null : bo.getAlbumId().toString());
            model.setAlbumName(bo.getAlbumName());
            model.setInMyLibrary(bo.isInMyLibrary());
            model.setFavoritesTimestamp(bo.getFavoritesTimestamp());
        }

        return model;
    }

    /**
     * Translates a Facet DTO to Facet Info
     *
     * @param dto FacetDTO
     * @return FacetInfo
     */
    public static FacetInfo translate(FacetDto dto) {
        FacetInfo model = new FacetInfo();

        if ( dto != null ) {
            model.setFacetId(dto.getFacetId());
            model.setName(dto.getFacetName());
        }

        return model;
    }

    /**
     * Translates a Facet DTO list to Facet Info list
     *
     * @param dtoList List<FacetDTO>
     * @return List<FacetInfo>
     */
    public static List<FacetInfo> translateFacetList(List<FacetDto> dtoList) {
        if ( dtoList != null ) {
            List<FacetInfo> modelList = new ArrayList<>(dtoList.size());
            for ( FacetDto dto : dtoList ) {
                modelList.add(translate(dto));
            }
            return modelList;
        }
        return new ArrayList<>();
    }
}
