package com.deanveloper.playtimeplus.commands.playtime.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandCall;
import com.deanveloper.playtimeplus.commands.playtime.SubCommandExecutor;
import com.deanveloper.playtimeplus.util.Utils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class TopSubCmd implements SubCommandExecutor {
	@Override
	public void execute(SubCommandCall call) {
		try {
			call.getSender().sendMessage("§e---------------§a[Playtime Top]§e---------------");

			for (Player p : Bukkit.getOnlinePlayers()) {
				PlayTimePlus.getManager().updateLastCount(p.getUniqueId());
			}

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
						Utils.configMessage(
								"messages.cmd.playtime.top.eachPlayer",
								call.getSender().getName(),
								PlayTimePlus.getEssentialsHook().fullName(id),
								11 - limit + "",
								Utils.format(PlayTimePlus.getManager().onlineTime(id)),
								""
						)
				);

				limit--;
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
