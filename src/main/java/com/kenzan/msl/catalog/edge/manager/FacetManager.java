/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.manager;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.dto.FacetDto;
import com.kenzan.msl.catalog.edge.translate.Translators;

import io.swagger.model.FacetInfo;
import io.swagger.model.FacetInfoWithChildren;

import java.util.ArrayList;
import java.util.List;

public class FacetManager {

  private FacetConstants fc = FacetConstants.getInstance();
  private static FacetManager instance = new FacetManager();

  private FacetManager() {}

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
    if (facet_id.equals("~")) {
      return getRootFacet();
    }

    // genreFacet or ratingFacet
    if (facet_id.contains("A")) {
      if (facet_id.equals(fc.GENRE_FACET_ID)) { // - genre facet case
        FacetInfoWithChildren genreFacet = new FacetInfoWithChildren();
        genreFacet.setFacetId(fc.GENRE_FACET_ID);
        genreFacet.setName(fc.GENRE_FACET_NAME);
        genreFacet.setChildren((Translators.translateFacetList(getGenreFacets())));
        return genreFacet;

      }

      FacetInfoWithChildren ratingFacet = new FacetInfoWithChildren();
      ratingFacet.setFacetId(fc.RATING_FACET_ID);
      ratingFacet.setName(fc.RATING_FACET_NAME);
      ratingFacet.setChildren(Translators.translateFacetList(getRatingFacets()));
      return ratingFacet;
    }

    Optional<FacetDto> optResponse = getFacet(facet_id);
    if (optResponse.isPresent()) {
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
   * @return Optional&lt;FacetDto&gt;
   */
  public Optional<FacetDto> getFacet(String id) {

    ArrayList<FacetDto> genreFacets = getGenreFacets();
    ArrayList<FacetDto> ratingFacets = getRatingFacets();

    for (FacetDto genreFacet : genreFacets) {
      if (genreFacet.getFacetId().equals(id)) {
        return Optional.of(genreFacet);
      }
    }

    for (FacetDto ratingFacet : ratingFacets) {
      if (ratingFacet.getFacetId().equals(id)) {
        return Optional.of(ratingFacet);
      }
    }

    return Optional.absent();
  }

  /**
   * Constructs an array of facetDto's corresponding to the rating facets
   *
   * @return ArrayList&lt;FacetDto&gt;
   */
  private ArrayList<FacetDto> getRatingFacets() {
    String[] ratings = new String[4];
    for (int i = 1; i < 5; i++) {
      ratings[i - 1] = i + " & UP";
    }
    ArrayList<FacetDto> result = new ArrayList<>();
    for (int i = 0; i < ratings.length; i++) {
      result.add(new FacetDto(Integer.toString(i + 1), ratings[i]));
    }
    return result;
  }

  /**
   * Constructs an array of facetDto's corresponding to the genre facets
   *
   * @return ArrayList&lt;FacetDto&gt;
   */
  private ArrayList<FacetDto> getGenreFacets() {
    ArrayList<FacetDto> result = new ArrayList<>();
    for (int i = 0; i < fc.GENRES.length; i++) {
      result.add(new FacetDto(Integer.toString(i + 5), fc.GENRES[i]));
    }
    return result;
  }
}
