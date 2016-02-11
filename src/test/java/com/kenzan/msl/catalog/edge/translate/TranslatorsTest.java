/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.translate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.kenzan.msl.catalog.client.dto.FeaturedAlbumsDto;
import com.kenzan.msl.catalog.client.dto.FeaturedArtistsDto;
import com.kenzan.msl.catalog.client.dto.FeaturedSongsDto;
import com.kenzan.msl.catalog.client.dto.AlbumsByFacetDto;
import com.kenzan.msl.catalog.client.dto.ArtistsByFacetDto;
import com.kenzan.msl.catalog.client.dto.SongsByFacetDto;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;

import io.swagger.model.AlbumInfo;
import io.swagger.model.AlbumList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.SongInfo;
import io.swagger.model.SongList;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author kenzan
 */
@RunWith(MockitoJUnitRunner.class)
public class TranslatorsTest {
    private static final UUID ALBUM1_UUID = UUID.fromString("00000000-0000-0000-0000-000000001");
    private static final String ALBUM1_NAME = "AlbumName1";
    private static final String ALBUM1_GENRE = "AlbumGenre1";
    private static final Integer ALBUM1_YEAR = 1971;
    private static final Integer ALBUM1_AVERAGE_RATING = 1;
    private static final Integer ALBUM1_PERSONAL_RATING = 2;
    private static final String ALBUM1_IMAGE_LINK = "http://server.com/Album/000001";

    private static final UUID ALBUM2_UUID = UUID.fromString("00000000-0000-0000-0000-000000002");
    private static final String ALBUM2_NAME = "AlbumName2";
    private static final Integer ALBUM2_YEAR = 1972;

    private static final UUID ARTIST1_UUID = UUID.fromString("00000000-0000-0000-0001-000000001");
    private static final String ARTIST1_NAME = "ArtistName1";
    private static final String ARTIST1_GENRE = "ArtistGenre1";
    private static final Integer ARTIST1_AVERAGE_RATING = 1;
    private static final Integer ARTIST1_PERSONAL_RATING = 2;
    private static final String ARTIST1_IMAGE_LINK = "http://server.com/Artist/000001";
    private static final UUID ARTIST1_MBID = UUID.fromString("00000000-0000-0000-0004-00000001");

    private static final UUID ARTIST2_UUID = UUID.fromString("00000000-0000-0000-0001-000000002");
    private static final String ARTIST2_NAME = "ArtistName2";
    private static final UUID ARTIST2_MBID = UUID.fromString("00000000-0000-0000-0004-00000002");

    private static final UUID ARTIST3_UUID = UUID.fromString("00000000-0000-0000-0001-000000003");

    private static final UUID SONG1_UUID = UUID.fromString("00000000-0000-0000-0002-000000001");
    private static final String SONG1_NAME = "SongName1";
    private static final String SONG1_GENRE = "SongGenre1";
    private static final Integer SONG1_DURATION = 101;
    private static final BigDecimal SONG1_DANCEABILITY = new BigDecimal(0.101);
    private static final BigDecimal SONG1_SONG_HOTTTNESSS = new BigDecimal(0.201);
    private static final Integer SONG1_YEAR = 1961;
    private static final Integer SONG1_AVERAGE_RATING = 3;
    private static final Integer SONG1_PERSONAL_RATING = 4;
    private static final String SONG1_IMAGE_LINK = "http://server.com/Song/000001";

    private static final UUID SONG2_UUID = UUID.fromString("00000000-0000-0000-0002-000000002");
    private static final String SONG2_NAME = "SongName2";
    private static final Integer SONG2_DURATION = 102;

    private static final UUID PAGINGSTATE1_UUID = UUID.fromString("00000000-0000-0000-0003-000000001");

    // ==========================================================================================================
    // ALBUMS
    // =================================================================================================================

