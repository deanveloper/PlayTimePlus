package com.deanveloper.playtimeplus.storage.json;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dean
 */
class JsonConverter {
    static JsonObject convertJson(JsonObject root) {
        JsonObject mutating = root;

        if (mutating.get("version").getAsInt() == 1) {
            mutating = oneToTwo(root);
        }

        return mutating;
    }

    private static JsonObject oneToTwo(JsonObject root) {
        root.addProperty("version", 2);
        JsonArray players = new JsonArray();
        for (JsonElement playerElem : root.getAsJsonArray("players")) {
            JsonObject oldPlayer = playerElem.getAsJsonObject();
            JsonObject newPlayer = new JsonObject();
            newPlayer.add("i", oldPlayer.get("i"));

            JsonArray newTimes = new JsonArray();
            for (JsonElement timeElem : oldPlayer.getAsJsonArray("t")) {
                JsonObject oldTime = timeElem.getAsJsonObject();
                JsonObject newTime = new JsonObject();
                newTime.add("s", oldTime.get("s"));
                newTime.add("e", oldTime.get("e"));
                newTime.add("i", newPlayer.get("i"));
                newTimes.add(newTime);
            }

            players.add(newPlayer);
        }

        root.add("players", players);

        return root;
    }
}
