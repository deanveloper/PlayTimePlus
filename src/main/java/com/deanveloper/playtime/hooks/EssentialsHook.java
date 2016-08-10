package com.deanveloper.playtime.hooks;

import com.deanveloper.playtime.util.Utils;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * @author Dean B
 */
public class EssentialsHook {
    private Essentials plugin;

    public EssentialsHook() {
        Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (plug != null) {
            plugin = (Essentials) plug;
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
     * The player's full name
     *
     * @param name The player to get the nickname of
     * @return The player's nickname
     */
    public String fullName(String name) {
        if(plugin != null) {
            Player p = Bukkit.getPlayerExact(name);
            if(!plugin.getPermissionsHandler().getPrefix(p).isEmpty()) {
                return Utils.getPrefix(p.getName()) + plugin.getUser(p).getNick(true);
            } else {
                return plugin.getUser(name).getNick(true);
            }
        } else {
            return Utils.getPrefix(name) + name;
        }
    }
}
