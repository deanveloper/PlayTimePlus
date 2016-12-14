package com.deanveloper.playtimeplus.exporter;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Export to a Json file
 *
 * @author Dean B
 */
public class JsonExporter implements Exporter {

    @Override
    public void export(Map<UUID, NavigableSet<TimeEntry>> entries) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (Map.Entry<UUID, NavigableSet<TimeEntry>> entry : entries.entrySet()) {
            JsonObject data = new JsonObject();
            data.addProperty("name", Utils.getNameForce(entry.getKey()));
            data.add("id", PlayTimePlus.GSON.toJsonTree(entry.getKey()));
            data.addProperty("idString", entry.getKey().toString());

            JsonArray times = new JsonArray();

            for(TimeEntry tEntry : entry.getValue()) {
                JsonObject obj = new JsonObject();
                obj.add("start", PlayTimePlus.GSON.toJsonTree(tEntry.getStart()));
                obj.add("end", PlayTimePlus.GSON.toJsonTree(tEntry.getEnd()));
                times.add(obj);
            }

            data.add("times", times);
            data.add("totalTime", PlayTimePlus.GSON.toJsonTree(
                    PlayTimePlus.getManager().onlineTime(entry.getKey())
            ));
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