    @Test
    public void testTranslateAlbumBoToModel_HappyPath() {
        AlbumBo bo = new AlbumBo();

        bo.setAlbumId(ALBUM1_UUID);
        bo.setAlbumName(ALBUM1_NAME);
        bo.setGenre(ALBUM1_GENRE);
        bo.setYear(ALBUM1_YEAR);
        bo.setAverageRating(ALBUM1_AVERAGE_RATING);
        bo.setPersonalRating(ALBUM1_PERSONAL_RATING);
        bo.setImageLink(ALBUM1_IMAGE_LINK);
        bo.setArtistId(ARTIST1_UUID);
        bo.setArtistName(ARTIST1_NAME);
        bo.getSongsList().add(SONG1_UUID.toString());
        bo.getSongsList().add(SONG2_UUID.toString());

        AlbumInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertEquals(model.getAlbumId(), ALBUM1_UUID.toString());
        assertEquals(model.getAlbumName(), ALBUM1_NAME);
        assertEquals(model.getArtistId(), ARTIST1_UUID.toString());
        assertEquals(model.getArtistName(), ARTIST1_NAME);
        assertEquals(model.getGenre(), ALBUM1_GENRE);
        assertEquals(model.getYear(), ALBUM1_YEAR);
        assertEquals(model.getAverageRating(), ALBUM1_AVERAGE_RATING);
        assertEquals(model.getPersonalRating(), ALBUM1_PERSONAL_RATING);
        assertEquals(model.getImageLink(), ALBUM1_IMAGE_LINK);
        assertFalse(model.getInMyLibrary());
        assertNotNull(model.getSongsList());
        assertEquals(model.getSongsList().size(), 2);
        assertTrue(model.getSongsList().contains(SONG1_UUID.toString()));
        assertTrue(model.getSongsList().contains(SONG2_UUID.toString()));
    }

    @Test
    public void testTranslateAlbumBoToModel_EverythingEmpty() {
        AlbumBo bo = new AlbumBo();

        AlbumInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertNull(model.getAlbumId());
        assertNull(model.getAlbumName());
        assertNull(model.getArtistId());
        assertNull(model.getArtistName());
        assertNull(model.getGenre());
        assertNull(model.getYear());
        assertNull(model.getAverageRating());
        assertNull(model.getPersonalRating());
        assertNull(model.getImageLink());
        assertFalse(model.getInMyLibrary());
        assertNull(model.getSongsList());
    }

    @Test
    public void testTranslateAlbumListBoToModel_HappyPath() {
        AlbumListBo bo = new AlbumListBo();

        bo.setPagingState(PAGINGSTATE1_UUID);

        FeaturedAlbumsDto dto1 = new FeaturedAlbumsDto();
        dto1.setAlbumId(ALBUM1_UUID);
        dto1.setAlbumName(ALBUM1_NAME);
        dto1.setAlbumYear(ALBUM1_YEAR);
        dto1.setArtistId(ARTIST1_UUID);
        dto1.setArtistName(ARTIST1_NAME);
        bo.add(dto1);

        AlbumsByFacetDto dto2 = new AlbumsByFacetDto();
        dto2.setAlbumId(ALBUM2_UUID);
        dto2.setAlbumName(ALBUM2_NAME);
        dto2.setAlbumYear(ALBUM2_YEAR);
        dto2.setArtistId(ARTIST2_UUID);
        dto2.setArtistName(ARTIST2_NAME);
        bo.add(dto2);

        AlbumList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertEquals(model.getPagingState().getPagingState(), PAGINGSTATE1_UUID.toString());
        assertNotNull(model.getAlbums());
        assertEquals(model.getAlbums().size(), 2);
        for ( int i = 0; i < 2; i++ ) {
            AlbumInfo albumModel = model.getAlbums().get(i);

            assertNotNull(albumModel);
            assertNotNull(albumModel.getAlbumId());
            if ( albumModel.getAlbumId().equals(ALBUM1_UUID.toString()) ) {
                assertEquals(albumModel.getAlbumId(), ALBUM1_UUID.toString());
                assertEquals(albumModel.getAlbumName(), ALBUM1_NAME);
                assertEquals(albumModel.getArtistId(), ARTIST1_UUID.toString());
                assertEquals(albumModel.getArtistName(), ARTIST1_NAME);
                assertEquals(albumModel.getYear(), ALBUM1_YEAR);
                assertNull(albumModel.getAverageRating());
                assertNull(albumModel.getPersonalRating());
                assertNull(albumModel.getImageLink());
                assertFalse(albumModel.getInMyLibrary());
                assertNull(albumModel.getSongsList());
            }
            else if ( albumModel.getAlbumId().equals(ALBUM2_UUID.toString()) ) {
                assertEquals(albumModel.getAlbumId(), ALBUM2_UUID.toString());
                assertEquals(albumModel.getAlbumName(), ALBUM2_NAME);
                assertEquals(albumModel.getArtistId(), ARTIST2_UUID.toString());
                assertEquals(albumModel.getArtistName(), ARTIST2_NAME);
                assertEquals(albumModel.getYear(), ALBUM2_YEAR);
                assertNull(albumModel.getAverageRating());
                assertNull(albumModel.getPersonalRating());
                assertNull(albumModel.getImageLink());
                assertFalse(albumModel.getInMyLibrary());
                assertNull(albumModel.getSongsList());
            }
            else {
                fail("Received unknown album in list.");
            }
        }
    }

