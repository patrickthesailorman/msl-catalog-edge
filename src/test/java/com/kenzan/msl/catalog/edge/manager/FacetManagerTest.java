package com.kenzan.msl.catalog.edge.manager;

import com.google.common.base.Optional;
import com.kenzan.msl.catalog.client.dto.FacetDto;
import io.swagger.model.FacetInfo;
import io.swagger.model.FacetInfoWithChildren;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author kenzan
 */
public class FacetManagerTest {

  private FacetManager facetManager = FacetManager.getInstance();
  private FacetConstants fc = FacetConstants.getInstance();

  @Test
  public void getRootFacet() {
    FacetInfoWithChildren results = facetManager.getRestFacets("~");
    assertEquals(results.getFacetId(), fc.ROOT_FACET_ID);
    assertEquals(results.getName(), fc.ROOT_FACET_NAME);

    List<FacetInfo> children = results.getChildren();
    assertNotNull(children);
    assertEquals(children.get(0).getFacetId(), fc.GENRE_FACET_ID);
    assertEquals(children.get(0).getName(), fc.GENRE_FACET_NAME);
    assertEquals(children.get(1).getFacetId(), fc.RATING_FACET_ID);
    assertEquals(children.get(1).getName(), fc.RATING_FACET_NAME);
  }

  @Test
  public void testGetRestFacets() {
    FacetInfoWithChildren results = facetManager.getRestFacets(fc.GENRE_FACET_ID);
    assertEquals(results.getChildren().size(), fc.GENRES.length);
    assertEquals(results.getName(), fc.GENRE_FACET_NAME);
    assertEquals(results.getFacetId(), fc.GENRE_FACET_ID);

    results = facetManager.getRestFacets(fc.RATING_FACET_ID);
    assertEquals(results.getChildren().size(), 4);
    assertEquals(results.getName(), fc.RATING_FACET_NAME);
    assertEquals(results.getFacetId(), fc.RATING_FACET_ID);
  }

  @Test
  public void getRatingFacet() {
    for (int i = 1; i < 5; i++) {
      Optional<FacetDto> result = facetManager.getFacet(Integer.toString(i));
      assertEquals(result.get().getFacetId(), Integer.toString(i));
      assertEquals(result.get().getFacetName(), String.format("%s & UP", Integer.toString(i)));
    }
  }

  @Test
  public void getGenreFacets() {
    for (int i = 5; i < 30; i++) {
      Optional<FacetDto> result = facetManager.getFacet(Integer.toString(i));
      assertEquals(result.get().getFacetId(), Integer.toString(i));
      assertNotNull(result.get().getFacetName());
    }
  }
}
