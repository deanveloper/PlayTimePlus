package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Comma separated list
 *
 * @author Dean B
 */
public class CsvExporter extends Exporter {
    @Override
    protected void exportFile(List<String> names, List<UUID> ids, List<Integer> secondsOnline) {
        File f = new File(PlayTime.getInstance().getDataFolder(), getFileName() + ".csv");

        try (PrintWriter writer = new PrintWriter(f, "utf8")) {
            for (int i = 0; i < names.size(); i++) {
                writer.printf("%s,%s,%d",
                        ids.get(i),
                        names.get(i),
                        secondsOnline.get(i)
                );
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
