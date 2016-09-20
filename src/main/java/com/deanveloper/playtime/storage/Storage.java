package com.deanveloper.playtime.storage;

import java.util.*;

/**
 * @author Dean
 */
public interface Storage {

    void createIfNotPresent(UUID id);

    void save();

    void update(PlayerEntry entry);

    PlayerEntry get(UUID id);

    Map<UUID, PlayerEntry> getPlayers();

}
