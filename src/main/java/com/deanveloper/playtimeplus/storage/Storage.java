package com.deanveloper.playtimeplus.storage;

import java.util.*;

/**
 * @author Dean
 */
public interface Storage {

    /**
     * Creates a PlayerEntry object as well as loading it into the local cache.
     * Implementation should do the checking to see if the PlayerEntry is already present.
     */
    void createIfNotPresent(UUID id);

    /**
     * Saves local cache to permanent storage.
     */
    void save();

    /**
     * Updates a player entry into the local cache.
     * Implementation should do nothing if the entries themselves are stored locally.
     */
    void update(PlayerEntry entry);

    /**
     * Gets the PlayerEntry for the associated UUID.
     */
    PlayerEntry get(UUID id);

    /**
     * Gets all players who have PlayerEntries.
     */
    Map<UUID, PlayerEntry> getPlayers();

}
