package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    private transient boolean totalChanged = true;
    private transient Duration lastTotal = Duration.ZERO;

    /**
     * Use for players who have never logged on before, otherwise
     * please use {@link Storage#get(UUID)}
     */
    public PlayerEntry(UUID id) {
        this.id = id;
        this.times = new ArrayList<>();
        totalChanged = true;
        lastTotal = Duration.ZERO;
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
                    .filter(time -> start.toEpochSecond(ZoneOffset.UTC) == time.getStart().toEpochSecond(ZoneOffset.UTC))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("TimeEntry not found starting with " + start));

            current.end = LocalDateTime.now();

            entry.totalChanged = true;

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
            time.setStart(now);
            time.setEnd(now);
            times.add(time);
        } else if (!online && onlineTimes.containsKey(id)) {
            LocalDateTime start = onlineTimes.remove(id);

            TimeEntry current = times.stream()
                    .filter(time -> Duration.between(time.getStart(), start).getSeconds() == 0)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("TimeEntry not found starting with " + start));

            current.setEnd(LocalDateTime.now());
        }

        totalChanged = true;
        PlayTimePlus.getPlayerDb().update(this);
    }

    /**
     * The total time the player has been online
     */
    public Duration getTotalTime() {
        if (totalChanged) {
            lastTotal = Duration.ZERO;
            for (TimeEntry entry : getTimes()) {
                lastTotal = lastTotal.plus(entry.getDuration());
            }
            totalChanged = false;
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
        @SerializedName("e")
        private LocalDateTime end;

        private transient boolean durationChanged;
        private transient Duration lastDuration;

        /**
         * For gson to use
         */
        private TimeEntry() {
            Bukkit.getLogger().info("timeentry");
            durationChanged = true;
            totalChanged = true;
            lastDuration = Duration.ZERO;
        }

        public TimeEntry(LocalDateTime start, LocalDateTime end) {
            this();
            this.start = start;
            this.end = end;
        }

        public void setStart(LocalDateTime start) {
            durationChanged = true;
            totalChanged = true;
            this.start = start;
        }

        public void setEnd(LocalDateTime end) {
            durationChanged = true;
            totalChanged = true;
            this.start = end;
        }

        public Duration getDuration() {
            if (durationChanged) {
                lastDuration = Duration.between(start, end);
                durationChanged = false;
            }
            return lastDuration;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "TimeEntry[start=" + start + ",end=" + end + "]";
        }
    }
}
