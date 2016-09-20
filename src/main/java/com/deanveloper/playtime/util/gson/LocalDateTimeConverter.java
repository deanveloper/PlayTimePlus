package com.deanveloper.playtime.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Dean
 */
public class LocalDateTimeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement elem, Type type, JsonDeserializationContext context) {
        if (!elem.isJsonObject()) {
            throw new JsonParseException("LocalDateTime must be a JsonObject!");
        }
        JsonObject json = (JsonObject) elem;

        Arrays.asList("Y", "M", "d", "h", "m", "s").forEach(string -> {
            if (json.get(string) == null || !json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
                throw new JsonParseException(string + " is not a number! it is " + json.get(string));
            }
        });
        return LocalDateTime.of(
                json.get("Y").getAsInt(),
                json.get("M").getAsInt(),
                json.get("d").getAsInt(),
                json.get("h").getAsInt(),
                json.get("m").getAsInt(),
                json.get("s").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(LocalDateTime time, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("Y", time.getYear());
        json.addProperty("M", time.getMonthValue());
        json.addProperty("d", time.getDayOfMonth());
        json.addProperty("h", time.getHour());
        json.addProperty("m", time.getMinute());
        json.addProperty("s", time.getSecond());
        return json;
    }
}
