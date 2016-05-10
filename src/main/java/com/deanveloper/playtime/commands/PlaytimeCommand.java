package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
			new BukkitRunnable() {
				@Override
				public void run() {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);

					if (p == null) {
						sender.sendMessage("Couldn't find player " + args[0]);
					} else {
						int time = PlayTime.getPlayerDb().get(p.getUniqueId().toString(), 0);
						sender.sendMessage(
								String.format("§a[Playtime] §d%s §ehas played for §d%s§e.",
										p.getName(), Utils.format(time))
						);
					}
				}
			}.runTaskAsynchronously(PlayTime.getInstance());
		} else if (sender instanceof Player) {
			if (args.length > 0 && args[0].equalsIgnoreCase("top")) {
				List<String> topTenIds = PlayTime.getPlayerDb().getConfig().getKeys(false).stream()
						.sorted((key1, key2) -> PlayTime.getPlayerDb().get(key1, int.class)
								.compareTo(PlayTime.getPlayerDb().get(key2, int.class)))
						.collect(Utils.lastN(10));
				sender.sendMessage("§e---------------§a[Playtime Top]§e---------------");

				List<OfflinePlayer> topTen = topTenIds.parallelStream()
						.map(id -> Bukkit.getOfflinePlayer(UUID.fromString(id)))
						.collect(Collectors.toList());

				for (int i = 0; i < 10; i++) {
					if (i >= topTenIds.size()) {
						break;
					}

					sender.sendMessage(String.format(
							"§d#%d. §r%s §ewith §d%s§e.",
							i + 1,
							topTen.get(i).getName(),
							Utils.format(PlayTime.getPlayerDb().get(topTen.get(i).getUniqueId().toString(), int.class
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
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return null;
	}
}
