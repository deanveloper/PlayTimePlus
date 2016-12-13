package com.deanveloper.playtimeplus.commands;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Manager;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Dean
 */
public class ConvertStorageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toUpperCase();
            try {
                StorageMethod storageMethod = StorageMethod.valueOf(args[0]);
                Manager manager = storageMethod.getStorage();
                Manager oldManager = PlayTimePlus.getManager();

                manager.getMap().putAll(oldManager.getMap());

                manager.save();

                PlayTimePlus.setManager(storageMethod);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§6Storage Methods");
                for (StorageMethod type : StorageMethod.values()) {
                    sender.sendMessage("§d" + type.name() + " §b- " + "§a" + type.getDesc());
                }
            }
        }
        return true;
    }
}
