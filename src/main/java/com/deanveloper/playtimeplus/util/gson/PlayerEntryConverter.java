package com.deanveloper.playtimeplus.util.gson;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Dean
 */
public class PlayerEntryConverter implements JsonDeserializer<PlayerEntry> {
    @Override
    public PlayerEntry deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) {
        JsonObject json = root.getAsJsonObject();
        PlayerEntry entry = new PlayerEntry(PlayTimePlus.GSON.fromJson(json.get("i"), UUID.class));
        JsonArray times = json.getAsJsonArray("t");

        for(JsonElement elem : times) {
            JsonObject time = elem.getAsJsonObject();
            entry.getTimes().add(
                    entry.new TimeEntry(
                            PlayTimePlus.GSON.fromJson(time.get("s"), LocalDateTime.class),
                            PlayTimePlus.GSON.fromJson(time.get("e"), LocalDateTime.class)
                    )
            );
        }

        return entry;
    }

    //Serialization can be handled by GSON automatically
}
