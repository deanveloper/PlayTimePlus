package com.deanveloper.playtimeplus.storage.json;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.NavigableSet;
import java.util.UUID;

/**
 * @author Dean
 */
public class JsonConverter {
    public static void convertJson(JsonObject root) {
        if(root.get("version").getAsInt() == 1) {
            oneToTwo(root);
        }
    }

    private static void oneToTwo(JsonObject root) {
        Type type = new TypeToken<Map<UUID, PlayerEntry>>() {
        }.getType();
        Map<UUID, PlayerEntry> map = PlayTimePlus.GSON.fromJson(root.get("players"), type);
        root.add("players", PlayTimePlus.GSON.toJsonTree(map.values()));
    }
}
