package com.deanveloper.playtimeplus.storage.json;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dean
 */
class JsonConverter {
    static void convertJson(JsonObject root) {
        if (root.get("version").getAsInt() == 1) {
            oneToTwo(root);
        }
    }

    private static void oneToTwo(JsonObject root) {
        Type type = new TypeToken<Map<UUID, PlayerEntry>>() {
        }.getType();
        Map<UUID, PlayerEntry> map = PlayTimePlus.GSON.fromJson(root.get("players"), type);

        JsonArray players = new JsonArray();
        for (PlayerEntry value : map.values()) {
            JsonObject pJson = new JsonObject();
            pJson.add("i", PlayTimePlus.GSON.toJsonTree(value.getId()));

            JsonArray arr = new JsonArray();
            for (PlayerEntry.TimeEntry time : value.getTimes()) {
                JsonObject tJson = new JsonObject();
                tJson.add("s", PlayTimePlus.GSON.toJsonTree(time.getStart()));
                tJson.add("e", PlayTimePlus.GSON.toJsonTree(time.getEnd()));
                tJson.add("i", PlayTimePlus.GSON.toJsonTree(value.getId()));
                arr.add(tJson);
            }

            players.add(pJson);
        }

        root.add("players", players);
    }
}
