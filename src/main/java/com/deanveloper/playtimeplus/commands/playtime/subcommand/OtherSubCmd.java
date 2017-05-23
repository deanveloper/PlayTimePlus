package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
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
			call.sendBack(
					Utils.configMessage("messages.cmd.playtime.other.fail",
							call.getSender().getName(),
							call.getArgs()[0],
							"",
							"",
							"Could not find player!"
					));
			return;
		}

		call.sendBack(
				Utils.configMessage("messages.cmd.playtime.other.success",
						call.getSender().getName(),
						call.getArgs()[0],
						"",
						Utils.format(PlayTimePlus.getManager().onlineTime(id)),
						""
				)
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
