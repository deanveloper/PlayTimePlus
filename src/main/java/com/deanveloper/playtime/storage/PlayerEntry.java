package com.deanveloper.playtime.storage;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;
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

    /**
     * Use for players who have never logged on before, otherwise
     * please use {@link Storage#get(UUID)}
     */
    public PlayerEntry(UUID id) {
        this.id = id;
        this.times = new ArrayList<>();
    }

    /**
     * Updates the players online time, should be called every minute.
     */
    public static void updatePlayers() {
        for (Map.Entry<UUID, LocalDateTime> online : onlineTimes.entrySet()) {
            if (PlayTime.getEssentialsHook().isAfk(online.getKey())) {
                continue;
            }
            LocalDateTime start = onlineTimes.get(online.getKey());
            PlayerEntry entry = PlayTime.getPlayerDb().get(online.getKey());

            entry.times.stream()
                    .filter(time -> time.start.equals(start))
                    .findAny()
                    .ifPresent(time -> time.duration = Duration.between(start, LocalDateTime.now()));

            PlayTime.getPlayerDb().update(entry);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return Utils.getName(id);
    }

    public List<TimeEntry> getTimes() {
        return times;
    }

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

            times.stream()
                    .filter(time -> time.start.equals(start))
                    .findAny()
                    .ifPresent(time -> time.duration = Duration.between(start, LocalDateTime.now()));

            PlayTime.getPlayerDb().update(this);
        }
    }

    public Duration getTotalTime() {
        Duration total = Duration.ZERO;
        for (TimeEntry entry : getTimes()) {
            total = total.plus(entry.duration);
        }
        return total;
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
    }
}
