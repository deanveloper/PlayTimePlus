package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.storage.TimeEntry;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A way to export to a file
 *
 * @author Dean B
 */
public interface Exporter {
    void export(Map<UUID, Set<TimeEntry>> entries);

    default String getFileName() {
        return "PlayerReport_" + System.currentTimeMillis();
    }
}
