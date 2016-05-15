package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Dean B
 */
public class PlaytimeCommand implements CommandExecutor, TabExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("playtime.command") && args.length > 0 && !args[0].equalsIgnoreCase("top")) {
			UUID id = Utils.getUuid(args[0]);
			if (id == null) {
				sender.sendMessage("Couldn't find player " + args[0]);
			} else {
				int time = PlayTime.getPlayerDb().get(id.toString(), 0);
				sender.sendMessage(
						String.format("§a[Playtime] §d%s §ehas played for §d%s§e.",
								Utils.correctCase(args[0]), Utils.format(time))
				);
			}
		} else if (sender instanceof Player) {
			if (args.length > 0 && args[0].equalsIgnoreCase("top")) {
				List<String> topTenIds = PlayTime.getPlayerDb().getConfig().getKeys(false).stream()
						.sorted((key1, key2) -> PlayTime.getPlayerDb().get(key1, int.class)
								.compareTo(PlayTime.getPlayerDb().get(key2, int.class)))
						.collect(Utils.lastN(10));
				sender.sendMessage("§e---------------§a[Playtime Top]§e---------------");

				List<String> topTen = topTenIds.parallelStream()
						.map(id -> Utils.getName(UUID.fromString(id)))
						.collect(Collectors.toList());

				for (int i = 0; i < 10; i++) {
					if (i >= topTenIds.size()) {
						break;
					}

					sender.sendMessage(String.format(
							"§d#%d. §r%s §ewith §d%s§e.",
							i + 1,
							topTen.get(i),
							Utils.format(PlayTime.getPlayerDb().get(Utils.getUuid(topTen.get(i)).toString(), int.class
							)))
					);
				}
			} else {
				int time = PlayTime.getPlayerDb().get(((Player) sender).getUniqueId().toString(), 0);

				sender.sendMessage("§a[Playtime] §dYou §ehave played for §d"
						+ Utils.format(time) + "§e.");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lbl, String[] args) {
		return null;
	}
}
