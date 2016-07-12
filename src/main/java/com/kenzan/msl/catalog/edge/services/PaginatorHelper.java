/*
 * Copyright 2015, Kenzan, All rights reserved.
 */
package com.kenzan.msl.catalog.edge.services;

import com.datastax.driver.core.Statement;
import com.kenzan.msl.catalog.client.cassandra.QueryAccessor;

/**
 * @author kenzan
 */
public interface PaginatorHelper {

  Statement prepareFacetedQuery(final QueryAccessor queryAccessor, final String facetName);

  Statement prepareFeaturedQuery(final QueryAccessor queryAccessor);

  String getFacetedQueryString(final String facetName);

  String getFeaturedQueryString();
}
