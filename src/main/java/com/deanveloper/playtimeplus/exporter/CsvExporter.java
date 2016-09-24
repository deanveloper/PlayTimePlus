package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Comma separated list
 *
 * @author Dean B
 */
public class CsvExporter implements Exporter {

    @Override
    public void export(List<PlayerEntry> entries) {
        List<String> formatted = new ArrayList<>(entries.size());

        formatted.addAll(
                entries.stream()
                        .map(entry -> String.format("%s,%s,%d",
                                entry.getId(),
                                entry.getName(),
                                entry.getTotalTime().getSeconds()))
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
