package com.deanveloper.playtime.exporter;

import java.util.List;
import java.util.UUID;

/**
 * A way to export to a file
 *
 * @author Dean B
 */
public abstract class Exporter {
    public final void export(List<String> names, List<UUID> ids, List<Integer> secondsOnline) {
        if (names.size() != ids.size() || names.size() != secondsOnline.size()) {
            throw new IllegalArgumentException("The lists aren't the same size!");
        }
        exportFile(names, ids, secondsOnline);
    }


    protected abstract void exportFile(List<String> names, List<UUID> ids, List<Integer> secondsOnline);

    protected String getFileName() {
        return "PlayerReport_" + System.currentTimeMillis();
    }
}
