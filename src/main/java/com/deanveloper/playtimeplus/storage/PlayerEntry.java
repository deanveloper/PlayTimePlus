package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class PlayerEntry implements Comparable<PlayerEntry>, Cloneable {

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
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (PlayTimePlus.getEssentialsHook().isAfk(p)) {
                continue;
            }
            PlayerEntry entry = PlayTimePlus.getStorage().get(p.getUniqueId());

            TimeEntry current = entry.getTimes().stream()
                    .max((te1, te2) -> te1.getStart().compareTo(te2.getStart()))
                    .orElseThrow(() -> new RuntimeException("No max found for " + p.getDisplayName()));

            current.setEnd(LocalDateTime.now());

            entry.mutated();
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

    public void mutated() {
        totalChanged = true;
    }

    /**
     * Set the person to being online or not
     */
    public void setOnline(boolean online) {
        if (online) {
            LocalDateTime now = LocalDateTime.now();
            TimeEntry time = new TimeEntry(now, now);
            getTimes().add(time);
        } else {
            TimeEntry current = getTimes().stream()
                    .max((te1, te2) -> te1.getStart().compareTo(te2.getStart()))
                    .orElseThrow(() -> new RuntimeException("No max found for " + this.getName()));

            current.setEnd(LocalDateTime.now());
        }

        mutated();
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

    @Override
    public PlayerEntry clone() {
        PlayerEntry clone;
        try {
            clone = (PlayerEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        clone.times = new ArrayList<>();
        clone.getTimes().addAll(times.stream()
                .map(TimeEntry::clone)
                .collect(Collectors.toList())
        );
        return clone;
    }

    public class TimeEntry implements Cloneable {
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
            mutated();
            this.start = start;
        }

        public void setEnd(LocalDateTime end) {
            mutated();
            this.end = end;
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

        public void mutated() {
            PlayerEntry.this.mutated();
            durationChanged = true;
        }

        @Override
        public String toString() {
            return "TimeEntry[start=" + start + ",end=" + end + "]";
        }

        @Override
        public TimeEntry clone() {
            try {
                return (TimeEntry) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
