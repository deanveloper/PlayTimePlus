package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class PlayerEntry implements Comparable<PlayerEntry>, Cloneable, Serializable {

    public static long serialVersionUID = 2L;

    @SerializedName("i")
    private UUID id;
    @SerializedName("t")
    private NavigableSet<TimeEntry> times;

    private transient Duration lastTotal;
    private transient LocalDateTime updateAgainAfter;

    /**
     * Use for players who have never logged on before, otherwise
     * please use {@link Storage#get(UUID)}
     */
    public PlayerEntry(UUID id) {
        this.id = id;
        this.times = new TreeSet<>();
        lastTotal = Duration.ZERO;
        updateAgainAfter = LocalDateTime.MIN;
    }

    /**
     * This method is called internally so you will -probably- never need to use it.
     * Basically it updates the player's time to the most recent time if they are online.
     */
    public void updateLatestTime() {
        if (Bukkit.getPlayer(getId()) == null || PlayTimePlus.getStorage().get(getId()) != this) {
            return;
        }

        if (!times.isEmpty()) {
            times.last().setEnd(LocalDateTime.now());
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
        return Utils.getNameForce(id);
    }

    /**
     * The times that the player has been online
     */
    public SortedSet<TimeEntry> getTimes() {
        updateLatestTime();
        return times;
    }

    /**
     * Call this if you ever change the contents of getTimes()
     */
    public void mutated() {
        if(updateAgainAfter.isBefore(LocalDateTime.now())) {
            lastTotal = Duration.ZERO;
            for (TimeEntry entry : times) {
                lastTotal = lastTotal.plus(entry.getDuration());
            }

            updateAgainAfter = LocalDateTime.now().plus(2, ChronoUnit.SECONDS);

            PlayTimePlus.getStorage().update(this);
        }
    }

    /**
     * Set the person to being online or not
     */
    public void setOnline(boolean online) {
        if (online) {
            LocalDateTime now = LocalDateTime.now();
            TimeEntry time = new TimeEntry(now, now, id);
            getTimes().add(time);

            mutated();
        } else {
            updateLatestTime();
        }
    }

    /**
     * The total time the player has been online
     */
    public Duration getTotalTime() {
        updateLatestTime();
        return lastTotal;
    }

    @Override
    public String toString() {
        return "PlayerEntry[id=" + id + ",times=" + times + "]";
    }

    @Override
    public int compareTo(PlayerEntry o) {
        return lastTotal.compareTo(o.lastTotal);
    }

    @Override
    public PlayerEntry clone() {
        PlayerEntry clone;
        try {
            clone = (PlayerEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        clone.times = new TreeSet<>();
        clone.lastTotal = lastTotal;
        clone.times.addAll(times.stream()
                .map(TimeEntry::clone)
                .collect(Collectors.toSet())
        );
        return clone;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        lastTotal = Duration.ZERO;
        for (TimeEntry entry : times) {
            lastTotal = lastTotal.plus(entry.getDuration());
        }

        updateAgainAfter = LocalDateTime.now().plus(2, ChronoUnit.SECONDS);
    }

    public static class TimeEntry implements Cloneable, Comparable<TimeEntry>, Serializable {
        public static long serialVersionUID = 1L;

        @SerializedName("s")
        private LocalDateTime start;
        @SerializedName("e")
        private LocalDateTime end;
        @SerializedName("i")
        private UUID parent;

        private transient Duration lastDuration;
        private transient boolean isClone;

        public TimeEntry(LocalDateTime start, LocalDateTime end, UUID parent) {
            this.start = start;
            this.end = end;
            this.parent = parent;
            lastDuration = Duration.between(start, end);
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
            mutated();
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
            mutated();
        }

        public Duration getDuration() {
            return lastDuration;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void mutated() {
            lastDuration = Duration.between(start, end);
            if(parent != null && !isClone) {
                PlayerEntry entry = PlayTimePlus.getStorage().get(parent);
                entry.times.remove(this);
                entry.times.add(this);
                entry.mutated();
            }
        }

        @Override
        public String toString() {
            return "TimeEntry[parent=" + parent + ",start=" + start + ",end=" + end + "]";
        }

        @Override
        public TimeEntry clone() {
            try {
                TimeEntry clone = (TimeEntry) super.clone();
                clone.isClone = true;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int compareTo(TimeEntry o) {
            return start.compareTo(o.start);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TimeEntry) {
                TimeEntry entry = ((TimeEntry) o);

                return this.getStart().equals(entry.getStart()) && this.getEnd().equals(entry.getEnd());
            }

            return false;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            lastDuration = Duration.between(start, end);
            isClone = false;
        }
    }
}
