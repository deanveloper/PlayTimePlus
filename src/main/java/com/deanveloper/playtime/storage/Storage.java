package com.deanveloper.playtime.storage;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dean
 */
public interface Storage {
    void save();

    PlayerEntry get(UUID id);
    void update(PlayerEntry entry);
    Map<UUID, PlayerEntry> getPlayers();

    class PlayerEntry {
        private UUID id;
        private String name;
        private List<TimeEntry> times;
        private transient Duration total = null;

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<TimeEntry> getTimes() {
            return times;
        }

        public Duration totalTime() {
            Duration total = Duration.ZERO;
            for(TimeEntry entry : getTimes()) {
                total = total.plus(entry.duration);
            }
            return total;
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
}
