package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Comma separated list
 *
 * @author Dean B
 */
public class CsvExporter implements Exporter {

    @Override
    public void export(Map<UUID, NavigableSet<TimeEntry>> entries) {
        List<String> formatted = new ArrayList<>(entries.size());

        formatted.addAll(
                entries.entrySet().stream()
                        .map(entry -> String.format("%s,%s,%d",
                                entry.getKey(),
                                Utils.getNameForce(entry.getKey()),
                                PlayTimePlus.getManager().onlineTime(entry.getKey()).getSeconds()))
                        .collect(Collectors.toList())
        );

        try {
            Files.write(
                    PlayTimePlus.getInstance().getDataFolder().toPath().resolve(getFileName() + ".csv"),
                    formatted
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
