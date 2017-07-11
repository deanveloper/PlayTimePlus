package com.deanveloper.playtimeplus.storage.json;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.UUID;

/**
 * @author Dean
 */
public class JsonManager implements Manager {
	private static final int VERSION = 1;
	private File storage;
	private Map<UUID, NavigableSet<TimeEntry>> players = new HashMap<>();

	@Override
	public void init() {
		storage = new File(PlayTimePlus.getInstance().getDataFolder(), "players.json");

		// Parse the file
		JsonObject root;
		try {
			String line = Files.readFirstLine(storage, Charset.defaultCharset());
			if (line == null || line.isEmpty()) {
				throw new FileNotFoundException("Just in case");
			}
			root = new JsonParser().parse(line).getAsJsonObject();
		} catch (FileNotFoundException e) {

			// If we are seeing the json for the first time
			root = new JsonObject();
			root.addProperty("version", VERSION);
			root.add("players", new JsonArray());
			save();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// If the version is incorrect
		if (root.get("version").getAsInt() != VERSION) {
			Bukkit.getLogger().info("Player storage is the wrong version.");
			Bukkit.getLogger().info("Old: " + root.get("version").getAsInt() + " | Current: " + VERSION);
			root = JsonConverter.convertJson(root);
		}

		Type timeEntrySet = new TypeToken<NavigableSet<TimeEntry>>() {
		}.getType();

		JsonObject json = root.getAsJsonObject("players");
		if (json == null) {
			players = new HashMap<>();
		} else {
			players = new HashMap<>(json.entrySet().size());
			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {

				UUID id = UUID.fromString(entry.getKey());
				NavigableSet<TimeEntry> times =
						PlayTimePlus.GSON.fromJson(entry.getValue().getAsJsonArray(), timeEntrySet);

				players.put(id, times);
			}
		}
	}

	@Override
	public void save() {
		// Update the players before saving
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateLastCount(p.getUniqueId());
		}
		try {
			JsonObject root = new JsonObject();
			root.addProperty("version", VERSION);
			root.add("players", PlayTimePlus.GSON.toJsonTree(players));
			Files.write(PlayTimePlus.GSON.toJson(root), storage, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<UUID, NavigableSet<TimeEntry>> getMap() {
		return players;
	}
}