    @Test
    public void testTranslateAlbumListBoToModel_EverythingEmpty() {
        AlbumListBo bo = new AlbumListBo();

        AlbumList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertNull(model.getPagingState().getPagingState());
        assertNotNull(model.getAlbums());
        assertEquals(model.getAlbums().size(), 0);
    }

    // =========================================================================================================
    // ARTISTS
    // =================================================================================================================

    @Test
    public void testTranslateArtistBoToModel_HappyPath() {
        ArtistBo bo = new ArtistBo();

        bo.setArtistId(ARTIST1_UUID);
        bo.setArtistName(ARTIST1_NAME);
        bo.setGenre(ARTIST1_GENRE);
        bo.setAverageRating(ARTIST1_AVERAGE_RATING);
        bo.setPersonalRating(ARTIST1_PERSONAL_RATING);
        bo.setImageLink(ARTIST1_IMAGE_LINK);
        bo.getAlbumsList().add(ALBUM1_UUID.toString());
        bo.getAlbumsList().add(ALBUM2_UUID.toString());
        bo.getSongsList().add(SONG1_UUID.toString());
        bo.getSongsList().add(SONG2_UUID.toString());
        bo.getSimilarArtistsList().add(ARTIST2_UUID.toString());
        bo.getSimilarArtistsList().add(ARTIST3_UUID.toString());

        ArtistInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertEquals(model.getArtistId(), ARTIST1_UUID.toString());
        assertEquals(model.getArtistName(), ARTIST1_NAME);
        assertEquals(model.getAverageRating(), ARTIST1_AVERAGE_RATING);
        assertEquals(model.getPersonalRating(), ARTIST1_PERSONAL_RATING);
        assertEquals(model.getImageLink(), ARTIST1_IMAGE_LINK);
        assertEquals(model.getGenre(), ARTIST1_GENRE);
        assertFalse(model.getInMyLibrary());
        assertNotNull(model.getAlbumsList());
        assertEquals(model.getAlbumsList().size(), 2);
        assertTrue(model.getAlbumsList().contains(ALBUM1_UUID.toString()));
        assertTrue(model.getAlbumsList().contains(ALBUM2_UUID.toString()));
        assertNotNull(model.getSongsList());
        assertEquals(model.getSongsList().size(), 2);
        assertTrue(model.getSongsList().contains(SONG1_UUID.toString()));
        assertTrue(model.getSongsList().contains(SONG2_UUID.toString()));
        assertNotNull(model.getSimilarArtistsList());
        assertEquals(model.getSimilarArtistsList().size(), 2);
        assertTrue(model.getSimilarArtistsList().contains(ARTIST2_UUID.toString()));
        assertTrue(model.getSimilarArtistsList().contains(ARTIST3_UUID.toString()));
    }

    @Test
    public void testTranslateArtistBoToModel_EverythingEmpty() {
        ArtistBo bo = new ArtistBo();

        ArtistInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertNull(model.getArtistId());
        assertNull(model.getArtistName());
        assertNull(model.getAverageRating());
        assertNull(model.getPersonalRating());
        assertNull(model.getImageLink());
        assertNull(model.getGenre());
        assertFalse(model.getInMyLibrary());
        assertNull(model.getAlbumsList());
        assertNull(model.getSongsList());
        assertNull(model.getSimilarArtistsList());
    }

