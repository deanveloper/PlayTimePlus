package com.deanveloper.playtimeplus;

import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.Storage;
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
        Storage storage = PlayTimePlus.getStorage();
        PlayerEntry entry = storage.get(e.getPlayer().getUniqueId());
        if(entry == null) {
            entry = new PlayerEntry(e.getPlayer().getUniqueId());
            storage.getPlayers().put(e.getPlayer().getUniqueId(), entry);
        }

        entry.setOnline(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        PlayTimePlus.getStorage().get(e.getPlayer().getUniqueId()).setOnline(false);
    }

    @EventHandler
    public void onLeave(PlayerKickEvent e) {
        PlayTimePlus.getStorage().get(e.getPlayer().getUniqueId()).setOnline(false);
    }
}
