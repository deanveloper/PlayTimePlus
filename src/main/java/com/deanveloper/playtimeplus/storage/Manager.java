package com.deanveloper.playtimeplus.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.PlayTimePlus;

import java.nio.file.Path;
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
	 * Saves local cache to permanent storage.
	 */
	void save();

	/**
	 * Gets the file for this manager
	 */
	Path getStorage();

	/**
	 * Gets the total time a player has been online
	 */
	default Duration onlineTime(UUID id) {
		Duration total = Duration.ZERO;
		for (TimeEntry entry : get(id)) {
			total = total.plus(entry.getDuration());
		}
		return total;
	}

	/**
	 * Gets the PlayerEntry for the associated UUID. Should never be null.
	 */
	default NavigableSet<TimeEntry> get(UUID id) {
		updateLastCount(id);
		return getMap().compute(id, (key, set) -> set == null ? new TreeSet<>() : set);
	}

	default void updateLastCount(UUID id) {
		Player p = Bukkit.getPlayer(id);

		// never increase player time while offline or afk
		if (p == null || PlayTimePlus.getEssentialsHook().isAfk(id)) {
			return;
		}
		NavigableSet<TimeEntry> times = getNoUpdate(id);
		if (!times.isEmpty()) {
			TimeEntry newEnd = times.pollLast().newEnd(LocalDateTime.now());
			times.add(newEnd);
		}

		PlayTimePlus.debug("Updated last entry for " + id);
	}

	/**
	 * Gets a map that contains all of the Players' times
	 */
	Map<UUID, NavigableSet<TimeEntry>> getMap();

	/**
	 * Gets the PlayerEntry for the associated UUID. Should never be null.
	 * Doesn't update the count.
	 */
	default NavigableSet<TimeEntry> getNoUpdate(UUID id) {
		return getMap().compute(id, (key, set) -> set == null ? new TreeSet<>() : set);
	}

	/**
	 * Create a new TimeEntry for the player
	 */
	default void startNewEntry(UUID id) {
		LocalDateTime now = LocalDateTime.now();
		getNoUpdate(id).add(new TimeEntry(now, now));

		PlayTimePlus.debug("Started new entry for " + id);
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
