package com.deanveloper.playtimeplus;

import com.deanveloper.playtimeplus.commands.ConvertStorageCommand;
import com.deanveloper.playtimeplus.commands.DebugCommand;
import com.deanveloper.playtimeplus.commands.ExportPlayersCommand;
import com.deanveloper.playtimeplus.commands.playtime.PlayTimeCommand;
import com.deanveloper.playtimeplus.hooks.EssentialsHook;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.Storage;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.gson.DurationConverter;
import com.deanveloper.playtimeplus.util.gson.LocalDateTimeConverter;
import com.deanveloper.playtimeplus.util.gson.PlayerEntryConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
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
public class PlayTimePlus extends JavaPlugin implements Listener {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
            .registerTypeAdapter(Duration.class, new DurationConverter())
            .registerTypeAdapter(PlayerEntry.class, new PlayerEntryConverter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();
    public static boolean debugEnabled = false;
    private static Storage storage;
    private static EssentialsHook eHook;
    private static PlayTimePlus instance;

    public static PlayTimePlus getInstance() {
        return instance;
    }

    public static Storage getStorage() {
        return storage;
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
        getCommand("playtime").setExecutor(new PlayTimeCommand());
        getCommand("exportplayers").setExecutor(new ExportPlayersCommand());
        getCommand("debug").setExecutor(new DebugCommand());
        getCommand("convertstorage").setExecutor(new ConvertStorageCommand());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Done!");

        getLogger().info("Loading players...");
        storage = StorageMethod.valueOf(getConfig().getString("storage").toUpperCase()).getStorage();
        storage.init();
        getLogger().info("Done!");
        getLogger().info("Starting autosave...");
        startAutoSave();
        getLogger().info("Done!");

        getLogger().info("Hooking into essentials...");
        eHook = new EssentialsHook();
        getLogger().info("Done!");

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            Utils.update(p.getUniqueId(), p.getName());
        }
        getLogger().info("PlayTimePlus enabled!");
    }

    @Override
    public void onDisable() {
        storage.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Utils.update(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    public static void setStorage(StorageMethod storage) {
        PlayTimePlus.storage = storage.getStorage();
        instance.getConfig().set("storage", storage.name());
    }

    private void startAutoSave() {
        int autosave = getConfig().getInt("autosave", -1) * 20;
        if (autosave < 1) {
            throw new IllegalStateException("Autosave must be above 0! (current: " + autosave + " seconds)");
        }
        new BukkitRunnable() {
            public void run() {
                debug("Autosaving!");
                getStorage().save();
            }
        }.runTaskTimer(this, autosave, autosave);
    }
}
