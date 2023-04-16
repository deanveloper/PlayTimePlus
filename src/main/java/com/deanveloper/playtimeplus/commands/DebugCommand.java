package com.deanveloper.playtimeplus.commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;

import java.util.UUID;

/**
 * @author Dean
 */
public class DebugCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("true")) {
				PlayTimePlus.debugEnabled = true;
				Bukkit.broadcast(sender.getName() + " enabled debug mode!", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
				Bukkit.getLogger().info("CURRENT STATE:" + PlayTimePlus.getManager().getMap().toString());
			} else if (args[0].equalsIgnoreCase("false")) {
				PlayTimePlus.debugEnabled = false;
				Bukkit.broadcast(sender.getName() + " disabled debug mode!", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
			} else {

				if (args[0].equalsIgnoreCase("setplayer")) {
					UUID id = UUID.fromString(args[1]);

					Manager manager = PlayTimePlus.getManager();
					if (args[2].equalsIgnoreCase("true")) {
						manager.startNewEntry(id);
					} else {
						manager.updateLastCount(id);
					}
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
