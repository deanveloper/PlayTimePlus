package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class TopSubCmd implements SubCommandExecutor {
    @Override
    public void execute(SubCommandCall call) {
        try {
            NavigableSet<PlayerEntry> allPlayers = new TreeSet<>(PlayTimePlus.getStorage().getPlayersSorted());

            call.getSender().sendMessage("§e---------------§a[Playtime Top]§e---------------");

            for (int i = 0; i < Math.min(10, allPlayers.size()); i++) {
                PlayerEntry entry = allPlayers.pollLast();
                call.getSender().sendMessage(
                        String.format(
                                "§d#%d. §r%s §ewith §d%s§e.",
                                i + 1,
                                PlayTimePlus.getEssentialsHook().fullName(entry.getName()),
                                Utils.format(entry.getTotalTime())
                        )
                );
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            call.sendBack("Oops... The developer made a boo-boo: " + e.getMessage());
            call.sendBack("Tell the server owner to send the full error in their console to the developer!");
        }
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String[] getAliases() {
        return NO_ALIASES;
    }

    @Override
    public boolean canConsoleExecute() {
        return true;
    }

    @Override
    public String getDesc() {
        return "Gets the players with the top 10 play times";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "playtimeplus.command.playtime.top";
    }
}
