package com.deanveloper.playtimeplus.util.gson;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author Dean
 */
public class PlayerEntryConverter implements JsonDeserializer<PlayerEntry> {
    @Override
    public PlayerEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject obj = json.getAsJsonObject();
        UUID id = PlayTimePlus.GSON.fromJson(obj.get("i"), UUID.class);
        PlayerEntry entry = new PlayerEntry(id);

        for(JsonElement elem : obj.get("t").getAsJsonArray()) {
            JsonObject tJson = elem.getAsJsonObject();
            PlayerEntry.TimeEntry time = new PlayerEntry.TimeEntry(
                    PlayTimePlus.GSON.fromJson(tJson.get("s"), LocalDateTime.class),
                    PlayTimePlus.GSON.fromJson(tJson.get("e"), LocalDateTime.class),
                    PlayTimePlus.GSON.fromJson(tJson.get("i"), UUID.class)
            );

            entry.getTimes().add(time);
        }

        return entry;
    }
}
