package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Dean
 */
public interface Manager {

    /**
     * Initializes the storage object. Do not define everything in the constructor!
     */
    void init();

    /**
     * Gets the PlayerEntry for the associated UUID. Should never be null.
     */
    NavigableSet<TimeEntry> get(UUID id);

    /**
     * Saves local cache to permanent storage.
     */
    void save();

    /**
     * Gets a map that contains all of the Players' times
     */
    Map<UUID, NavigableSet<TimeEntry>> getMap();

    /**
     * Gets the total time a player has been online
     */
    default Duration onlineTime(UUID id) {
        Duration total = Duration.ZERO;
        for(TimeEntry entry : get(id)) {
            total = total.plus(entry.getDuration());
        }
        return total;
    }

    /**
     * Create a new TimeEntry for the player
     */
    default void startNewEntry(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        get(id).add(new TimeEntry(now, now ,id));

        PlayTimePlus.debug("Started new entry for " + id);
    }

    default void updateLastCount(UUID id) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(id);

        // never increase player time while offline or afk
        if(!p.isOnline() || PlayTimePlus.getEssentialsHook().isAfk(id)) {
            return;
        }
        NavigableSet<TimeEntry> times = get(id);
        if(!times.isEmpty()) {
            TimeEntry newEnd = times.pollLast().newEnd(LocalDateTime.now());
            times.add(newEnd);
        }

        PlayTimePlus.debug("Updated last entry for " + id);
    }

    /**
     * Re-orders the map
     */
    default void update() {
        Map<UUID, NavigableSet<TimeEntry>> temp = new HashMap<>(getMap());
        getMap().clear();
        getMap().putAll(temp);
    }
}
