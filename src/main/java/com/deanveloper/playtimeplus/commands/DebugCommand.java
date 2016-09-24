package com.deanveloper.playtimeplus.commands;

import com.deanveloper.playtimeplus.PlayTimePlus;
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
                sender.sendMessage(sender.getName() + " enabled debug mode!");
            } else if (args[0].equalsIgnoreCase("false")) {
                PlayTimePlus.debugEnabled = false;
                sender.sendMessage(sender.getName() + " disabled debug mode!");
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
