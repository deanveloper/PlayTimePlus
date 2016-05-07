package com.deanveloper.playtime.hooks;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Dean B
 */
public class GroupManagerHook {
	private GroupManager plugin;

	public GroupManagerHook() {
		try {
			this.plugin = (GroupManager) Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
		} catch (Exception e) {
			Bukkit.getLogger().info("GroupManager is not being used, prefixes will be done through scoreboard");
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
