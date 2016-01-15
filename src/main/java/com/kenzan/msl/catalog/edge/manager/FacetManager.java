/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.manager;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.dao.FacetDao;
import com.kenzan.msl.catalog.edge.translate.Translators;

import io.swagger.model.FacetInfo;
import io.swagger.model.FacetInfoWithChildren;

import java.util.ArrayList;
import java.util.List;

public class FacetManager {

    private FacetConstants fc = FacetConstants.getInstance();
    private static FacetManager instance = new FacetManager();

    private FacetManager() {
    }

    public static FacetManager getInstance() {
        return instance;
    }

    /**
     * Method that provides a valid interface for REST endpoint to retrieve a specific facet
     *
     * @param facet_id String
     * @return FacetInfoWithChildren
     */
    public FacetInfoWithChildren getRestFacets(String facet_id) {
        if ( facet_id.equals("~") ) {
            return getRootFacet();
        }

        // genreFacet or ratingFacet
        if ( facet_id.contains("A") ) {
            if ( facet_id.equals(fc.GENRE_FACET_ID) ) { // - genre facet case
                FacetInfoWithChildren genreFacet = new FacetInfoWithChildren();
                genreFacet.setFacetId(fc.GENRE_FACET_ID);
                genreFacet.setName(fc.GENRE_FACET_NAME);
                genreFacet.setChildren((Translators.translateFacetList(getGenreFacets())));
                return genreFacet;

            }
            else {
                FacetInfoWithChildren ratingFacet = new FacetInfoWithChildren();
                ratingFacet.setFacetId(fc.RATING_FACET_ID);
                ratingFacet.setName(fc.RATING_FACET_NAME);
                ratingFacet.setChildren(Translators.translateFacetList(getRatingFacets()));
                return ratingFacet;
            }
        }

        Optional<FacetDao> optResponse = getFacet(facet_id);
        if ( optResponse.isPresent() ) {
            FacetInfoWithChildren responseFacet = new FacetInfoWithChildren();
            responseFacet.setFacetId(optResponse.get().getFacetId());
            responseFacet.setName(optResponse.get().getFacetName());
            return responseFacet;
        }

        return new FacetInfoWithChildren();
    }

    /**
     * Retrieves the root facet
     *
     * @return FacetInfoWithChildren
     */
    private FacetInfoWithChildren getRootFacet() {
        List<FacetInfo> mainFacets = new ArrayList<>();
        FacetInfo genreFacets = new FacetInfo();
        genreFacets.setFacetId(fc.GENRE_FACET_ID);
        genreFacets.setName(fc.GENRE_FACET_NAME);
        mainFacets.add(genreFacets);

        FacetInfo ratingFacets = new FacetInfo();
        ratingFacets.setFacetId(fc.RATING_FACET_ID);
        ratingFacets.setName(fc.RATING_FACET_NAME);
        mainFacets.add(ratingFacets);

        FacetInfoWithChildren result = new FacetInfoWithChildren();
        result.setFacetId(fc.ROOT_FACET_ID);
        result.setName(fc.ROOT_FACET_NAME);
        result.setChildren(mainFacets);
        return result;
    }

    /**
     * Retrieves a specific facet from the genre or rating facets array
     *
     * @param id String
     * @return Optional<FacetDao>
     */
    public Optional<FacetDao> getFacet(String id) {

        ArrayList<FacetDao> genreFacets = getGenreFacets();
        ArrayList<FacetDao> ratingFacets = getRatingFacets();

        for ( FacetDao genreFacet : genreFacets ) {
            if ( genreFacet.getFacetId().equals(id) ) {
                return Optional.of(genreFacet);
            }
        }

        for ( FacetDao ratingFacet : ratingFacets ) {
            if ( ratingFacet.getFacetId().equals(id) ) {
                return Optional.of(ratingFacet);
            }
        }

        return Optional.absent();
    }

    /**
     * Constructs an array of facetDao's corresponding to the rating facets
     *
     * @return ArrayList<FacetDao>
     */
    private ArrayList<FacetDao> getRatingFacets() {
        String[] ratings = new String[4];
        for ( int i = 1; i < 5; i++ ) {
            ratings[i - 1] = i + " & UP";
        }
        ArrayList<FacetDao> result = new ArrayList<>();
        for ( int i = 0; i < ratings.length; i++ ) {
            result.add(new FacetDao(Integer.toString(i + 1), ratings[i]));
        }
        return result;
    }

    /**
     * Constructs an array of facetDao's corresponding to the genre facets
     *
     * @return ArrayList<FacetDao>
     */
    private ArrayList<FacetDao> getGenreFacets() {
        ArrayList<FacetDao> result = new ArrayList<>();
        for ( int i = 0; i < fc.GENRES.length; i++ ) {
            result.add(new FacetDao(Integer.toString(i + 5), fc.GENRES[i]));
        }
        return result;
    }
}
