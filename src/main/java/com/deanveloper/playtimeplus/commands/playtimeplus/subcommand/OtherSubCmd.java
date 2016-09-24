package com.deanveloper.playtimeplus.commands.playtimeplus.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.util.UUID;

/**
 * @author Dean
 */
public class OtherSubCmd implements SubCommandExecutor {
    private String[] aliases = new String[]{"o"};

    @Override
    public void execute(SubCommandCall call) {
        UUID id = Utils.getUuid(call.getArgs()[0]);
        if (id == null) {
            call.sendBack("Couldn't find player " + call.getArgs()[0]);
            return;
        }
        PlayerEntry time = PlayTimePlus.getPlayerDb().get(id);
        call.sendBack(
                String.format("§d%s §ehas played for §d%s§e.",
                        Utils.correctCase(call.getArgs()[0]), Utils.format(time.getTotalTime()))
        );
    }

    @Override
    public String getName() {
        return "other";
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public boolean canConsoleExecute() {
        return true;
    }

    @Override
    public String getDesc() {
        return "Get another player's play time.";
    }

    @Override
    public String getUsage() {
        return "<player>";
    }

    @Override
    public String getPermission() {
        return "playtimeplus.command.playtime.other";
    }
}
