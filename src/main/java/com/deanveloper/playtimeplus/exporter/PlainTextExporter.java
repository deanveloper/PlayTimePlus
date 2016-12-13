package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Export to a plaintext file
 *
 * @author Dean B
 */
public class PlainTextExporter implements Exporter {

    @Override
    public void export(Map<UUID, Set<TimeEntry>> entries) {
        List<String> formatted = new ArrayList<>(entries.size());

        formatted.addAll(
                entries.entrySet().stream()
                        .map(entry -> String.format(
                                "%s (aka %s) has been on for %s",
                                entry.getKey(),
                                Utils.getNameForce(entry.getKey()),
                                PlayTimePlus.getManager().onlineTime(entry.getKey()).getSeconds())
                        ).collect(Collectors.toList())
        );

        try {
            Files.write(
                    Paths.get(PlayTimePlus.getInstance().getDataFolder().getAbsolutePath(), getFileName() + ".txt"),
                    formatted
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
