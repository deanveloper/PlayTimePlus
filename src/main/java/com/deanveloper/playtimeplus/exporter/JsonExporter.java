package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Export to a Json file
 *
 * @author Dean B
 */
public class JsonExporter implements Exporter {

    @Override
    public void export(List<PlayerEntry> entries) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (PlayerEntry entry : entries) {
            JsonObject data = new JsonObject();
            data.addProperty("name", entry.getName());
            data.add("id", PlayTimePlus.GSON.toJsonTree(entry.getId()));
            data.addProperty("idString", entry.getId().toString());

            JsonArray times = new JsonArray();

            for(PlayerEntry.TimeEntry tEntry : entry.getTimes()) {
                JsonObject obj = new JsonObject();
                obj.add("start", PlayTimePlus.GSON.toJsonTree(tEntry.getStart()));
                obj.add("end", PlayTimePlus.GSON.toJsonTree(tEntry.getEnd()));
            }

            data.add("times", times);
            data.add("totalTime", PlayTimePlus.GSON.toJsonTree(entry.getTotalTime()));
            arr.add(data);
        }

        root.add("players", arr);

        Gson prettyPrint = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyPrint.toJson(root);

        try {
            Files.write(
                    Paths.get(PlayTimePlus.getInstance().getDataFolder().getAbsolutePath(), getFileName() + ".txt"),
                    Arrays.asList(prettyJson.split("\\n"))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
