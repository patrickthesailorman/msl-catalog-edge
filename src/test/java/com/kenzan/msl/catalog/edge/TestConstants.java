package com.kenzan.msl.catalog.edge;

import com.kenzan.msl.account.client.dto.AlbumsByUserDto;
import com.kenzan.msl.account.client.dto.ArtistsByUserDto;
import com.kenzan.msl.account.client.dto.SongsByUserDto;
import com.kenzan.msl.catalog.client.dto.PagingStateDto;
import com.kenzan.msl.catalog.client.dto.FeaturedAlbumsDto;
import com.kenzan.msl.catalog.client.dto.AlbumsByFacetDto;
import com.kenzan.msl.catalog.client.dto.SongsArtistByAlbumDto;
import com.kenzan.msl.catalog.client.dto.SongsAlbumsByArtistDto;
import com.kenzan.msl.catalog.client.dto.AlbumArtistBySongDto;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.common.bo.SongListBo;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.SongBo;

import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class TestConstants {
    private static TestConstants instance = null;

    public final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000001");
    public final UUID ARTIST_ID = UUID.fromString("00000000-0000-0000-0001-000000001");
    public final UUID ARTIST_ID_2 = UUID.fromString("00000000-0000-0000-0012-000000001");
    public final UUID ARTIST_ID_3 = UUID.fromString("00000000-0000-0000-0013-000000001");
    public final String ARTIST_NAME = "ARTIST_NAME";
    public final UUID SONG_ID = UUID.fromString("00000000-0000-0000-0002-000000001");
    public final UUID ALBUM_ID = UUID.fromString("00000000-0000-0000-0002-000000001");
    public final UUID PAGING_STATE_ID = UUID.fromString("00000000-0000-0001-0002-000000001");
    public final Integer ITEMS = 5;
    public final String FACETS = "2,7";
    public final Date TIMESTAMP = new Date();
    public final int SONG_DURATION = 101;

    public ArtistListBo ARTIST_LIST_BO;
    public AlbumListBo ALBUM_LIST_BO;
    public SongListBo SONG_LIST_BO;

    public List<AlbumsByUserDto> albumsByUserDtoList = new ArrayList<>();
    public List<ArtistsByUserDto> artistsByUserDtoList = new ArrayList<>();
    public List<SongsByUserDto> songsByUserDtoList = new ArrayList<>();
    public PagingStateDto pagingStateDto = new PagingStateDto();

    public List<AlbumBo> albumBoList = new ArrayList<>();
    public List<FeaturedAlbumsDto> featuredAlbumDtoList = new ArrayList<>();
    public List<AlbumsByFacetDto> albumsByFacetDtoList = new ArrayList<>();

    public SongsArtistByAlbumDto songsArtistByAlbumDto = new SongsArtistByAlbumDto();
    public SongsAlbumsByArtistDto songsAlbumsByArtistDto = new SongsAlbumsByArtistDto();
    public AlbumArtistBySongDto albumArtistBySongDto = new AlbumArtistBySongDto();

    public AlbumBo ALBUM_BO;
    public ArtistBo ARTIST_BO;
    public SongBo SONG_BO;

    public final Integer ALBUM_YEAR = 1988;
    public final String ALBUM_NAME = "SOME ALBUM NAME";
    public final String SONG_NAME = "TEST SONG NAME";

    private TestConstants() {
        initDtos();
        initBos();

        albumBoList.add(ALBUM_BO);

        ARTIST_LIST_BO = new ArtistListBo();
        ALBUM_LIST_BO = new AlbumListBo();
        SONG_LIST_BO = new SongListBo();

        AlbumsByUserDto albumsByUserDto = new AlbumsByUserDto();
        albumsByUserDto.setAlbumId(ALBUM_ID);
        albumsByUserDto.setArtistId(ARTIST_ID);
        albumsByUserDto.setUserId(USER_ID);
        albumsByUserDto.setFavoritesTimestamp(TIMESTAMP);
        albumsByUserDtoList.add(albumsByUserDto);

        ArtistsByUserDto artistsByUserDto = new ArtistsByUserDto();
        artistsByUserDto.setArtistName(ARTIST_NAME);
        artistsByUserDto.setArtistId(ARTIST_ID);
        artistsByUserDto.setUserId(USER_ID);
        artistsByUserDto.setFavoritesTimestamp(TIMESTAMP);
        artistsByUserDtoList.add(artistsByUserDto);

        SongsByUserDto songsByUserDto = new SongsByUserDto();
        songsByUserDto.setArtistId(ARTIST_ID);
        songsByUserDto.setArtistName(ARTIST_NAME);
        songsByUserDto.setUserId(USER_ID);
        songsByUserDto.setAlbumId(ALBUM_ID);
        songsByUserDto.setSongId(SONG_ID);
        songsByUserDto.setFavoritesTimestamp(TIMESTAMP);
        songsByUserDtoList.add(songsByUserDto);
    }

    public static TestConstants getInstance() {
        if ( instance == null ) {
            instance = new TestConstants();
        }
        return instance;
    }

    private void initBos() {
        ALBUM_BO = new AlbumBo();
        ALBUM_BO.setAlbumId(ALBUM_ID);
        ALBUM_BO.setArtistId(ARTIST_ID);

        ARTIST_BO = new ArtistBo();
        ARTIST_BO.setArtistId(ARTIST_ID);
        ARTIST_BO.setArtistName(ARTIST_NAME);

        SONG_BO = new SongBo();
        SONG_BO.setArtistName(ARTIST_NAME);
        SONG_BO.setArtistId(ARTIST_ID);
        SONG_BO.setAlbumId(ALBUM_ID);
        SONG_BO.setSongId(SONG_ID);
    }

    private void initDtos() {
        Set<String> genres = new HashSet<String>();
        genres.add("4");
        Map<UUID, String> similarArtists = new HashMap<>();
        similarArtists.put(ARTIST_ID_2, ARTIST_NAME);
        similarArtists.put(ARTIST_ID_3, ARTIST_NAME);

        songsArtistByAlbumDto.setAlbumId(ALBUM_ID);
        songsArtistByAlbumDto.setArtistId(ARTIST_ID);
        songsArtistByAlbumDto.setArtistName(ARTIST_NAME);
        songsArtistByAlbumDto.setSongName(SONG_NAME);
        songsArtistByAlbumDto.setSongId(SONG_ID);
        songsArtistByAlbumDto.setAlbumId(ALBUM_ID);
        songsArtistByAlbumDto.setAlbumName(ALBUM_NAME);
        songsArtistByAlbumDto.setAlbumYear(ALBUM_YEAR);
        songsArtistByAlbumDto.setArtistGenres(genres);

        songsAlbumsByArtistDto.setArtistId(ARTIST_ID);
        songsAlbumsByArtistDto.setArtistName(ARTIST_NAME);
        songsAlbumsByArtistDto.setSongName(SONG_NAME);
        songsAlbumsByArtistDto.setSongId(SONG_ID);
        songsAlbumsByArtistDto.setArtistGenres(genres);
        songsAlbumsByArtistDto.setAlbumId(ALBUM_ID);
        songsAlbumsByArtistDto.setAlbumName(ALBUM_NAME);
        songsAlbumsByArtistDto.setAlbumYear(ALBUM_YEAR);
        songsAlbumsByArtistDto.setSongDuration(SONG_DURATION);
        songsAlbumsByArtistDto.setSimilarArtists(similarArtists);

        albumArtistBySongDto.setAlbumId(ALBUM_ID);
        albumArtistBySongDto.setArtistId(ARTIST_ID);
        albumArtistBySongDto.setSongId(SONG_ID);
        albumArtistBySongDto.setSongName(SONG_NAME);
        albumArtistBySongDto.setAlbumName(ALBUM_NAME);
        albumArtistBySongDto.setArtistName(ARTIST_NAME);
        albumArtistBySongDto.setAlbumYear(ALBUM_YEAR);
        albumArtistBySongDto.setSongDuration(SONG_DURATION);
        albumArtistBySongDto.setSimilarArtists(similarArtists);
        albumArtistBySongDto.setArtistGenres(genres);

        PagingStateDto.PagingStateUdt pagingStateUdt = new PagingStateDto.PagingStateUdt();
        pagingStateDto.setUserId(USER_ID);
        pagingStateDto.setPagingState(pagingStateUdt);
    }

}
