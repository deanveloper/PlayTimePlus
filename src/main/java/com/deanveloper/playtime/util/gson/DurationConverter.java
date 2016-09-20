package com.deanveloper.playtime.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @author Dean
 */
public class DurationConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonElement elem, Type type, JsonDeserializationContext context) {
        if (!elem.isJsonPrimitive() || elem.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("Duration must be a Number!");
        }
        Number json = elem.getAsJsonPrimitive().getAsNumber();

        return Duration.ofSeconds(json.intValue());
    }

    @Override
    public JsonElement serialize(Duration time, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(time.getSeconds());
    }
}
