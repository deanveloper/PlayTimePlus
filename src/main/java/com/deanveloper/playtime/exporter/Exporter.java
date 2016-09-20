package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.storage.Storage;

import java.util.List;
import java.util.UUID;

/**
 * A way to export to a file
 *
 * @author Dean B
 */
public interface Exporter {
    void export(List<Storage.PlayerEntry> entries);

    default String getFileName() {
        return "PlayerReport_" + System.currentTimeMillis();
    }
}
