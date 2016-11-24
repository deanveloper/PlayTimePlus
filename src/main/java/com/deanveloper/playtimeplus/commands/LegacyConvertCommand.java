package com.deanveloper.playtimeplus.commands;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Dean B on 11/23/2016.
 */
public class LegacyConvertCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ConfigManager config = new ConfigManager(PlayTimePlus.getInstance(), "players.yml");
            Set<String> keys = config.getConfig().getKeys(true);
            for (String key : keys) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                try {
                    PlayerEntry p = PlayTimePlus.getStorage().get(id);

                    if (p == null) {
                        p = new PlayerEntry(id);
                        Bukkit.getLogger().info(id + " is not in storage, creating player data!");
                        PlayTimePlus.getStorage().getPlayers().put(id, p);
                        PlayTimePlus.getStorage().getPlayersSorted().add(p);
                    }

                    LocalDateTime start = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());

                    // If the player already has playtime at the epoch
                    if(p.getTimes().first().getStart().equals(start)) {
                        Bukkit.getLogger().info(id + " has already been converted from legacy, skipping...");
                        continue;
                    }

                    p.getTimes().add(
                            new PlayerEntry.TimeEntry(start, start.plusSeconds(config.get(key, int.class)), id)
                    );
                    PlayTimePlus.getStorage().update(p);
                } catch (Exception e) {
                    Bukkit.getLogger().log(
                            Level.SEVERE, "Exception trying to add time to UUID " + id.toString(), e
                    );
                }
            }
        } else {
            sender.sendMessage("This command can only be executed from console");
        }
        return true;
    }
}
