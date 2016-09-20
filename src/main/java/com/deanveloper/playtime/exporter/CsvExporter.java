package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.storage.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Comma separated list
 *
 * @author Dean B
 */
public class CsvExporter implements Exporter {

    @Override
    public void export(List<Storage.PlayerEntry> entries) {
        List<String> formatted = new ArrayList<>(entries.size());

        formatted.addAll(
                entries.stream()
                        .map(entry -> String.format("%s,%s,%d",
                                entry.getId(),
                                entry.getName(),
                                entry.totalTime().getSeconds()))
                        .collect(Collectors.toList())
        );

        try {
            Files.write(
                    PlayTime.getInstance().getDataFolder().toPath().resolve(getFileName() + ".csv"),
                    formatted
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
