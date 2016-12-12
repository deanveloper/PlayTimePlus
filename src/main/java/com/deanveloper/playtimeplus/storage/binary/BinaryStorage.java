package com.deanveloper.playtimeplus.storage.binary;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Version 1's File Format:
 *
 * VERSION (int)
 * each player: 0xFF (byte), uniqueId (UUID), time (below), 0x00 (byte)
 * each time:   0x11 (byte), start (LocalDateTime), end (LocalDateTime), 0x00 (byte)
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
        int streamVersion;
        try (
                FileInputStream input = new FileInputStream(storage);
                ObjectInputStream objIn = new ObjectInputStream(input)
        ) {
            streamVersion = objIn.readInt();

            if (streamVersion != VERSION) {
                sortedPlayers = (NavigableSet<PlayerEntry>) BinaryConverter.convertBinary(streamVersion, objIn);
            } else {
                int read;
                while ((read = objIn.read()) == 0xFF) {
                    PlayerEntry entry = new PlayerEntry((UUID) objIn.readObject());

                    while ((read = objIn.read()) == 0x11) {
                        PlayerEntry.TimeEntry time = new PlayerEntry.TimeEntry(
                                (LocalDateTime) objIn.readObject(),
                                (LocalDateTime) objIn.readObject(),
                                entry.getId()
                        );
                        entry.getTimes().add(time);
                    }
                    entry.mutated();

                    // If anything other than a 0x00 bit appears here, throw exception
                    if (read != 0x00) {
                        throw new IOException("Trouble parsing file contact developer immediately!");
                    }
                }

                // If the file ends with anything other than 0x00, throw exception
                if (read != 0x00) {
                    throw new IOException("Trouble parsing file contact developer immediately!");
                }
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
            // First, write the version
            objOut.writeInt(VERSION);

            // Then, write the players...
            for (PlayerEntry entry : sortedPlayers) {
                // 0xFF will denote we are starting a PlayerEntry Object until 0x00 is reached
                objOut.write(0xFF);
                // Write the entry ID
                objOut.writeObject(entry.getId());

                // Keep writing 0x11 followed by times until 0x00 is reached
                for (PlayerEntry.TimeEntry time : entry.getTimes()) {
                    objOut.write(0x11);
                    objOut.writeObject(time.getStart());
                    objOut.writeObject(time.getEnd());
                }
                // Now that 0x00 is reached, we go to the next object
                objOut.write(0x00);
            }

            // Write another 0x00 to end the file
            objOut.write(0x00);
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
