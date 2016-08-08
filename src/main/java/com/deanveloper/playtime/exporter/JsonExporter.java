package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;
import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Export to a Json file
 *
 * @author Dean B
 */
public class JsonExporter extends Exporter {
    @Override
    protected void exportFile(List<String> names, List<UUID> ids, List<Integer> secondsOnline) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (int i = 0; i < names.size(); i++) {
            JsonObject data = new JsonObject();
            data.addProperty("name", names.get(i));
            data.addProperty("id", ids.get(i).toString());
            data.addProperty("secondsOnline", secondsOnline.get(i));
            arr.add(data);
        }

        root.add("people", arr);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(root.toString());
        String prettyJson = gson.toJson(je);

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
