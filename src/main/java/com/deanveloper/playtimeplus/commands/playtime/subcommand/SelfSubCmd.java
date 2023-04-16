package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import org.bukkit.command.ConsoleCommandSender;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.util.ConfigVar;
import com.deanveloper.playtimeplus.util.Utils;

/**
 * @author Dean
 */
public class SelfSubCmd implements SubCommandExecutor {
	private String[] aliases = new String[]{"me", "s"};

	@Override
	public void execute(SubCommandCall call) {
		if (call.getSender() instanceof ConsoleCommandSender) {
			call.sendBack(
					Utils.configMessage(
							"messages.cmd.playtime.self.consoleuse",
							new ConfigVar("sender", call.getSender().getName())
					)
			);
			return;
		}

		call.sendBack(
				Utils.configMessage(
						"messages.cmd.playtime.self.success",
						new ConfigVar("sender", call.getSender().getName()),
						new ConfigVar("target", call.getSender().getName()),
						new ConfigVar(
								"time",
								Utils.format(PlayTimePlus.getManager().onlineTime(call.getPlayer().getUniqueId()))
						)
				)
		);
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
