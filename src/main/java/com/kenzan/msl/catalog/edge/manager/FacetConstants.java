package com.kenzan.msl.catalog.edge.manager;

public class FacetConstants {

    public String GENRE_FACET_ID = "A1";
    public String GENRE_FACET_NAME = "genres";

    public String RATING_FACET_ID = "A2";
    public String RATING_FACET_NAME = "rating";

    public String ROOT_FACET_ID = "00";
    public String ROOT_FACET_NAME = "root";

    public String[] GENRES = {
        "Classical",
        "Blues",
        "Dubstep",
        "Jazz",
        "Electronica",
        "Latin",
        "Soul",
        "Funk",
        "Cajun",
        "Celtic",
        "Folk",
        "Big Band",
        "Alternative",
        "Reggae",
        "Bluegrass",
        "Punk",
        "Rap",
        "Rock",
        "Hip Hop",
        "Gospel",
        "Heavy Metal",
        "Country",
        "Salsa",
        "Opera",
        "Pop" };

    private static FacetConstants instance = null;

    private FacetConstants() {
    }

    public static FacetConstants getInstance() {
        if ( instance == null ) {
            instance = new FacetConstants();
        }
        return instance;
    }
}
