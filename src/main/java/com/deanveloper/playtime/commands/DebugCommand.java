package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
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
                PlayTime.debugEnabled = true;
                sender.sendMessage(sender.getName() + " enabled debug mode!");
            } else if (args[0].equalsIgnoreCase("false")) {
                PlayTime.debugEnabled = false;
                sender.sendMessage(sender.getName() + " disabled debug mode!");
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
