package com.deanveloper.playtime.hooks;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Dean B
 */
public class GroupManagerHook {
	private GroupManager plugin;

	public GroupManagerHook() {
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
		if (plug != null) {
			plugin = (GroupManager) plug;
		} else {
			Bukkit.getLogger().info("GroupManager is not installed, prefixes will be done through scoreboard");
		}
	}

	public String getPrefix(final Player base) {
		final AnjoPermissionsHandler handler = plugin.getWorldsHolder().getWorldPermissions(base);
		if (handler == null) {
			return null;
		}
		return handler.getUserPrefix(base.getName());
	}
}
