package com.deanveloper.playtimeplus.util.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @author Dean
 */
public class DurationConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonElement elem, Type type, JsonDeserializationContext context) {

        try {
            return Duration.ofSeconds(elem.getAsInt());
        } catch (NumberFormatException e) {
            throw new JsonParseException("Error initializing a Duration", e);
        }
    }

    @Override
    public JsonElement serialize(Duration time, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(time.getSeconds());
    }
}
