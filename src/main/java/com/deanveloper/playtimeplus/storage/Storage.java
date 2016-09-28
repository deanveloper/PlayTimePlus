package com.deanveloper.playtimeplus.storage;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Dean
 */
public interface Storage {

    /**
     * Saves local cache to permanent storage.
     */
    void save();

    /**
     * Gets the PlayerEntry for the associated UUID.
     */
    PlayerEntry get(UUID id);

    /**
     * Gets all players who have PlayerEntries.
     */
    Map<UUID, PlayerEntry> getPlayers();

}
