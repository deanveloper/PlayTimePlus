package com.deanveloper.playtime.util;

import com.deanveloper.playtime.PlayTime;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

/**
 * @author Dean B
 */
public class Utils {
    private static BiMap<UUID, String> nameIdMap = HashBiMap.create(new CaseInsensitiveMap<UUID>()).inverse();

    public static UUID getUuid(String name) {
        if(name == null) throw new NullPointerException("Cannot get the UUID of a null username!");
        return nameIdMap.inverse().get(name);
    }

    public static String getName(UUID id) {
        if(id == null) throw new NullPointerException("Cannot get the name of a null UUID!");
        return nameIdMap.get(id);
    }

    public static String correctCase(String name) {
        for (String eachName : nameIdMap.values()) {
            if (eachName.equals(name)) {
                return eachName;
            }
        }
        return null;
    }

    public static void update(UUID id, String name) {
        nameIdMap.forcePut(id, name);
    }

    public static String forceGetName(UUID id) {
        String name = getName(id);

        if (name == null) {
            name = Bukkit.getOfflinePlayer(id).getName();
            update(id, name);
        }

        return name;
    }

    public static String getPrefix(String name) {
        if (name == null) throw new NullPointerException("Cannot get the prefix of a null name!");
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(name);
        if (team == null) return "";
        else return team.getPrefix();
    }

    public static String format(int seconds) {
        StringBuilder sb = new StringBuilder();

        int hours = seconds / 60 / 60;
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours != 1) {
                sb.append('s');
            }
            sb.append(", ");
        }

        //always include minutes, even if 0
        int minutes = seconds / 60 % 60;
        sb.append(minutes).append(" minute");
        if (minutes != 1) {
            sb.append('s');
        }

        return sb.toString();
    }
}
