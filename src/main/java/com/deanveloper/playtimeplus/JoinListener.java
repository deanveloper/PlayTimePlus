package com.deanveloper.playtimeplus;

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
        PlayTimePlus.getManager().startNewEntry(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        PlayTimePlus.getManager().updateLastCount(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerKickEvent e) {
        PlayTimePlus.getManager().updateLastCount(e.getPlayer().getUniqueId());
    }
}
