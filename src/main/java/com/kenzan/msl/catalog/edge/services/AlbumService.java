package com.kenzan.msl.catalog.edge.services;

import com.google.common.base.Optional;
import com.kenzan.msl.common.bo.AlbumBo;
import com.kenzan.msl.common.bo.AlbumListBo;

import java.util.UUID;

/**
 * @author kenzan
 */
public interface AlbumService {

  Optional<AlbumBo> getAlbum(final Optional<UUID> userUuid, final UUID albumUuid);

  AlbumListBo getAlbumsList(final Optional<UUID> userUuid, final Integer items,
      final String facets, final Optional<UUID> pagingStateUuid);
}
