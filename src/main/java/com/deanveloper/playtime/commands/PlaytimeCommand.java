package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Dean B
 */
public class PlaytimeCommand implements CommandExecutor, TabExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("playtime.command") && args.length > 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);

					if (p == null) {
						sender.sendMessage("Couldn't find player " + args[0]);
					} else {
						int playTime = PlayTime.getPlayerDb().get(p.getUniqueId().toString(), 0);
						Duration time = Duration.of(playTime, ChronoUnit.SECONDS);

						sender.sendMessage("§a[Playtime] §d" + p.getName() + " §ehas played for §d"
								+ DurationFormatter.format(time) + "§e.");
					}
				}
			}.runTaskAsynchronously(PlayTime.getInstance());
			return true;
		} else if (sender instanceof Player){
			int playTime = PlayTime.getPlayerDb().get(((Player) sender).getUniqueId().toString(), 0);
			Duration time = Duration.of(playTime, ChronoUnit.SECONDS);

			sender.sendMessage("§a[Playtime] §dYou §ehave played for §d"
					+ DurationFormatter.format(time) + "§e.");
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return null;
	}
}
