package com.deanveloper.playtime.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dean B
 */
public class Utils {
    private static BiMap<UUID, String> nameIdMap = HashBiMap.create(new CaseInsensitiveMap<UUID>()).inverse();
    private static Map<String, String> correctCaseMap = new HashMap<>();

    public static UUID getUuid(String name) {
        if(name == null) throw new NullPointerException("Cannot get the UUID of a null username!");
        return nameIdMap.inverse().get(name.toLowerCase());
    }

    public static String getName(UUID id) {
        if(id == null) throw new NullPointerException("Cannot get the name of a null UUID!");
        return correctCaseMap.get(nameIdMap.get(id));
    }

    public static String correctCase(String name) {
        return correctCaseMap.getOrDefault(name.toLowerCase(), name.toLowerCase());
    }

    public static void update(UUID id, String name) {
        nameIdMap.forcePut(id, name.toLowerCase());
        correctCaseMap.put(name.toLowerCase(), name);
    }

    public static String getPrefix(String name) {
        if (name == null) throw new NullPointerException("Cannot get the prefix of a null name!");
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(name);
        if (team == null) return "";
        else return team.getPrefix();
    }

    public static String format(Duration dur) {
        StringBuilder sb = new StringBuilder();

        int hours = (int) dur.toHours();
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours != 1) {
                sb.append('s');
            }
            sb.append(", ");
        }

        //always include minutes, even if 0
        int minutes = (int) dur.toMinutes() % 60;
        sb.append(minutes).append(" minute");
        if (minutes != 1) {
            sb.append('s');
        }

        return sb.toString();
    }
}
