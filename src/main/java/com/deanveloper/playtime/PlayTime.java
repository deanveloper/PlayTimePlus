package com.deanveloper.playtime;

import com.deanveloper.playtime.commands.DebugCommand;
import com.deanveloper.playtime.commands.ExportPlayersCommand;
import com.deanveloper.playtime.commands.PlaytimeCommand;
import com.deanveloper.playtime.hooks.EssentialsHook;
import com.deanveloper.playtime.storage.PlayerEntry;
import com.deanveloper.playtime.storage.Storage;
import com.deanveloper.playtime.storage.StorageMethod;
import com.deanveloper.playtime.util.Utils;
import com.deanveloper.playtime.util.gson.DurationConverter;
import com.deanveloper.playtime.util.gson.LocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Dean B
 */
public class PlayTime extends JavaPlugin implements Listener {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
            .registerTypeAdapter(Duration.class, new DurationConverter())
            .create();
    public static boolean debugEnabled = false;
    private static Storage playerDb;
    private static EssentialsHook eHook;
    private static PlayTime instance;

    public static PlayTime getInstance() {
        return instance;
    }

    public static Storage getPlayerDb() {
        return playerDb;
    }

    public static EssentialsHook getEssentialsHook() {
        return eHook;
    }

    public static void debug(String msg) {
        if (debugEnabled) {
            Bukkit.getLogger().info("[DEBUG] " + msg);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getLogger().info("Setting commands and listeners...");
        getCommand("playtime").setExecutor(new PlaytimeCommand());
        getCommand("exportplayers").setExecutor(new ExportPlayersCommand());
        getCommand("debug").setExecutor(new DebugCommand());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        getLogger().info("Done!");

        getLogger().info("Loading players...");
        playerDb = StorageMethod.valueOf(getConfig().getString("storage").toUpperCase()).getStorage();
        getLogger().info("Done!");
        getLogger().info("Starting autosave...");
        startAutoSave();
        getLogger().info("Done!");

        getLogger().info("Hooking into essentials...");
        eHook = new EssentialsHook();
        getLogger().info("Done!");
        Bukkit.getPluginManager().registerEvents(this, this);

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            Utils.update(p.getUniqueId(), p.getName());
        }
        getLogger().info("PlayTime enabled!");
    }

    @Override
    public void onDisable() {
        PlayerEntry.updatePlayers();
        playerDb.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Utils.update(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    private void startAutoSave() {
        int autosave = getConfig().getInt("autosave", -1) * 20;
        if (autosave < 1) {
            throw new IllegalStateException("Autosave must be above 0! (current: " + autosave + " seconds)");
        }
        new BukkitRunnable() {
            public void run() {
                PlayerEntry.updatePlayers();
                getPlayerDb().save();
                debug(getPlayerDb().getPlayers().toString());
            }
        }.runTaskTimer(this, autosave, autosave); // every minute
    }
}
