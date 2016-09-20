package com.deanveloper.playtime.storage;

import com.deanveloper.playtime.PlayTime;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class JsonStorage implements Storage {
    private final File storage;
    private final JsonObject root;
    private final JsonElement version;
    private final JsonObject players;
    private final Map<UUID, LocalDateTime> current;

    JsonStorage() {
        storage = new File(PlayTime.getInstance().getDataFolder(), "players.root");
        JsonObject temp;
        try {
            temp = new JsonParser().parse(new FileReader(storage)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            temp = new JsonObject();
            temp.addProperty("version", 1);
            temp.add("version", new JsonObject());
        }
        root = temp;
        version = root.get("version");
        players = root.getAsJsonObject("players");
        current = new HashMap<>();
    }

    public JsonObject getRoot() {
        return root;
    }

    @Override
    public void save() {
        try {
            new FileWriter(storage).write(root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerEntry get(UUID id) {
        return PlayTime.GSON.fromJson(players.get(id.toString()), PlayerEntry.class);
    }

    @Override
    public void update(PlayerEntry entry) {
        players.add(entry.getId().toString(), PlayTime.GSON.toJsonTree(entry));
    }

    @Override
    public Map<UUID, PlayerEntry> getPlayers() {
        synchronized (players) {
            return players.entrySet().parallelStream()
                    .collect(Collectors.toMap(
                            entry -> UUID.fromString(entry.getKey()),
                            entry -> PlayTime.GSON.fromJson(entry.getValue(), PlayerEntry.class)
                    ));
        }
    }
}
