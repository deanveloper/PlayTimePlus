package com.deanveloper.playtimeplus.hooks;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * @author Dean B
 */
public class EssentialsHook {
    private Essentials plugin;

    public EssentialsHook() {
        Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (plug != null) {
            plugin = (Essentials) plug;
            registerAfkHook();
        } else {
            Bukkit.getLogger().info("Essentials is not installed, we cannot account for if the player is AFK");
        }
    }

    /**
     * If the player is afk
     *
     * @param p The player to check
     * @return If they are AFK, assumes not AFK if essentials is not installed
     */
    public boolean isAfk(Player p) {
        return plugin != null && plugin.getUser(p).isAfk();
    }

    /**
     * If the player is afk
     *
     * @param p The player to check
     * @return If they are AFK, assumes not AFK if essentials is not installed
     */
    public boolean isAfk(UUID p) {
        return plugin != null && plugin.getUser(p).isAfk();
    }

    /**
     * The player's full name
     *
     * @param player The player to get the full name of
     * @return The player's full name
     */
    public String fullName(UUID player) {
        if (player == null) {
            throw new NullPointerException("player cannot be null!");
        }

        String nickName = Utils.getNameForce(player);
        if (plugin != null) {
            Player p = Bukkit.getPlayer(player);
            if (p != null && !plugin.getPermissionsHandler().getPrefix(p).isEmpty()) {
                if (plugin.getUser(player) != null) {
                    return plugin.getPermissionsHandler().getPrefix(p) + plugin.getUser(player).getNick(true);
                }
            } else {
                String nick = plugin.getUser(player).getNick(true);
                if (nick != null) {
                    nickName = nick;
                }
            }
        }
        return Utils.getPrefix(nickName) + nickName;
    }

    private void registerAfkHook() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onAfk(AfkStatusChangeEvent e) {
                Player p = e.getAffected().getBase();
                if (e.getValue()) { // if turning afk
                    PlayTimePlus.getManager().updateLastCount(p.getUniqueId());
                    PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to afk");
                } else {
                    PlayTimePlus.getManager().startNewEntry(p.getUniqueId());
                    PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to non-afk");
                }
            }
        }, PlayTimePlus.getInstance());
    }
}
