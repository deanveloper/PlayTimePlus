package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.util.Utils;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Dean
 */
public class SelfSubCmd implements SubCommandExecutor {
    private String[] aliases = new String[]{ "me", "s" };

    @Override
    public void execute(SubCommandCall call) {
        if (call.getSender() instanceof ConsoleCommandSender) {
            call.sendBack("Only players can execute this command!");
            return;
        }

        call.sendBack("§dYou §ehave played for §d%s§e.",
                Utils.format(PlayTimePlus.getManager().onlineTime(call.getPlayer().getUniqueId())));
    }

    @Override
    public String getName() {
        return "self";
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public boolean canConsoleExecute() {
        return false;
    }

    @Override
    public String getDesc() {
        return "See how long you have played";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "playtimeplus.command.playtime.self";
    }
}
