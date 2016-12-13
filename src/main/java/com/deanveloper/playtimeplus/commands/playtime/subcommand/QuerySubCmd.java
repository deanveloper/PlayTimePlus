package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.query.QueryException;
import com.deanveloper.playtimeplus.util.query.QueryUtil;
import org.bukkit.ChatColor;

import java.time.Duration;
import java.util.*;

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
                Map<UUID, Set<TimeEntry>> entries = QueryUtil.query(call.getArgs());
                Map<UUID, Duration> durations = new HashMap<>(entries.size());

                for(Map.Entry<UUID, Set<TimeEntry>> e : entries.entrySet()) {
                    Duration total = Duration.ZERO;
                    for(TimeEntry time : e.getValue()) {
                        total = total.plus(time.getDuration());
                    }
                    durations.put(e.getKey(), total);
                }

                call.sendBack("Query finished!");
                PlayTimePlus.debug("QUERY: " + entries);
                durations.entrySet().stream()
                        .sorted(Comparator.comparing(Map.Entry::getValue))
                        .forEach(entry ->
                                call.sendBack("§d%s §e-> §d%s",
                                        Utils.getNameForce(entry.getKey()),
                                        Utils.format(entry.getValue()))
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
