/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.kenzan.msl.account.client.dao.AlbumsByUserDao;
import com.kenzan.msl.account.client.dao.ArtistsByUserDao;
import com.kenzan.msl.account.client.dao.SongsByUserDao;
import com.kenzan.msl.account.client.services.CassandraAccountService;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.SongBo;

import java.util.UUID;

public class LibraryHelper {

    private static CassandraAccountService cassandraAccountService = CassandraAccountService.getInstance();

    // =================================================================================================================
    // ALBUMS
    // =================================================================================================================

    /**
     * Retrieve user library albums
     *
     * @param userId java.util.UUID
     * @return Result<AlbumsByUserDao>
     */
    public Result<AlbumsByUserDao> getUserAlbums(final UUID userId) {
        return cassandraAccountService
            .mapAlbumsByUser(cassandraAccountService.getAlbumsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if an album is on a user library and if it's it attaches the timestamp data and
     * library flag
     *
     * @param userAlbums Result<SongsByUserDao> userAlbums
     * @param album com.kenzan.msl.common.bo.AlbumBo
     */
    public void processLibraryAlbumInfo(Result<AlbumsByUserDao> userAlbums, final AlbumBo album) {
        for ( AlbumsByUserDao albumsByUserDao : userAlbums ) {
            if ( albumsByUserDao.getAlbumId().equals(album.getAlbumId()) ) {
                album.setInMyLibrary(true);
                album.setFavoritesTimestamp(albumsByUserDao.getFavoritesTimestamp().toString());
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
     * @return Result<ArtistsByUserDao>
     */
    public Result<ArtistsByUserDao> getUserArtists(final UUID userId) {
        return cassandraAccountService
            .mapArtistByUser(cassandraAccountService.getArtistsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if an artist is on a user library and if it's it attaches the timestamp data and
     * library flag
     *
     * @param userArtists Result<SongsByUserDao> userArtists
     * @param artist com.kenzan.msl.common.bo.ArtistBo
     */
    public void processLibraryArtistInfo(Result<ArtistsByUserDao> userArtists, final ArtistBo artist) {
        for ( ArtistsByUserDao artistsByUserDao : userArtists ) {
            if ( artistsByUserDao.getArtistId().equals(artist.getArtistId()) ) {
                artist.setInMyLibrary(true);
                artist.setFavoritesTimestamp(artistsByUserDao.getFavoritesTimestamp().toString());
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
     * @return Result<SongsByUserDao>
     */
    public Result<SongsByUserDao> getUserSongs(final UUID userId) {
        return cassandraAccountService
            .mapSongsByUser(cassandraAccountService.getSongsByUser(userId, Optional.absent(), Optional.absent()))
            .toBlocking().first();
    }

    /**
     * Checks if a song is on a user library and if it's it attaches the timestamp data and library
     * flag
     *
     * @param userSongs Result<SongsByUserDao> userSongs
     * @param song com.kenzan.msl.common.bo.SongBo
     */
    public void processLibrarySongInfo(Result<SongsByUserDao> userSongs, final SongBo song) {
        for ( SongsByUserDao songsByUserDao : userSongs ) {
            if ( songsByUserDao.getSongId().equals(song.getSongId()) ) {
                song.setInMyLibrary(true);
                song.setFavoritesTimestamp(songsByUserDao.getFavoritesTimestamp().toString());
            }
        }
    }
}
