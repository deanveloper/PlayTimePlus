package com.deanveloper.playtime;

import com.deanveloper.playtime.commands.DebugCommand;
import com.deanveloper.playtime.commands.ExportPlayersCommand;
import com.deanveloper.playtime.commands.PlaytimeCommand;
import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.storage.Storage;
import com.deanveloper.playtime.storage.StorageMethod;
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
    private static Storage playerDb;
    private static EssentialsHook eHook;
    private static PlayTime instance;
    public static boolean debugEnabled = false;

    public static PlayTime getInstance() {
        return instance;
    }

    public static Storage getPlayerDb() {
        return playerDb;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Setting commands...");
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("exportplayers").setExecutor(new ExportPlayersCommand());
        getCommand("debug").setExecutor(new DebugCommand());
        getLogger().info("Done!");

        getLogger().info("Loading players...");
        playerDb = StorageMethod.valueOf(getConfig().getString("storage").toUpperCase()).getStorage();
        getLogger().info("Done!");

        getLogger().info("Hooking into essentials...");
        eHook = new EssentialsHook();
        getLogger().info("Done!");
        startAutoSave();
        Bukkit.getPluginManager().registerEvents(this, this);

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            Utils.update(p.getUniqueId(), p.getName());
        }
        getLogger().info("PlayTime enabled!");

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

    private void startAutoSave() {
        new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !eHook.isAfk(player))
                        .forEach(p -> {
                            debug("added 1 second to " + p.getName());
                            String stringyId = p.toString();
                            int time = playerDb.get(p.getUniqueId(), 0);
                            time += 1;
                            playerDb.set(stringyId, time);
                        });
            }
        }.runTaskTimer(this, 20L, 20L); // every second

        new BukkitRunnable() {
            public void run() {
                getPlayerDb().save();
                debug(getPlayerDb().getPlayers().toString());
            }
        }.runTaskTimer(this, 20L * 60, 20L * 60); // every minute
    }

    public static EssentialsHook getEssentialsHook() {
        return eHook;
    }

    public static void debug(String msg) {
        if(debugEnabled) {
            Bukkit.getLogger().info("[DEBUG] " + msg);
        }
    }
}
