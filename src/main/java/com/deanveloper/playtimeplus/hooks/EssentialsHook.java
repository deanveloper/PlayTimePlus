package com.deanveloper.playtimeplus.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.ess3.api.events.AfkStatusChangeEvent;

import java.util.UUID;

/**
 * @author Dean B
 */
public class EssentialsHook {
	private static Essentials plugin;

	static {
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		if (plug != null) {
			plugin = (Essentials) plug;
			registerAfkHook();
		} else {
			Bukkit.getLogger().info("Essentials is not installed, we cannot account for if the player is AFK");
		}
	}

	private static void registerAfkHook() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onAfk(AfkStatusChangeEvent e) {
				Player p = e.getAffected().getBase();
				if (e.getValue()) { // if turning afk
					PlayTimePlus.getManager().updateLastCount(p.getUniqueId());
					PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to afk");
				} else {
					PlayTimePlus.getManager().startNewEntry(p.getUniqueId());
					PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to non-afk");
				}
			}
		}, PlayTimePlus.getInstance());
	}

	public static boolean isHooked() {
		return plugin != null;
	}

	/**
	 * If the player is afk
	 *
	 * @param p The player to check
	 * @return If they are AFK, assumes not AFK if essentials is not installed
	 */
	public static boolean isAfk(Player p) {
		return plugin.getUser(p).isAfk();
	}

	/**
	 * If the player is afk
	 *
	 * @param p The player to check
	 * @return If they are AFK, assumes not AFK if essentials is not installed
	 */
	public static boolean isAfk(UUID p) {
		return plugin.getUser(p).isAfk();
	}

	/**
	 * Gets a players nickname from essentials
	 *
	 * @param id the id of the player to check
	 * @return the nickname of the player
	 */
	public static String getNickname(UUID id) {
		return plugin.getUser(id).getNick(true);
	}
}
