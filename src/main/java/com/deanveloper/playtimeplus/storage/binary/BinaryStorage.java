package com.deanveloper.playtimeplus.storage.binary;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

/**
 * @author Dean
 */
public class BinaryStorage implements Storage {
    private File storage;
    private Map<UUID, PlayerEntry> players;
    private NavigableSet<PlayerEntry> sortedPlayers;
    private static final int VERSION = 1;

    @Override
    public void init() {
        storage = new File(PlayTimePlus.getInstance().getDataFolder(), "players.playtimeplus");

        // Parse the file
        int tempVersion;
        try (
                FileInputStream input = new FileInputStream(storage);
                ObjectInputStream objIn = new ObjectInputStream(input)
        ) {
            tempVersion = objIn.readInt();

            if(tempVersion != VERSION) {
                sortedPlayers = (NavigableSet<PlayerEntry>) BinaryConverter.convertBinary(objIn);
            } else {
                sortedPlayers = (NavigableSet<PlayerEntry>) objIn.readObject();
            }

        } catch (FileNotFoundException e) {
            sortedPlayers = new TreeSet<>();
            save();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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
    public void save() {
        // Update the players before saving
        for (Player p : Bukkit.getOnlinePlayers()) {
            get(p.getUniqueId()).updateLatestTime();
        }

        try (
                FileOutputStream output = new FileOutputStream(storage);
                ObjectOutputStream objOut = new ObjectOutputStream(output)
        ) {
            objOut.writeInt(VERSION);
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
