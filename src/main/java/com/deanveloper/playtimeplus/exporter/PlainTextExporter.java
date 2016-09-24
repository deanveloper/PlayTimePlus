package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Export to a plaintext file
 *
 * @author Dean B
 */
public class PlainTextExporter implements Exporter {

    @Override
    public void export(List<PlayerEntry> entries) {
        List<String> formatted = new ArrayList<>(entries.size());

        formatted.addAll(
                entries.stream()
                        .map(entry -> String.format(
                                "%s (aka %s) has been on for %s",
                                entry.getId(),
                                entry.getName(),
                                Utils.format(entry.getTotalTime()))
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
