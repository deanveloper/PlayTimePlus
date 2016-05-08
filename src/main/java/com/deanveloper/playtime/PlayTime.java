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
	private ConfigManager playerDb;
	private ConfigManager settings;
	private EssentialsHook eHook;
	private GroupManagerHook gmHook;

	@Override
	public void onEnable() {
		playerDb = new ConfigManager(this, "players.yml");
		settings = new ConfigManager(this, "config.yml");
		eHook = new EssentialsHook();
		gmHook = new GroupManagerHook();
		startTimer();
		getLogger().info("PlayTime enabled!");
	}

	@Override
	public void onDisable() {
		playerDb.save();
		settings.save();
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
	}
}
