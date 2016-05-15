package com.deanveloper.playtime;

import com.deanveloper.playtime.commands.PlaytimeCommand;
import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.hooks.GroupManagerHook;
import com.deanveloper.playtime.util.ConfigManager;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Dean B
 */
public class PlayTime extends JavaPlugin implements Listener {
	private static ConfigManager playerDb;
	private EssentialsHook eHook;
	private GroupManagerHook gmHook;
	private static PlayTime instance;

	public static PlayTime getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		getCommand("playtime").setExecutor(new PlaytimeCommand());
		getLogger().info("Loading players...");
		playerDb = new ConfigManager(this, "players.yml");
		getLogger().info("Players loaded!");
		getLogger().info("Hooking plugins...");
		eHook = new EssentialsHook();
		gmHook = new GroupManagerHook();
		getLogger().info("Hooked into available plugins!");
		startTimer();
		getLogger().info("PlayTime enabled!");

		for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			Utils.update(p.getUniqueId(), p.getName());
		}

		instance = this;
	}

	@Override
	public void onDisable() {
		playerDb.save();
	}

	public static ConfigManager getPlayerDb() {
		return playerDb;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Utils.update(e.getPlayer().getUniqueId(), e.getPlayer().getName());
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
