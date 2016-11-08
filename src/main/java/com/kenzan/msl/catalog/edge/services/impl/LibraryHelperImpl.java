/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services.impl;

import com.datastax.driver.mapping.Result;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.catalog.edge.services.LibraryHelper;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.SongBo;

import java.util.UUID;

/**
 * @author kenzan
 */
public class LibraryHelperImpl implements LibraryHelper{

  private final AccountDataClientService accountDataClientService;

  @Inject
  public LibraryHelperImpl(final AccountDataClientService accountDataClientService) {
    this.accountDataClientService = accountDataClientService;
  }

  /**
   * Retrieve user library albums
   *
   * @param userId java.util.UUID
   * @return Result&lt;AlbumsByUserDto&gt;
   */
  public Result<AlbumsByUserDto> getUserAlbums(final UUID userId) {
    return accountDataClientService
        .mapAlbumsByUser(
            accountDataClientService.getAlbumsByUser(userId, Optional.absent(), Optional.absent()))
        .toBlocking().first();
  }

  /**
   * Checks if an album is on a user library and if it's it attaches the timestamp data and library
   * flag
   *
   * @param userAlbums Result&lt;SongsByUserDto&gt; userAlbums
   * @param album com.kenzan.msl.common.bo.AlbumBo
   */
  public void processLibraryAlbumInfo(final Iterable<AlbumsByUserDto> userAlbums,
      final AlbumBo album) {
    for (AlbumsByUserDto albumsByUserDto : userAlbums) {
      if (albumsByUserDto.getAlbumId().equals(album.getAlbumId())) {
        album.setInMyLibrary(true);
        album.setFavoritesTimestamp(Long
            .toString(albumsByUserDto.getFavoritesTimestamp().getTime()));
      }
    }
  }

  /**
   * Retrieve user library artists
   *
   * @param userId java.util.UUID
   * @return Result&lt;ArtistsByUserDto&gt;
   */
  public Result<ArtistsByUserDto> getUserArtists(final UUID userId) {
    return accountDataClientService
        .mapArtistByUser(
            accountDataClientService.getArtistsByUser(userId, Optional.absent(), Optional.absent()))
        .toBlocking().first();
  }

  /**
   * Checks if an artist is on a user library and if it's it attaches the timestamp data and library
   * flag
   *
   * @param userArtists Result&lt;SongsByUserDto&gt; userArtists
   * @param artist com.kenzan.msl.common.bo.ArtistBo
   */
  public void processLibraryArtistInfo(Iterable<ArtistsByUserDto> userArtists, final ArtistBo artist) {
    for (ArtistsByUserDto artistsByUserDto : userArtists) {
      if (artistsByUserDto.getArtistId().equals(artist.getArtistId())) {
        artist.setInMyLibrary(true);
        artist.setFavoritesTimestamp(Long.toString(artistsByUserDto.getFavoritesTimestamp()
            .getTime()));
      }
    }
  }

  /**
   * Retrieve user library songs
   *
   * @param userId java.util.UUID
   * @return Result&lt;SongsByUserDto&gt;
   */
  public Result<SongsByUserDto> getUserSongs(final UUID userId) {
    return accountDataClientService
        .mapSongsByUser(
            accountDataClientService.getSongsByUser(userId, Optional.absent(), Optional.absent()))
        .toBlocking().first();
  }

  /**
   * Checks if a song is on a user library and if it's it attaches the timestamp data and library
   * flag
   *
   * @param userSongs Result&lt;SongsByUserDto&gt; userSongs
   * @param song com.kenzan.msl.common.bo.SongBo
   */
  public void processLibrarySongInfo(Iterable<SongsByUserDto> userSongs, final SongBo song) {
    for (SongsByUserDto songsByUserDto : userSongs) {
      if (songsByUserDto.getSongId().equals(song.getSongId())) {
        song.setInMyLibrary(true);
        song.setFavoritesTimestamp(Long.toString(songsByUserDto.getFavoritesTimestamp().getTime()));
      }
    }
  }
}
