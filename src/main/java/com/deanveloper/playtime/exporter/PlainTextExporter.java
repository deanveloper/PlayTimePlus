package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Export to a plaintext file
 *
 * @author Dean B
 */
public class PlainTextExporter extends Exporter {
    @Override
    protected void exportFile(List<String> names, List<UUID> ids, List<Integer> secondsOnline) {
        File f = new File(PlayTime.getInstance().getDataFolder(), getFileName() + ".txt");

        try (PrintWriter writer = new PrintWriter(f, "utf8")){
            for(int i = 0; i < names.size(); i++) {
                writer.printf("%s (aka %s) has been on for %s",
                    ids.get(i),
                    names.get(i),
                    Utils.format(secondsOnline.get(i))
                );
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
