package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
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
                        int time = PlayTime.getPlayerDb().get(((Player) sender).getUniqueId().toString(), 0);

                        sender.sendMessage("§a[Playtime] §dYou §ehave played for §d"
                                + Utils.format(time) + "§e.");
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
                    List<String> topTenIds = new ArrayList<>(PlayTime.getPlayerDb().getConfig().getKeys(false));
                    //sort from most to least
                    topTenIds = topTenIds.stream()
                            .sorted((key1, key2) ->
                                    PlayTime.getPlayerDb().get(key2, int.class)
                                            .compareTo(PlayTime.getPlayerDb().get(key1, int.class)))
                            .collect(Collectors.toList());

                    //remove from the end until it has 10
                    while (topTenIds.size() > 10) {
                        topTenIds.remove(topTenIds.size() - 1);
                    }

                    sender.sendMessage("§e---------------§a[Playtime Top]§e---------------");

                    List<String> topTen = new ArrayList<>(topTenIds.size());
                    topTen.addAll(topTenIds.stream()
                            .map(id -> Utils.getName(UUID.fromString(id)))
                            .collect(Collectors.toList()));

                    for (int i = 0; i < 10; i++) {
                        if (i >= topTenIds.size()) {
                            break;
                        }

                        sender.sendMessage(String.format(
                                "§d#%d. §r%s §ewith §d%s§e.",
                                i + 1,
                                PlayTime.getEssentialsHook().fullName(topTen.get(i)),
                                Utils.format(
                                        PlayTime.getPlayerDb().get(Utils.getUuid(topTen.get(i)).toString(), int.class))
                                )
                        );
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    sender.sendMessage("Oops... The developer made a boo-boo: " + e.getMessage());
                    sender.sendMessage("Tell the server owner to send the full error in their console to the developer!");
                }

                // CHANGE
            } else if (args[0].equals("change")) {
                if (!sender.hasPermission("playtime.command.playtime.change")) {
                    sender.sendMessage("§cYou do not have permission to change people's playtimes");
                }
                if (args.length >= 3) {
                    UUID id = Utils.getUuid(args[1]);
                    if (id == null) {
                        sender.sendMessage("§cCouldn't find player " + args[1]);
                    } else {
                        if (args[2].startsWith("+") || args[2].startsWith("-")) {
                            try {
                                int change = Integer.parseInt(args[2]);

                                int playTime = PlayTime.getPlayerDb().get(id.toString(), 0);
                                playTime += change;
                                PlayTime.getPlayerDb().set(id.toString(), playTime);

                                sender.sendMessage(args[1] + "'s playtime was changed by " + change + " seconds");
                                sender.sendMessage("§7Their playtime is now at " + Utils.format(playTime));
                            } catch (NumberFormatException e) {
                                return onCommand(sender, cmd, lbl, new String[] {"change"});
                            }
                        } else {
                            return onCommand(sender, cmd, lbl, new String[] {"change"});
                        }
                    }
                } else {
                    sender.sendMessage("Usage: /playtime change <playername> <(+|-)seconds>");
                }

                // OTHER
            } else {
                if(!sender.hasPermission("playtime.command.playtime.other")) {
                    sender.sendMessage("§cYou do not have permission to view other people's playtimes!");
                } else {
                    if(args.length >= 1) {
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
