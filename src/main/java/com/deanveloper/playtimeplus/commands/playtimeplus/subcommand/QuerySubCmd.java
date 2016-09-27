package com.deanveloper.playtimeplus.commands.playtimeplus.subcommand;

import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtimeplus.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.query.QueryException;
import com.deanveloper.playtimeplus.util.query.QueryUtil;
import org.bukkit.ChatColor;

import java.util.List;
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
                Set<PlayerEntry> entries = QueryUtil.query(call.getArgs());
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
        return null;
    }
}
