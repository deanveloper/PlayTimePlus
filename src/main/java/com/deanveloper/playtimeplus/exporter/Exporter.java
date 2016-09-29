package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.storage.PlayerEntry;

import java.util.SortedSet;

/**
 * A way to export to a file
 *
 * @author Dean B
 */
public interface Exporter {
    void export(SortedSet<PlayerEntry> entries);

    default String getFileName() {
        return "PlayerReport_" + System.currentTimeMillis();
    }
}
