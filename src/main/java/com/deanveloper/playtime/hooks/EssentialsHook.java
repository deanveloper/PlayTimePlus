package com.deanveloper.playtime.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Dean B
 */
public class EssentialsHook {
	private Essentials plugin;

	public EssentialsHook() {
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		if (plug != null) {
			plugin = (Essentials) plug;
		} else {
			Bukkit.getLogger().info("Essentials is not installed, we cannot account for if the player is AFK");
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
