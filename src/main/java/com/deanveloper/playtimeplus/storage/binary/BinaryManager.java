package com.deanveloper.playtimeplus.storage.binary;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.TimeEntry;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Version 1's File Format:
 * <p>
 * VERSION (int)
 * each player: 0xFF (byte), uniqueId (UUID), times (below), 0x00 (byte)
 * each time:   0x11 (byte), start (LocalDateTime), end (LocalDateTime), 0x00 (byte)
 */
public class BinaryManager implements Manager {
	private static final int VERSION = 1;
	private File storage;
	private Map<UUID, NavigableSet<TimeEntry>> players;

	public BinaryManager() {
		storage = new File(PlayTimePlus.getInstance().getDataFolder(), "players.json");
		players = new HashMap<>();
	}

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
				//noinspection unchecked
				players = (Map<UUID, NavigableSet<TimeEntry>>) BinaryConverter.convertBinary(streamVersion,
						objIn);
			} else {
				players = new HashMap<>();

				int read;
				while ((read = objIn.read()) == 0xFF) {
					UUID id = (UUID) objIn.readObject();
					NavigableSet<TimeEntry> times = new TreeSet<>();

					while ((read = objIn.read()) == 0x11) {
						TimeEntry time = new TimeEntry(
								(LocalDateTime) objIn.readObject(),
								(LocalDateTime) objIn.readObject()
						);
						times.add(time);
					}
					players.put(id, times);

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
			players = new HashMap<>();
			save();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save() {
		// Update the players before saving
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateLastCount(p.getUniqueId());
		}

		try (
				FileOutputStream output = new FileOutputStream(storage);
				ObjectOutputStream objOut = new ObjectOutputStream(output)
		) {
			// First, write the version
			objOut.writeInt(VERSION);

			// Then, write the players...
			for (Map.Entry<UUID, NavigableSet<TimeEntry>> entry : players.entrySet()) {
				// 0xFF will denote we are starting an Entry until 0x00 is reached
				objOut.write(0xFF);
				// Write the entry ID
				objOut.writeObject(entry.getKey());

				// Keep writing 0x11 followed by times until 0x00 is reached
				for (TimeEntry time : entry.getValue()) {
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
	public Map<UUID, NavigableSet<TimeEntry>> getMap() {
		return players;
	}
}
