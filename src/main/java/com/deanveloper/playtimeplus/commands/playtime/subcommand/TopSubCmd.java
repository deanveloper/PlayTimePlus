package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class TopSubCmd implements SubCommandExecutor {
    @Override
    public void execute(SubCommandCall call) {
        try {
            call.getSender().sendMessage("§e---------------§a[Playtime Top]§e---------------");

            Bukkit.getOnlinePlayers().forEach(p -> PlayTimePlus.getManager().updateLastCount(p.getUniqueId()));

            List<UUID> top10 = PlayTimePlus.getManager().getMap().keySet().parallelStream()
                    .sorted((id1, id2) -> PlayTimePlus.getManager().onlineTime(id2).compareTo(PlayTimePlus.getManager().onlineTime(id1)))
                    .sequential()
                    .limit(10)
                    .collect(Collectors.toList());

            int limit = 10;
            for (UUID id : top10) {
                if (limit <= 0) {
                    break;
                }

                call.getSender().sendMessage(
                        String.format(
                                "§d#%d. §r%s §ewith §d%s§e.",
                                11 - limit,
                                PlayTimePlus.getEssentialsHook().fullName(id),
                                Utils.format(PlayTimePlus.getManager().onlineTime(id))
                        )
                );

                limit++;
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
