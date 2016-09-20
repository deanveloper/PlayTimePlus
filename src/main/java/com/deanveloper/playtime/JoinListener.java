package com.deanveloper.playtime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Dean
 */
public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayTime.getPlayerDb().get(e.getPlayer().getUniqueId()).setOnline(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        PlayTime.getPlayerDb().get(e.getPlayer().getUniqueId()).setOnline(false);
    }

    @EventHandler
    public void onLeave(PlayerKickEvent e) {
        PlayTime.getPlayerDb().get(e.getPlayer().getUniqueId()).setOnline(false);
    }
}
