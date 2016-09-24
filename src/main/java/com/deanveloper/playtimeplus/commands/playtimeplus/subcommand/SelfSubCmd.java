package com.deanveloper.playtimeplus.commands.playtimeplus.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

/**
 * @author Dean
 */
public class SelfSubCmd implements SubCommandExecutor {
    @Override
    public void execute(SubCommandCall call) {
        PlayerEntry playerEntry = PlayTimePlus.getPlayerDb().get(call.getPlayer().getUniqueId());

        call.sendBack("§dYou §ehave played for §d%s§e.", Utils.format(playerEntry.getTotalTime()));
    }

    @Override
    public String getName() {
        return "self";
    }

    @Override
    public String[] getAliases() {
        return NO_ALIASES;
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
        return "playtimeplus.command.playtimeplus.self";
    }
}
