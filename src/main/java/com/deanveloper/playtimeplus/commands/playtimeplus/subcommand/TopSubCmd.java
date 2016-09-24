package com.deanveloper.playtimeplus.commands.playtimeplus.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class TopSubCmd implements SubCommandExecutor {
    @Override
    public void execute(SubCommandCall call) {
        try {
            List<PlayerEntry> topTen = new ArrayList<>(
                    PlayTimePlus.getPlayerDb().getPlayers().values()
            );

            //sort from most to least
            topTen = topTen.stream()
                    .sorted()
                    .limit(10)
                    .collect(Collectors.toList());

            call.getSender().sendMessage("§e---------------§a[Playtime Top]§e---------------");

            for (int i = 0; i < 10; i++) {
                if (i >= topTen.size()) {
                    break;
                }

                call.getSender().sendMessage(
                        String.format(
                                "§d#%d. §r%s §ewith §d%s§e.",
                                i + 1,
                                PlayTimePlus.getEssentialsHook().fullName(topTen.get(i).getName()),
                                Utils.format(topTen.get(i).getTotalTime())
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
