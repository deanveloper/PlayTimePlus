package com.deanveloper.playtimeplus.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;

import java.util.UUID;

/**
 * Created by Dean on 7/18/2017.
 */
public class ChatHook {
	private static Chat chat;

	static {
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
			if (rsp != null) {
				chat = rsp.getProvider();
			}
		}
	}


	public static boolean isHooked() {
		return chat != null;
	}

	/**
	 * Gets the prefix of the player.
	 *
	 * @param world The world name to check from
	 * @param player The player to check
	 * @return the prefix of the player
	 */
	public static String getPrefix(String world, UUID player) {
		return chat.getPlayerPrefix(world, Bukkit.getOfflinePlayer(player));
	}
}
