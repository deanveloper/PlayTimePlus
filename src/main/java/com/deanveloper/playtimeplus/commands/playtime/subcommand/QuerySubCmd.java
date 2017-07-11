package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.ConfigVar;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.query.QueryException;
import com.deanveloper.playtimeplus.util.query.QueryUtil;

import java.time.Duration;
import java.util.*;

/**
 * @author Dean
 */
public class QuerySubCmd implements SubCommandExecutor {
	private String[] aliases = new String[]{"lookup", "q"};

	@Override
	public void execute(SubCommandCall call) {
		if (call.getArgs()[0].equals("help")) {
			call.sendBack(
					Utils.configMessage("messages.cmd.playtime.query.help",
							new ConfigVar("sender", call.getSender().getName())
					)
			);
		} else {
			try {
				call.sendBack(
						Utils.configMessage("messages.cmd.playtime.query.start",
								new ConfigVar("sender", call.getSender().getName())
						)
				);
				Map<UUID, NavigableSet<TimeEntry>> entries = QueryUtil.query(call.getArgs());
				Map<UUID, Duration> durations = new HashMap<>(entries.size());

				for (Map.Entry<UUID, NavigableSet<TimeEntry>> e : entries.entrySet()) {
					Duration total = Duration.ZERO;
					for (TimeEntry time : e.getValue()) {
						total = total.plus(time.getDuration());
					}
					durations.put(e.getKey(), total);
				}

				call.sendBack(
						Utils.configMessage("messages.cmd.playtime.query.success",
								new ConfigVar("sender", call.getSender().getName())
						)
				);
				PlayTimePlus.debug("QUERY: " + entries);
				durations.entrySet().stream()
						.sorted(Comparator.comparing(Map.Entry::getValue))
						.forEach(entry ->
								call.sendBack(
										Utils.configMessage("messages.cmd.playtime.query.eachPlayer",
												new ConfigVar("sender", call.getSender().getName()),
												new ConfigVar("player", Utils.getNameForce(entry.getKey())),
												new ConfigVar("time", Utils.format(entry.getValue()))
										)
								)
						);
			} catch (QueryException e) {
				call.sendBack(
						Utils.configMessage(
								"messages.cmd.playtime.query.queryError",
								new ConfigVar("sender", call.getSender().getName()),
								new ConfigVar("error", e.getMessage())
						)
				);
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
