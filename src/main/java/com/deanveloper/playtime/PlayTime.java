package com.deanveloper.playtime;

import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.hooks.GroupManagerHook;
import com.deanveloper.playtime.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Dean B
 */
public class PlayTime extends JavaPlugin {
	private static ConfigManager playerDb;
	private ConfigManager settings;
	private EssentialsHook eHook;
	private GroupManagerHook gmHook;
	private static PlayTime instance;

	public static PlayTime getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		playerDb = new ConfigManager(this, "players.yml");
		settings = new ConfigManager(this, "config.yml");
		eHook = new EssentialsHook();
		gmHook = new GroupManagerHook();
		startTimer();
		getLogger().info("PlayTime enabled!");
		instance = this;
	}

	@Override
	public void onDisable() {
		playerDb.save();
		settings.save();
	}

	public static ConfigManager getPlayerDb() {
		return playerDb;
	}

	private void startTimer() {
		new BukkitRunnable() {
			public void run() {
				Bukkit.getOnlinePlayers().stream()
						.filter(player -> !eHook.isAfk(player))
						.forEach(player -> {
							String stringyId = player.getUniqueId().toString();
							double time = playerDb.get(stringyId, 0);
							time += 1;
							playerDb.set(player.getUniqueId().toString(), time);
						});
			}
		}.runTaskTimer(this, 20L, 20L);

		new BukkitRunnable() {
			public void run() {
				getPlayerDb().save();
			}
		}.runTaskTimer(this, 20L * 60, 20L * 60); //every minute
	}
}