    @Test
    public void testTranslateArtistListBoToModel_HappyPath() {
        ArtistListBo bo = new ArtistListBo();

        bo.setPagingState(PAGINGSTATE1_UUID);

        FeaturedArtistsDto dto1 = new FeaturedArtistsDto();
        dto1.setArtistId(ARTIST1_UUID);
        dto1.setArtistName(ARTIST1_NAME);
        bo.add(dto1);

        ArtistsByFacetDto dto2 = new ArtistsByFacetDto();
        dto2.setArtistId(ARTIST2_UUID);
        dto2.setArtistName(ARTIST2_NAME);
        bo.add(dto2);

        ArtistList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertEquals(model.getPagingState().getPagingState(), PAGINGSTATE1_UUID.toString());
        assertNotNull(model.getArtists());
        assertEquals(model.getArtists().size(), 2);
        for ( int i = 0; i < 2; i++ ) {
            ArtistInfo artistModel = model.getArtists().get(i);

            assertNotNull(artistModel);
            assertNotNull(artistModel.getArtistId());
            if ( artistModel.getArtistId().equals(ARTIST1_UUID.toString()) ) {
                assertEquals(artistModel.getArtistName(), ARTIST1_NAME);
                assertNull(artistModel.getAverageRating());
                assertNull(artistModel.getPersonalRating());
                assertNull(artistModel.getImageLink());
                assertNull(artistModel.getGenre());
                assertFalse(artistModel.getInMyLibrary());
                assertNull(artistModel.getAlbumsList());
                assertNull(artistModel.getSongsList());
                assertNull(artistModel.getSimilarArtistsList());
            }
            else if ( artistModel.getArtistId().equals(ARTIST2_UUID.toString()) ) {
                assertEquals(artistModel.getArtistName(), ARTIST2_NAME);
                assertNull(artistModel.getAverageRating());
                assertNull(artistModel.getPersonalRating());
                assertNull(artistModel.getImageLink());
                assertNull(artistModel.getGenre());
                assertFalse(artistModel.getInMyLibrary());
                assertNull(artistModel.getAlbumsList());
                assertNull(artistModel.getSongsList());
                assertNull(artistModel.getSimilarArtistsList());
            }
            else {
                fail("Received unknown album in list.");
            }
        }
    }

    @Test
    public void testTranslateArtistListBoToModel_EverythingEmpty() {
        ArtistListBo bo = new ArtistListBo();

        ArtistList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertNull(model.getPagingState().getPagingState());
        assertNull(model.getArtists());
    }

    // ===========================================================================================================
    // SONGS
    // =================================================================================================================

    @Test
    public void testTranslateSongBoToModel_HappyPath() {
        SongBo bo = new SongBo();

        bo.setSongId(SONG1_UUID);
        bo.setSongName(SONG1_NAME);
        bo.setGenre(SONG1_GENRE);
        bo.setDuration(SONG1_DURATION);
        bo.setDanceability(SONG1_DANCEABILITY);
        bo.setSongHotttnesss(SONG1_SONG_HOTTTNESSS);
        bo.setYear(SONG1_YEAR);
        bo.setAverageRating(SONG1_AVERAGE_RATING);
        bo.setPersonalRating(SONG1_PERSONAL_RATING);
        bo.setImageLink(SONG1_IMAGE_LINK);
        bo.setArtistId(ARTIST1_UUID);
        bo.setArtistName(ARTIST1_NAME);
        bo.setAlbumId(ALBUM1_UUID);
        bo.setAlbumName(ALBUM1_NAME);

        SongInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertEquals(model.getSongId(), SONG1_UUID.toString());
        assertEquals(model.getSongName(), SONG1_NAME);
        assertEquals(model.getImageLink(), SONG1_IMAGE_LINK);
        assertEquals(model.getDuration(), SONG1_DURATION);
        assertEquals(model.getGenre(), SONG1_GENRE);
        assertEquals(model.getDanceability(), SONG1_DANCEABILITY);
        assertEquals(model.getAverageRating(), SONG1_AVERAGE_RATING);
        assertEquals(model.getPersonalRating(), SONG1_PERSONAL_RATING);
        assertEquals(model.getSongHotttnesss(), SONG1_SONG_HOTTTNESSS);
        assertEquals(model.getYear(), SONG1_YEAR);
        assertFalse(model.getInMyLibrary());
        assertEquals(model.getArtistId(), ARTIST1_UUID.toString());
        assertEquals(model.getArtistName(), ARTIST1_NAME);
        assertEquals(model.getAlbumId(), ALBUM1_UUID.toString());
        assertEquals(model.getAlbumName(), ALBUM1_NAME);
    }

    @Test
    public void testTranslateSongBoToModel_EverythingEmpty() {
        SongBo bo = new SongBo();

        SongInfo model = Translators.translate(bo);

        assertNotNull(model);
        assertNull(model.getSongId());
        assertNull(model.getSongName());
        assertNull(model.getImageLink());
        assertNull(model.getDuration());
        assertNull(model.getGenre());
        assertNull(model.getDanceability());
        assertNull(model.getAverageRating());
        assertNull(model.getPersonalRating());
        assertNull(model.getSongHotttnesss());
        assertNull(model.getYear());
        assertFalse(model.getInMyLibrary());
        assertNull(model.getArtistId());
        assertNull(model.getArtistName());
        assertNull(model.getAlbumId());
        assertNull(model.getAlbumName());
    }

