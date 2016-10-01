package com.deanveloper.playtimeplus.commands;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.Storage;
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
                Storage storage = storageMethod.getStorage();
                Storage oldStorage = PlayTimePlus.getStorage();

                storage.getPlayersSorted().addAll(oldStorage.getPlayersSorted());
                storage.getPlayers().putAll(oldStorage.getPlayers());

                storage.save();

                PlayTimePlus.setStorage(storageMethod);
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
