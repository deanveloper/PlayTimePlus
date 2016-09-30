package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.PlayTimePlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

/**
 * @author Dean
 */
public class BinaryStorage implements Storage {
    private final File storage;
    private final Map<UUID, PlayerEntry> players;
    private final NavigableSet<PlayerEntry> sortedPlayers;
    private final int version;

    BinaryStorage() {
        storage = new File(PlayTimePlus.getInstance().getDataFolder(), "players.playtimeplus");

        // Parse the file
        int tempVersion;
        NavigableSet<PlayerEntry> tempPlayers;
        try (
                FileInputStream input = new FileInputStream(storage);
                ObjectInputStream objIn = new ObjectInputStream(input)
        ) {

            tempVersion = objIn.readInt();
            tempPlayers = (NavigableSet<PlayerEntry>) objIn.readObject();
        } catch (FileNotFoundException e) {
            tempVersion = 1;
            tempPlayers = new TreeSet<>();
            save();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        version = tempVersion;
        sortedPlayers = tempPlayers;

        players = new HashMap<>(sortedPlayers.size());
        for (PlayerEntry entry : sortedPlayers) {
            players.put(entry.getId(), entry);
        }
    }

    @Override
    public PlayerEntry get(UUID id) {
        return players.get(id);
    }

    @Override
    public void update(PlayerEntry entry) {
        if (sortedPlayers.remove(entry)) {
            sortedPlayers.add(entry);
        }
    }

    @Override
    public void save() {
        // Update the players before saving
        for (Player p : Bukkit.getOnlinePlayers()) {
            get(p.getUniqueId()).update();
        }

        try (
                FileOutputStream output = new FileOutputStream(storage);
                ObjectOutputStream objOut = new ObjectOutputStream(output)
        ) {

            objOut.writeInt(version);
            objOut.writeObject(sortedPlayers);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
