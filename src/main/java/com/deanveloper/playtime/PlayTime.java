package com.deanveloper.playtime;

import com.deanveloper.playtime.commands.ExportPlayersCommand;
import com.deanveloper.playtime.commands.PlaytimeCommand;
import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.util.ConfigManager;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Dean B
 */
public class PlayTime extends JavaPlugin implements Listener {
    private static ConfigManager playerDb;
    private static EssentialsHook eHook;
    private static PlayTime instance;

    public static PlayTime getInstance() {
        return instance;
    }

    public static ConfigManager getPlayerDb() {
        return playerDb;
    }

    @Override
    public void onEnable() {
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("exportplayers").setExecutor(new ExportPlayersCommand());
        getLogger().info("Loading players...");
        playerDb = new ConfigManager(this, "players.yml");
        getLogger().info("Players loaded!");
        getLogger().info("Hooking plugins...");
        eHook = new EssentialsHook();
        getLogger().info("Hooked into available plugins!");
        startTimer();
        getLogger().info("PlayTime enabled!");

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            Utils.update(p.getUniqueId(), p.getName());
        }

        instance = this;
    }

    @Override
    public void onDisable() {
        playerDb.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Utils.update(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    private void startTimer() {
        new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().stream()
                        .filter(eHook::isAfk)
                        .forEach(p -> {
                            String stringyId = p.getUniqueId().toString();
                            int time = playerDb.get(stringyId, 0);
                            time += 1;
                            playerDb.set(stringyId, time);
                        });
            }
        }.runTaskTimer(this, 20L, 20L); // every second

        new BukkitRunnable() {
            public void run() {
                getPlayerDb().save();
            }
        }.runTaskTimer(this, 20L * 60, 20L * 60); // every minute
    }
}
