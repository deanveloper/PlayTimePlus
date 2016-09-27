package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.query.QueryException;
import com.deanveloper.playtimeplus.util.query.QueryUtil;
import org.bukkit.ChatColor;

import java.util.Set;

/**
 * @author Dean
 */
public class QuerySubCmd implements SubCommandExecutor {
    private String[] aliases = new String[] {"lookup", "q"};

    @Override
    public void execute(SubCommandCall call) {
        if (call.getArgs()[0].equals("help")) {
            call.sendBack("Go to this page for help: https://goo.gl/Y1KeoG");
        } else {
            try {
                call.sendBack("Performing query...");
                Set<PlayerEntry> entries = QueryUtil.query(call.getArgs());
                call.sendBack("Query finished!");
                entries.stream()
                        .sorted()
                        .forEach(pEntry ->
                                call.sendBack("§d%s §e-> §d%s", pEntry.getName(), Utils.format(pEntry.getTotalTime()))
                        );
            } catch (QueryException e) {
                call.sendBack(ChatColor.RED + "ERROR: " + e.getMessage());
            }
        }
    }




    @Override
    public String getName() {
        return "query";
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
        return "Query all play times";
    }

    @Override
    public String getUsage() {
        return "[help|query]";
    }

    @Override
    public String getPermission() {
        return "playtimeplus.command.playtime.query";
    }
}
