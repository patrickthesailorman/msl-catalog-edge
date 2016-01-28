package com.kenzan.msl.catalog.edge;

import com.kenzan.msl.account.client.dao.AlbumsByUserDao;
import com.kenzan.msl.account.client.dao.ArtistsByUserDao;
import com.kenzan.msl.account.client.dao.SongsByUserDao;
import com.kenzan.msl.catalog.client.dao.*;
import com.kenzan.msl.common.bo.*;

import java.util.*;

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

    public List<AlbumsByUserDao> albumsByUserDaoList = new ArrayList<>();
    public List<ArtistsByUserDao> artistsByUserDaoList = new ArrayList<>();
    public List<SongsByUserDao> songsByUserDaoList = new ArrayList<>();
    public PagingStateDao pagingStateDao = new PagingStateDao();

    public List<AlbumBo> albumBoList = new ArrayList<>();
    public List<FeaturedAlbumsDao> featuredAlbumDaoList = new ArrayList<>();
    public List<AlbumsByFacetDao> albumsByFacetDaoList = new ArrayList<>();

    public SongsArtistByAlbumDao songsArtistByAlbumDao = new SongsArtistByAlbumDao();
    public SongsAlbumsByArtistDao songsAlbumsByArtistDao = new SongsAlbumsByArtistDao();
    public AlbumArtistBySongDao albumArtistBySongDao = new AlbumArtistBySongDao();

    public AlbumBo ALBUM_BO;
    public ArtistBo ARTIST_BO;
    public SongBo SONG_BO;

    public final Integer ALBUM_YEAR = 1988;
    public final String ALBUM_NAME = "SOME ALBUM NAME";
    public final String SONG_NAME = "TEST SONG NAME";

    private TestConstants() {
        initDaos();
        initBos();

        albumBoList.add(ALBUM_BO);

        ARTIST_LIST_BO = new ArtistListBo();
        ALBUM_LIST_BO = new AlbumListBo();
        SONG_LIST_BO = new SongListBo();

        AlbumsByUserDao albumsByUserDao = new AlbumsByUserDao();
        albumsByUserDao.setAlbumId(ALBUM_ID);
        albumsByUserDao.setArtistId(ARTIST_ID);
        albumsByUserDao.setUserId(USER_ID);
        albumsByUserDao.setFavoritesTimestamp(TIMESTAMP);
        albumsByUserDaoList.add(albumsByUserDao);

        ArtistsByUserDao artistsByUserDao = new ArtistsByUserDao();
        artistsByUserDao.setArtistName(ARTIST_NAME);
        artistsByUserDao.setArtistId(ARTIST_ID);
        artistsByUserDao.setUserId(USER_ID);
        artistsByUserDao.setFavoritesTimestamp(TIMESTAMP);
        artistsByUserDaoList.add(artistsByUserDao);

        SongsByUserDao songsByUserDao = new SongsByUserDao();
        songsByUserDao.setArtistId(ARTIST_ID);
        songsByUserDao.setArtistName(ARTIST_NAME);
        songsByUserDao.setUserId(USER_ID);
        songsByUserDao.setAlbumId(ALBUM_ID);
        songsByUserDao.setSongId(SONG_ID);
        songsByUserDao.setFavoritesTimestamp(TIMESTAMP);
        songsByUserDaoList.add(songsByUserDao);
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

    private void initDaos() {
        Set<String> genres = new HashSet<String>();
        genres.add("4");
        Map<UUID, String> similarArtists = new HashMap<>();
        similarArtists.put(ARTIST_ID_2, ARTIST_NAME);
        similarArtists.put(ARTIST_ID_3, ARTIST_NAME);

        songsArtistByAlbumDao.setAlbumId(ALBUM_ID);
        songsArtistByAlbumDao.setArtistId(ARTIST_ID);
        songsArtistByAlbumDao.setArtistName(ARTIST_NAME);
        songsArtistByAlbumDao.setSongName(SONG_NAME);
        songsArtistByAlbumDao.setSongId(SONG_ID);
        songsArtistByAlbumDao.setAlbumId(ALBUM_ID);
        songsArtistByAlbumDao.setAlbumName(ALBUM_NAME);
        songsArtistByAlbumDao.setAlbumYear(ALBUM_YEAR);
        songsArtistByAlbumDao.setArtistGenres(genres);

        songsAlbumsByArtistDao.setArtistId(ARTIST_ID);
        songsAlbumsByArtistDao.setArtistName(ARTIST_NAME);
        songsAlbumsByArtistDao.setSongName(SONG_NAME);
        songsAlbumsByArtistDao.setSongId(SONG_ID);
        songsAlbumsByArtistDao.setArtistGenres(genres);
        songsAlbumsByArtistDao.setAlbumId(ALBUM_ID);
        songsAlbumsByArtistDao.setAlbumName(ALBUM_NAME);
        songsAlbumsByArtistDao.setAlbumYear(ALBUM_YEAR);
        songsAlbumsByArtistDao.setSongDuration(SONG_DURATION);
        songsAlbumsByArtistDao.setSimilarArtists(similarArtists);

        albumArtistBySongDao.setAlbumId(ALBUM_ID);
        albumArtistBySongDao.setArtistId(ARTIST_ID);
        albumArtistBySongDao.setSongId(SONG_ID);
        albumArtistBySongDao.setSongName(SONG_NAME);
        albumArtistBySongDao.setAlbumName(ALBUM_NAME);
        albumArtistBySongDao.setArtistName(ARTIST_NAME);
        albumArtistBySongDao.setAlbumYear(ALBUM_YEAR);
        albumArtistBySongDao.setSongDuration(SONG_DURATION);
        albumArtistBySongDao.setSimilarArtists(similarArtists);
        albumArtistBySongDao.setArtistGenres(genres);

        PagingStateDao.PagingStateUdt pagingStateUdt = new PagingStateDao.PagingStateUdt();
        pagingStateDao.setUserId(USER_ID);
        pagingStateDao.setPagingState(pagingStateUdt);
    }

}
