package com.deanveloper.playtimeplus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.deanveloper.playtimeplus.commands.ConvertStorageCommand;
import com.deanveloper.playtimeplus.commands.DebugCommand;
import com.deanveloper.playtimeplus.commands.ExportPlayersCommand;
import com.deanveloper.playtimeplus.commands.playtime.PlayTimeCommand;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.storage.json.gson.TimeEntryDeserializer;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.storage.json.gson.DurationConverter;
import com.deanveloper.playtimeplus.storage.json.gson.LocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Dean B
 */
public class PlayTimePlus extends JavaPlugin implements Listener {
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(TimeEntry.class, new TimeEntryDeserializer())
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
			.registerTypeAdapter(Duration.class, new DurationConverter())
			.setLongSerializationPolicy(LongSerializationPolicy.STRING)
			.create();
	private static final int CONFIG_VERSION = 1;
	public static boolean debugEnabled = false;
	private static Manager manager;
	private static PlayTimePlus instance;

	@Override
	public void onEnable() {
		instance = this;

		saveDefaultConfig();
		if (getConfig().getInt("version") != CONFIG_VERSION) {
			updateConfig();
		}

		getLogger().info("Setting commands and listeners...");
		getCommand("playtime").setExecutor(new PlayTimeCommand());
		getCommand("exportplayers").setExecutor(new ExportPlayersCommand());
		getCommand("debug").setExecutor(new DebugCommand());
		getCommand("convertstorage").setExecutor(new ConvertStorageCommand());
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		getLogger().info("Done!");

		getLogger().info("Loading players...");
		manager = StorageMethod.valueOf(getConfig().getString("storage").toUpperCase()).getStorage();
		manager.init();
		getLogger().info("Done!");
		getLogger().info("Starting autosave...");
		startAutoSave();
		getLogger().info("Done!");

		getLogger().info("PlayTimePlus enabled!");
	}

	private void updateConfig() {
		try (Reader defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8")) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

			getConfig().set("messages", defConfig.getConfigurationSection("messages"));
			getConfig().set("version", 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		saveConfig();
	}

	private void startAutoSave() {
		int autosave = getConfig().getInt("autosave", -1) * 20;
		if (autosave < 1) {
			throw new IllegalStateException("Autosave must be above 0! (current: " + autosave + " seconds)");
		}
		new BukkitRunnable() {
			public void run() {
				debug("Autosaving!");
				getManager().save();
			}
		}.runTaskTimer(this, autosave, autosave);
	}

	public static void debug(String msg) {
		if (debugEnabled) {
			Bukkit.getLogger().info("[DEBUG] " + msg);
		}
	}

	public static Manager getManager() {
		return manager;
	}

	public static void setManager(StorageMethod manager) {
		PlayTimePlus.manager = manager.getStorage();
		instance.getConfig().set("storage", manager.name());
		instance.saveConfig();
	}

	@Override
	public void onDisable() {
		manager.save();
		instance.saveConfig();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Utils.update(e.getPlayer().getUniqueId(), e.getPlayer().getName());
	}

	public static PlayTimePlus getInstance() {
		return instance;
	}
}
