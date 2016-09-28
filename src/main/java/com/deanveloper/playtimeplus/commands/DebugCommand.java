package com.deanveloper.playtimeplus.commands;

import com.deanveloper.playtimeplus.PlayTimePlus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            } else if (args[0].equalsIgnoreCase("false")) {
                PlayTimePlus.debugEnabled = false;
                Bukkit.broadcast(sender.getName() + " disabled debug mode!", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
