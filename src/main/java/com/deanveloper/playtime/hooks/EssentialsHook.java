package com.deanveloper.playtime.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Dean B
 */
public class EssentialsHook {
	private Essentials plugin;

	public EssentialsHook() {
		try {
			this.plugin = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		} catch (Exception e) {
			Bukkit.getLogger().info("GroupManager is not being used, prefixes will be done through scoreboard");
		}
	}

	/**
	 * If the player is afk
	 *
	 * @param p The player to check
	 * @return  If they are AFK, assumes not AFK if essentials is not installed
	 */
	public boolean isAfk(Player p) {
		return plugin != null && plugin.getUser(p).isAfk();
	}
}
