package com.kenzan.msl.catalog.edge.services;

import com.google.common.base.Optional;
import com.kenzan.msl.common.bo.ArtistBo;
import com.kenzan.msl.common.bo.ArtistListBo;

import java.util.UUID;

/**
 * @author kenzan
 */
public interface ArtistService {

  Optional<ArtistBo> getArtist(final Optional<UUID> userUuid, final UUID artistUuid);

  ArtistListBo getArtistsList(final Optional<UUID> userUuid, final Integer items,
      final String facets, final Optional<UUID> pagingStateUuid);
}
