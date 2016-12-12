package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Dean
 */
public interface Storage {

    /**
     * Initializes the storage object. Do not define everything in the constructor!
     */
    void init();

    /**
     * Gets the PlayerEntry for the associated UUID.
     */
    PlayerEntry get(UUID id);

    /**
     * Updates the player in the sorted set
     */
    default void update(PlayerEntry entry) {
        if(getPlayersSorted().remove(entry)) {
            PlayTimePlus.debug("Removed " + entry.getName() + " from sorted list");
        }

        if (getPlayersSorted().add(entry)) {
            PlayTimePlus.debug("Added " + entry.getName() + " to sorted list");
        }

        if(getPlayers().remove(entry.getId()) != null) {
            PlayTimePlus.debug("Removed " + entry.getName() + " from map");
        }

        if(getPlayers().put(entry.getId(), entry) == null) {
            PlayTimePlus.debug("Added " + entry.getName() + " to map");
        }
    }

    /**
     * Saves local cache to permanent storage.
     */
    void save();

    /**
     * Gets all players who have PlayerEntries.
     */
    Map<UUID, PlayerEntry> getPlayers();

    /**
     * Gets a sorted version of all players who have PlayerEntries
     */
    NavigableSet<PlayerEntry> getPlayersSorted();
}
