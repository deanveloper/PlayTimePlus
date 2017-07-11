package com.deanveloper.playtimeplus.storage.binary;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.storage.binary.old.BinaryManagerV1;
import sun.nio.ch.ChannelInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* (non-javadoc)
 * Version 2's File Format:
 * <p>
 * VERSION (int)
 * GZIP the rest:
 * players (int)
 * each player: idLeastSig (long), idMostSig(long), length (int), times (LocalDateTime[2][])
 */
public class BinaryManager implements Manager {
	private static final int VERSION = 2;
	private Path storage;
	private Map<UUID, NavigableSet<TimeEntry>> players;

	public BinaryManager() {
		storage = PlayTimePlus.getInstance().getDataFolder().toPath().resolve("players.playtimeplus");
		players = new HashMap<>();
	}

	@Override
	public void init() {
		// Parse the file

		try (FileChannel file = FileChannel.open(storage, StandardOpenOption.READ)) {
			ByteBuffer versionBuffer = ByteBuffer.allocate(Integer.BYTES);
			file.read(versionBuffer);
			versionBuffer.rewind();
			int streamVersion = versionBuffer.getInt();

			if (VERSION == streamVersion) {
				try (GZIPInputStream gzip = new GZIPInputStream(Channels.newInputStream(file));
				     ReadableByteChannel unzipper = Channels.newChannel(gzip)) {

					ByteBuffer playerLengthBuf = ByteBuffer.allocate(Integer.BYTES);
					unzipper.read(playerLengthBuf);
					playerLengthBuf.rewind();
					int playerLength = playerLengthBuf.getInt();

					for (int i = 0; i < playerLength; i++) {
						ByteBuffer playerHeader = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES);
						unzipper.read(playerHeader);
						playerHeader.rewind();

						UUID id = new UUID(playerHeader.getLong(), playerHeader.getLong());
						int length = playerHeader.getInt(); // length is the number of entries, not number of LDTs

						// Each LocalDateTime is 15 bytes. 2 LocalDateTimes make a TimeEntry
						ByteBuffer times = ByteBuffer.allocate(30 * length);
						unzipper.read(times);
						times.rewind();
						NavigableSet<TimeEntry> entries = new TreeSet<>();
						for (int j = 0; j < length; j++) {
							LocalDateTime start = readLdt(times);
							LocalDateTime end = readLdt(times);
							entries.add(new TimeEntry(start, end));
						}

						players.put(id, entries);
					}
				}
			} else {
				BinaryManagerV1 old = new BinaryManagerV1();
				old.init();
				players = old.getMap();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		// Update the players before saving
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateLastCount(p.getUniqueId());
		}

		try (FileChannel file = FileChannel.open(storage, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

			ByteBuffer versionBuffer = ByteBuffer.allocate(Integer.BYTES);
			versionBuffer.putInt(VERSION);
			file.write(versionBuffer);

			try (GZIPOutputStream gzip = new GZIPOutputStream(Channels.newOutputStream(file));
			     WritableByteChannel zipper = Channels.newChannel(gzip)) {
				int playerLength = players.size();

				ByteBuffer playerLengthBuf = ByteBuffer.allocate(Integer.BYTES);
				playerLengthBuf.putInt(playerLength);
				zipper.write(playerLengthBuf);

				for (Map.Entry<UUID, NavigableSet<TimeEntry>> player : players.entrySet()) {
					UUID id = player.getKey();
					NavigableSet<TimeEntry> times = player.getValue();

					ByteBuffer playerHeader = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES);
					playerHeader.putLong(id.getMostSignificantBits());
					playerHeader.putLong(id.getLeastSignificantBits());
					playerHeader.putInt(times.size());
					zipper.write(playerHeader);

					ByteBuffer timeBuf = ByteBuffer.allocate(30 * times.size());
					for (TimeEntry time : times) {
						putLdt(timeBuf, time.getStart());
						putLdt(timeBuf, time.getEnd());
					}
					zipper.write(timeBuf);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Path getStorage() {
		return storage;
	}

	@Override
	public Map<UUID, NavigableSet<TimeEntry>> getMap() {
		return players;
	}

	private LocalDateTime readLdt(ByteBuffer times) {
		return LocalDateTime.of(
				times.getInt(),     // year
				times.getShort(),   // month of year
				times.getShort(),   // day of month
				times.get(),        // hour of day
				times.get(),        // minute of hour
				times.get(),        // second of minute
				times.getInt()      // nano of second
		);
	}

	private void putLdt(ByteBuffer times, LocalDateTime ldt) {
		times.putInt(ldt.getYear());
		times.putShort((short) ldt.getMonthValue());
		times.putShort((short) ldt.getDayOfMonth());
		times.put((byte) ldt.getHour());
		times.put((byte) ldt.getMinute());
		times.put((byte) ldt.getSecond());
		times.putInt(ldt.getNano());
	}

}
