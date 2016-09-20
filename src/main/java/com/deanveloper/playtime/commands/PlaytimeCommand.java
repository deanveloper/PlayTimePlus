package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.storage.Storage;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Dean B
 */
public class PlaytimeCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (args.length == 0) {
            return onCommand(sender, cmd, lbl, new String[]{"self"});
        } else if (args.length >= 1) {
            args[0] = args[0].toLowerCase();

            // HELP
            if (args[0].equals("help")) {
                StringJoiner joiner = new StringJoiner("§a|§b", "§a[§b", "§a]");
                joiner.add("help");
                if (sender.hasPermission("playtime.command.playtime.self")) {
                    joiner.add("self");
                }
                if (sender.hasPermission("playtime.command.playtime.top")) {
                    joiner.add("top");
                }
                if (sender.hasPermission("playtime.command.playtime.change")) {
                    joiner.add("change");
                }
                if (sender.hasPermission("playtime.command.playtime.other")) {
                    joiner.add("playername");
                }

                if (joiner.length() == 0) {
                    sender.sendMessage("You do not have permission to use this command!");
                } else {
                    sender.sendMessage("§aUsage: " + joiner.toString());
                }

                // SELF
            } else if (args[0].equals("self")) {
                if (!sender.hasPermission("playtime.command.playtime.self")) {
                    sender.sendMessage("§cYou don't have permission to view your playtime");
                } else {
                    if (sender instanceof Player) {
                        Storage.PlayerEntry playerEntry = PlayTime.getPlayerDb().get(((Player) sender).getUniqueId());

                        sender.sendMessage("§a[Playtime] §dYou §ehave played for §d"
                                + Utils.format(playerEntry.totalTime()) + "§e.");
                    } else {
                        sender.sendMessage("You need to be a player to see your playtime, silly!");
                    }
                }

                // TOP
            } else if (args[0].equals("top")) {
                if (!sender.hasPermission("playtime.command.playtime.top")) {
                    sender.sendMessage("§cYou don't have permission to view the top players");
                }
                try {
                    List<Storage.PlayerEntry> topTen = new ArrayList<>(
                            PlayTime.getPlayerDb().getPlayers().values()
                    );

                    //sort from most to least
                    topTen = topTen.stream()
                            .sorted()
                            .limit(10)
                            .collect(Collectors.toList());

                    sender.sendMessage("§e---------------§a[Playtime Top]§e---------------");

                    for (int i = 0; i < 10; i++) {
                        if (i >= topTen.size()) {
                            break;
                        }

                        sender.sendMessage(
                                String.format(
                                        "§d#%d. §r%s §ewith §d%s§e.",
                                        i + 1,
                                        PlayTime.getEssentialsHook().fullName(topTen.get(i).getName()),
                                        Utils.format(topTen.get(i).totalTime())
                                )
                        );
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    sender.sendMessage("Oops... The developer made a boo-boo: " + e.getMessage());
                    sender.sendMessage("Tell the server owner to send the full error in their console to the developer!");
                }

                // CHANGE
            } else {
                if (!sender.hasPermission("playtime.command.playtime.other")) {
                    sender.sendMessage("§cYou do not have permission to view other people's playtimes!");
                } else {
                    if (args.length >= 1) {
                        UUID id = Utils.getUuid(args[0]);
                        if (id == null) {
                            sender.sendMessage("Couldn't find player " + args[0]);
                        } else {
                            Storage.PlayerEntry time = PlayTime.getPlayerDb().get(id);
                            sender.sendMessage(
                                    String.format("§a[Playtime] §d%s §ehas played for §d%s§e.",
                                            Utils.correctCase(args[0]), Utils.format(time.totalTime()))
                            );
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String lbl, String[] args) {
        return null;
    }
}
