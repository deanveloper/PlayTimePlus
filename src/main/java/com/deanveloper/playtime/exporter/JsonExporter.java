package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.storage.PlayerEntry;
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
            data.add("id", PlayTime.GSON.toJsonTree(entry.getId()));
            data.addProperty("idString", entry.getId().toString());
            data.addProperty("secondsOnline", entry.getTotalTime().getSeconds());
            arr.add(data);
        }

        root.add("players", arr);

        Gson prettyPrint = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyPrint.toJson(root);

        try {
            Files.write(
                    Paths.get(PlayTime.getInstance().getDataFolder().getAbsolutePath(), getFileName() + ".txt"),
                    Arrays.asList(prettyJson.split("\\n"))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
