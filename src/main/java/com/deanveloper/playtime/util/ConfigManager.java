package com.deanveloper.playtime.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * @author Dean B
 */
public class ConfigManager {
	private Plugin plugin;
	private String fileName;
	private File configFile;
	private FileConfiguration config;

	public ConfigManager(Plugin plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;

		this.configFile = new File(plugin.getDataFolder(), fileName);

		saveDefault();
		reload();
	}

	public Object get(String path) {
		return config.get(path);
	}

	public <T> T get(String path, T def) {
		return (T) config.get(path, def);
	}

	public <T> T get(String path, Class<T> type) {
		return (T) config.get(path);
	}

	public ConfigManager set(String path, Object value) {
		config.set(path, value);
		return this;
	}

	public void reload() {
		config = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		InputStream defaults = plugin.getResource(fileName);
		if (defaults != null) {
			config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaults)));
		}
	}

	public void save() {
		try {
			config.save(configFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}

	private void saveDefault() {
		if (!configFile.exists()) {
			plugin.saveResource(fileName, false);
		}
	}

	@Override
	public String toString() {
		return config.toString();
	}
}
