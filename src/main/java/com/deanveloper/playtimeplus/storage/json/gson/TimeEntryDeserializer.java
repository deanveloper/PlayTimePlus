package com.deanveloper.playtimeplus.storage.json.gson;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Created by Dean on 7/18/2017.
 */
public class TimeEntryDeserializer implements JsonDeserializer<TimeEntry> {
	@Override
	public TimeEntry deserialize(JsonElement elem, Type t, JsonDeserializationContext ctx) throws JsonParseException {
		if (!elem.isJsonObject()) {
			throw new JsonParseException("TimeEntry must be a JsonObject!");
		}
		JsonObject json = (JsonObject) elem;

		try {
			return new TimeEntry(
					PlayTimePlus.GSON.fromJson(json.get("s"), LocalDateTime.class),
					PlayTimePlus.GSON.fromJson(json.get("e"), LocalDateTime.class)
			);
		} catch (NumberFormatException e) {
			throw new JsonParseException("Error initializing a LocalDateTime", e);
		}
	}
}
