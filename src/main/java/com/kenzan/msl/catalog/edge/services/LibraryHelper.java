package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.mapping.Result;
import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.SongBo;

import java.util.UUID;

/**
 * @author Kenzan
 */
public interface LibraryHelper {

    Result<AlbumsByUserDto> getUserAlbums(final UUID userId);

    void processLibraryAlbumInfo(final Iterable<AlbumsByUserDto> userAlbums,
                                 final AlbumBo album);

    Result<ArtistsByUserDto> getUserArtists(final UUID userId);

    void processLibraryArtistInfo(Iterable<ArtistsByUserDto> userArtists, final ArtistBo artist);

    Result<SongsByUserDto> getUserSongs(final UUID userId);

    void processLibrarySongInfo(Iterable<SongsByUserDto> userSongs, final SongBo song);

}
