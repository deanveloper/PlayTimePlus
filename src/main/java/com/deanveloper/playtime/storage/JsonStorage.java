package com.deanveloper.playtime.storage;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.gson.DurationConverter;
import com.deanveloper.playtime.util.gson.LocalDateTimeConverter;
import com.google.gson.*;

import java.io.*;
import java.time.Duration;
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
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
            .registerTypeAdapter(Duration.class, new DurationConverter())
            .create();
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
        return gson.fromJson(players.get(id.toString()), PlayerEntry.class);
    }

    @Override
    public void update(PlayerEntry entry) {
        players.add(entry.getId().toString(), gson.toJsonTree(entry));
    }

    @Override
    public Map<UUID, PlayerEntry> getPlayers() {
        synchronized (players) {
            return players.entrySet().parallelStream()
                    .collect(Collectors.toMap(
                            entry -> UUID.fromString(entry.getKey()),
                            entry -> gson.fromJson(entry.getValue(), PlayerEntry.class)
                    ));
        }
    }
}
