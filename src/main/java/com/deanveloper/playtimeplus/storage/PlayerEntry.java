package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Dean
 */
public class PlayerEntry implements Comparable<PlayerEntry> {
    private static Map<UUID, LocalDateTime> onlineTimes = new HashMap<>();

    @SerializedName("i")
    private UUID id;
    @SerializedName("t")
    private List<TimeEntry> times;
    private transient boolean hasChanged;
    private transient Duration lastTotal;

    /**
     * For gson to use
     */
    private PlayerEntry() {
        hasChanged = true;
        lastTotal = Duration.ZERO;
    }

    /**
     * Use for players who have never logged on before, otherwise
     * please use {@link Storage#get(UUID)}
     */
    public PlayerEntry(UUID id) {
        this();

        this.id = id;
        this.times = new ArrayList<>();
    }

    /**
     * Updates the players online time, should be called every ten seconds.
     */
    public static void updatePlayers() {
        for (Map.Entry<UUID, LocalDateTime> online : onlineTimes.entrySet()) {
            if (PlayTimePlus.getEssentialsHook().isAfk(online.getKey())) {
                continue;
            }
            LocalDateTime start = onlineTimes.get(online.getKey());
            PlayerEntry entry = PlayTimePlus.getPlayerDb().get(online.getKey());

            TimeEntry current = entry.times.stream()
                    .filter(time -> Duration.between(time.getStart(), start).getSeconds() == 0)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("TimeEntry not found starting with " + start));

            current.duration = Duration.between(start, LocalDateTime.now());

            entry.hasChanged = true;

            PlayTimePlus.getPlayerDb().update(entry);
        }
    }

    /**
     * The {@link UUID} of the player the object describes
     */
    public UUID getId() {
        return id;
    }

    /**
     * The name of the player the object describes
     */
    public String getName() {
        return Utils.getName(id);
    }

    /**
     * The times that the player has been online
     */
    public List<TimeEntry> getTimes() {
        return times;
    }

    /**
     * Set the person to being online or not
     */
    public void setOnline(boolean online) {
        if (online && !onlineTimes.containsKey(id)) {
            LocalDateTime now = LocalDateTime.now();
            onlineTimes.put(id, now);
            TimeEntry time = new TimeEntry();
            time.start = now;
            time.duration = Duration.ZERO;
            times.add(time);
        } else if (!online && onlineTimes.containsKey(id)) {
            LocalDateTime start = onlineTimes.remove(id);

            TimeEntry current = times.stream()
                    .filter(time -> Duration.between(time.getStart(), start).getSeconds() == 0)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("TimeEntry not found starting with " + start));

            current.duration = Duration.between(start, LocalDateTime.now());
        }

        hasChanged = true;
        PlayTimePlus.getPlayerDb().update(this);
    }

    /**
     * The total time the player has been online
     */
    public Duration getTotalTime() {
        if (hasChanged) {
            lastTotal = Duration.ZERO;
            for (TimeEntry entry : getTimes()) {
                lastTotal = lastTotal.plus(entry.duration);
            }
            hasChanged = false;
        }
        return lastTotal;
    }

    @Override
    public String toString() {
        return "PlayerEntry[id=" + id + ",times=" + times + "]";
    }

    @Override
    public int compareTo(PlayerEntry o) {
        // Put the other object first to sort in descending order
        return Long.compare(o.getTotalTime().getSeconds(), this.getTotalTime().getSeconds());
    }

    public class TimeEntry {
        @SerializedName("s")
        private LocalDateTime start;
        @SerializedName("d")
        private Duration duration;

        public LocalDateTime getStart() {
            return start;
        }

        public Duration getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return "TimeEntry[start=" + start + ",duration=" + duration + "]";
        }
    }
}
