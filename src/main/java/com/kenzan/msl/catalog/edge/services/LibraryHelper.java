/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.SongBo;

import java.util.UUID;

public class LibraryHelper {

    // =================================================================================================================
    // ALBUMS
    // =================================================================================================================

    /**
     * Retrieve user library albums
     *
     * @param userId java.util.UUID
     * @return Result<AlbumsByUserDto>
     */
    public Result<AlbumsByUserDto> getUserAlbums(final UUID userId) {
        CassandraAccountService cassandraAccountService = CassandraAccountService.getInstance();
        return cassandraAccountService
            .mapAlbumsByUser(cassandraAccountService.getAlbumsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if an album is on a user library and if it's it attaches the timestamp data and
     * library flag
     *
     * @param userAlbums Result<SongsByUserDto> userAlbums
     * @param album com.kenzan.msl.common.bo.AlbumBo
     */
    public void processLibraryAlbumInfo(Iterable<AlbumsByUserDto> userAlbums, final AlbumBo album) {
        for ( AlbumsByUserDto albumsByUserDto : userAlbums ) {
            if ( albumsByUserDto.getAlbumId().equals(album.getAlbumId()) ) {
                album.setInMyLibrary(true);
                album.setFavoritesTimestamp(albumsByUserDto.getFavoritesTimestamp().toString());
            }
        }
    }

    // =================================================================================================================
    // ARTISTS
    // =================================================================================================================

    /**
     * Retrieve user library artists
     *
     * @param userId java.util.UUID
     * @return Result<ArtistsByUserDto>
     */
    public Result<ArtistsByUserDto> getUserArtists(final UUID userId) {
        CassandraAccountService cassandraAccountService = CassandraAccountService.getInstance();
        return cassandraAccountService
            .mapArtistByUser(cassandraAccountService.getArtistsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if an artist is on a user library and if it's it attaches the timestamp data and
     * library flag
     *
     * @param userArtists Result<SongsByUserDto> userArtists
     * @param artist com.kenzan.msl.common.bo.ArtistBo
     */
    public void processLibraryArtistInfo(Iterable<ArtistsByUserDto> userArtists, final ArtistBo artist) {
        for ( ArtistsByUserDto artistsByUserDto : userArtists ) {
            if ( artistsByUserDto.getArtistId().equals(artist.getArtistId()) ) {
                artist.setInMyLibrary(true);
                artist.setFavoritesTimestamp(artistsByUserDto.getFavoritesTimestamp().toString());
            }
        }
    }

    // =================================================================================================================
    // SONGS
    // =================================================================================================================

    /**
     * Retrieve user library songs
     *
     * @param userId java.util.UUID
     * @return Result<SongsByUserDto>
     */
    public Result<SongsByUserDto> getUserSongs(final UUID userId) {
        CassandraAccountService cassandraAccountService = CassandraAccountService.getInstance();
        return cassandraAccountService
            .mapSongsByUser(cassandraAccountService.getSongsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if a song is on a user library and if it's it attaches the timestamp data and library
     * flag
     *
     * @param userSongs Result<SongsByUserDto> userSongs
     * @param song com.kenzan.msl.common.bo.SongBo
     */
    public void processLibrarySongInfo(Iterable<SongsByUserDto> userSongs, final SongBo song) {
        for ( SongsByUserDto songsByUserDto : userSongs ) {
            if ( songsByUserDto.getSongId().equals(song.getSongId()) ) {
                song.setInMyLibrary(true);
                song.setFavoritesTimestamp(songsByUserDto.getFavoritesTimestamp().toString());
            }
        }
    }
}
