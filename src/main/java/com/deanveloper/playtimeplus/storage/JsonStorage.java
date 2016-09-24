package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
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

    JsonStorage() {
        storage = new File(PlayTimePlus.getInstance().getDataFolder(), "players.json");
        JsonObject temp;
        try {
            String line = Files.readFirstLine(storage, Charset.defaultCharset());
            if (line == null || line.isEmpty()) {
                throw new FileNotFoundException("Just in case");
            }
            temp = new JsonParser().parse(line).getAsJsonObject();
        } catch (FileNotFoundException e) {
            temp = new JsonObject();
            temp.addProperty("version", 1);
            temp.add("players", new JsonObject());
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        root = temp;
        version = root.get("version");
        players = root.getAsJsonObject("players");
    }

    public JsonObject getRoot() {
        return root;
    }

    @Override
    public void createIfNotPresent(UUID id) {
        if(players.get(id.toString()) != null) {
            return;
        }

        players.add(id.toString(), PlayTimePlus.GSON.toJsonTree(new PlayerEntry(id)));
    }

    @Override
    public void save() {
        try {
            Files.write(PlayTimePlus.GSON.toJson(root), storage, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(PlayerEntry entry) {
        players.add(entry.getId().toString(), PlayTimePlus.GSON.toJsonTree(entry));
    }

    @Override
    public PlayerEntry get(UUID id) {
        return PlayTimePlus.GSON.fromJson(players.get(id.toString()), PlayerEntry.class);
    }

    @Override
    public Map<UUID, PlayerEntry> getPlayers() {
        synchronized (players) {
            return players.entrySet().parallelStream()
                    .collect(Collectors.toMap(
                            entry -> UUID.fromString(entry.getKey()),
                            entry -> PlayTimePlus.GSON.fromJson(entry.getValue(), PlayerEntry.class)
                    ));
        }
    }
}
