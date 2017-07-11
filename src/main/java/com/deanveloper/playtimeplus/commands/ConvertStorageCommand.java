package com.deanveloper.playtimeplus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import com.deanveloper.playtimeplus.util.ConfigVar;
import com.deanveloper.playtimeplus.util.Utils;

/**
 * @author Dean
 */
public class ConvertStorageCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length >= 1) {
			args[0] = args[0].toUpperCase();
			try {
				StorageMethod storageMethod = StorageMethod.valueOf(args[0]);
				Manager manager = storageMethod.getStorage();
				Manager oldManager = PlayTimePlus.getManager();

				manager.getMap().putAll(oldManager.getMap());
				manager.save();

				PlayTimePlus.setManager(storageMethod);

				sender.sendMessage(
						Utils.configMessage(
								"messages.cmd.convertstorage.success",
								new ConfigVar("sender", sender.getName())
						)
				);
			} catch (IllegalArgumentException e) {
				sender.sendMessage(
						Utils.configMessage(
								"messages.cmd.convertstorage.error.header",
								new ConfigVar("sender", sender.getName())
						)
				);
				for (StorageMethod type : StorageMethod.values()) {
					sender.sendMessage(
							Utils.configMessage(
									"messages.cmd.convertstorage.error.eachMethod",
									new ConfigVar("sender", sender.getName()),
									new ConfigVar("type", type.name()),
									new ConfigVar("desc", type.getDesc())
							)
					);
				}
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(
						Utils.configMessage(
								"messages.criticalerr",
								new ConfigVar("sender", sender.getName()),
								new ConfigVar("criticalerr", e.getMessage())
						)
				);
			}
		} else {
			return false;
		}
		return true;
	}
}
