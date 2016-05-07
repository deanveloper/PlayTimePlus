package com.deanveloper.playtime;

import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.hooks.GroupManagerHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Dean B
 */
public class PlayTime extends JavaPlugin {
	private EssentialsHook eHook;
	private GroupManagerHook gmHook;

	@Override
	public void onEnable() {
		startTimer();
		getLogger().info("PlayTime enabled!");
	}

	private void startTimer() {

		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(eHook.isAfk(p)) {
						MetadataValue val = p.getMetadata("playtime_online").get(0);
						double onlineTime = val == null ? 0 : val.asInt();
						onlineTime += 20L;
						p.setMetadata("playtime_online", new FixedMetadataValue(PlayTime.this, onlineTime));
					}
				}
			}
		}.runTaskTimer(this, 20L, 20L);
	}
}
