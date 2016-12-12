package com.deanveloper.playtimeplus.storage.json;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.Storage;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Dean
 */
public class JsonStorage implements Storage {
    private static final int VERSION = 1;
    private File storage;
    private Map<UUID, PlayerEntry> players;
    private NavigableSet<PlayerEntry> sortedPlayers;

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

        Type type = new TypeToken<NavigableSet<PlayerEntry>>() {
        }.getType();

        NavigableSet<PlayerEntry> temp = PlayTimePlus.GSON.fromJson(root.get("players"), type);
        if (temp == null) {
            sortedPlayers = new TreeSet<>();
        } else {
            sortedPlayers = temp;
        }

        sortedPlayers.forEach(PlayerEntry::mutated);

        players = new HashMap<>(sortedPlayers.size());
        for (PlayerEntry entry : sortedPlayers) {
            players.put(entry.getId(), entry);
        }
    }

    @Override
    public void save() {
        // Update the players before saving
        for (Player p : Bukkit.getOnlinePlayers()) {
            get(p.getUniqueId()).updateLatestTime();
        }
        try {
            JsonObject root = new JsonObject();
            root.addProperty("version", VERSION);
            root.add("players", PlayTimePlus.GSON.toJsonTree(sortedPlayers));
            Files.write(PlayTimePlus.GSON.toJson(root), storage, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerEntry get(UUID id) {
        return players.get(id);
    }

    @Override
    public Map<UUID, PlayerEntry> getPlayers() {
        return players;
    }

    @Override
    public NavigableSet<PlayerEntry> getPlayersSorted() {
        return sortedPlayers;
    }
}
