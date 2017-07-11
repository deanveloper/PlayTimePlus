package com.deanveloper.playtimeplus;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import com.deanveloper.playtimeplus.hooks.EssentialsHook;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.gson.DurationConverter;
import com.deanveloper.playtimeplus.util.gson.LocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Dean B
 */
public class PlayTimePlus extends JavaPlugin implements Listener {
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
			.registerTypeAdapter(Duration.class, new DurationConverter())
			.setLongSerializationPolicy(LongSerializationPolicy.STRING)
			.create();
	public static boolean debugEnabled = false;
	private static Manager manager;
	private static EssentialsHook eHook;
	private static PlayTimePlus instance;

	@Override
	public void onEnable() {
		instance = this;

		updateConfig();

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

		getLogger().info("Hooking into essentials...");
		eHook = new EssentialsHook();
		getLogger().info("Done!");

		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			Utils.update(p.getUniqueId(), p.getName());
		}
		getLogger().info("PlayTimePlus enabled!");
	}

	private void updateConfig() {
		saveDefaultConfig();
		if (getConfig().getInt("version") == 0) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
			getConfig().set("messages", defaultConfig.getConfigurationSection("messages"));
			getConfig().set("version", 1);
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

	public static EssentialsHook getEssentialsHook() {
		return eHook;
	}
}