    @Test
    public void testTranslateSongListBoToModel_HappyPath() {
        SongListBo bo = new SongListBo();

        bo.setPagingState(PAGINGSTATE1_UUID);

        FeaturedSongsDto dto1 = new FeaturedSongsDto();
        dto1.setSongId(SONG1_UUID);
        dto1.setSongName(SONG1_NAME);
        dto1.setSongDuration(SONG1_DURATION);
        dto1.setAlbumId(ALBUM1_UUID);
        dto1.setAlbumName(ALBUM1_NAME);
        dto1.setAlbumYear(ALBUM1_YEAR);
        dto1.setArtistId(ARTIST1_UUID);
        dto1.setArtistName(ARTIST1_NAME);
        dto1.setArtistMbid(ARTIST1_MBID);
        bo.add(dto1);

        SongsByFacetDto dto2 = new SongsByFacetDto();
        dto2.setSongId(SONG2_UUID);
        dto2.setSongName(SONG2_NAME);
        dto2.setSongDuration(SONG2_DURATION);
        dto2.setAlbumId(ALBUM2_UUID);
        dto2.setAlbumName(ALBUM2_NAME);
        dto2.setAlbumYear(ALBUM2_YEAR);
        dto2.setArtistId(ARTIST2_UUID);
        dto2.setArtistName(ARTIST2_NAME);
        dto2.setArtistMbid(ARTIST2_MBID);
        bo.add(dto2);

        SongList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertEquals(model.getPagingState().getPagingState(), PAGINGSTATE1_UUID.toString());
        assertNotNull(model.getSongs());
        assertEquals(model.getSongs().size(), 2);
        for ( int i = 0; i < 2; i++ ) {
            SongInfo songModel = model.getSongs().get(i);

            assertNotNull(songModel);
            assertNotNull(songModel.getSongId());
            if ( songModel.getSongId().equals(SONG1_UUID.toString()) ) {
                assertEquals(songModel.getSongName(), SONG1_NAME);
                assertNull(songModel.getImageLink());
                assertEquals(songModel.getDuration(), SONG1_DURATION);
                assertNull(songModel.getGenre());
                assertNull(songModel.getDanceability());
                assertNull(songModel.getAverageRating());
                assertNull(songModel.getPersonalRating());
                assertNull(songModel.getSongHotttnesss());
                assertFalse(songModel.getInMyLibrary());
                assertEquals(songModel.getArtistId(), ARTIST1_UUID.toString());
                assertEquals(songModel.getArtistName(), ARTIST1_NAME);
                assertEquals(songModel.getAlbumId(), ALBUM1_UUID.toString());
                assertEquals(songModel.getAlbumName(), ALBUM1_NAME);
                assertEquals(songModel.getYear(), ALBUM1_YEAR);
            }
            else if ( songModel.getSongId().equals(SONG2_UUID.toString()) ) {
                assertEquals(songModel.getSongName(), SONG2_NAME);
                assertNull(songModel.getImageLink());
                assertEquals(songModel.getDuration(), SONG2_DURATION);
                assertNull(songModel.getGenre());
                assertNull(songModel.getDanceability());
                assertNull(songModel.getAverageRating());
                assertNull(songModel.getPersonalRating());
                assertNull(songModel.getSongHotttnesss());
                assertFalse(songModel.getInMyLibrary());
                assertEquals(songModel.getArtistId(), ARTIST2_UUID.toString());
                assertEquals(songModel.getArtistName(), ARTIST2_NAME);
                assertEquals(songModel.getAlbumId(), ALBUM2_UUID.toString());
                assertEquals(songModel.getAlbumName(), ALBUM2_NAME);
                assertEquals(songModel.getYear(), ALBUM2_YEAR);
            }
            else {
                fail("Received unknown album in list.");
            }
        }
    }

    @Test
    public void testTranslateSongListBoToModel_EverythingEmpty() {
        SongListBo bo = new SongListBo();

        SongList model = Translators.translate(bo);

        assertNotNull(model);
        assertNotNull(model.getPagingState());
        assertNull(model.getPagingState().getPagingState());
        assertNull(model.getSongs());
    }
}
