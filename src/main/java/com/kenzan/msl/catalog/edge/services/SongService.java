package com.kenzan.msl.catalog.edge.services;

import com.google.common.base.Optional;
import com.kenzan.msl.common.bo.SongBo;
import com.kenzan.msl.common.bo.SongListBo;

import java.util.UUID;

/**
 * @author kenzan
 */
public interface SongService {

  Optional<SongBo> getSong(final Optional<UUID> userUuid, final UUID songUuid);

  SongListBo getSongsList(final Optional<UUID> userUuid, final Integer items, final String facets,
      final Optional<UUID> pagingStateUuid);
}
