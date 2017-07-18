package com.deanveloper.playtimeplus.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.hooks.ChatHook;
import com.deanveloper.playtimeplus.hooks.EssentialsHook;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
		if (name == null) {
			throw new NullPointerException("Cannot get the UUID of a null username!");
		}
		return nameIdMap.inverse().get(name.toLowerCase());
	}

	public static String getNameForce(UUID id) {
		String name = getName(id);
		if (name == null) {

			String newId = id.toString().replace("-", "");
			String json;
			try {
				json = getContent("https://sessionserver.mojang.com/session/minecraft/profile/" + newId);
			} catch (IOException e) {
				throw new RuntimeException("Problem getting name of " + id, e);
			}
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			name = obj.get("name").getAsString();

			update(id, name);
		}
		return name;
	}

	public static String getName(UUID id) {
		if (id == null) {
			throw new NullPointerException("Cannot get the name of a null UUID!");
		}
		PlayTimePlus.debug("ID -> NAME");
		PlayTimePlus.debug("[" + id + "] -> [" + correctCaseMap.get(nameIdMap.get(id)) + ']');
		String name = correctCaseMap.get(nameIdMap.get(id));
		if (name == null) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(id);
			name = op.getName();
			update(id, name);
		}
		return name;
	}

	private static String getContent(String web) throws IOException {
		URL url = new URL(web);
		return IOUtils.toString(url, "UTF8");
	}

	public static void update(UUID id, String name) {
		nameIdMap.forcePut(id, name.toLowerCase());
		correctCaseMap.put(name.toLowerCase(), name);
	}

	public static String correctCase(String name) {
		return correctCaseMap.getOrDefault(name.toLowerCase(), name.toLowerCase());
	}

	/**
	 * Returns the prefix of the player.
	 *
	 * First checks through vault. If vault is not installed, then it uses Scoreboards.
	 *
	 * @param world The world to check in
	 * @param id the id of the player
	 * @return the prefix of the player in the world
	 */
	public static String getPrefix(String world, UUID id) {
		if (ChatHook.isHooked()) {
			return ChatHook.getPrefix(world, id);
		}

		String name = getName(id);
		if (name == null){
			return "";
		}

		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(name);
		if (team == null) {
			return "";
		}

		return team.getPrefix();
	}

	public static String getNick(UUID id) {
		if (EssentialsHook.isHooked()) {
			return EssentialsHook.getNickname(id);
		}

		Player p = Bukkit.getPlayer(id);
		if (p != null) {
			return p.getDisplayName();
		}

		return getNameForce(id);
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

	public static String configMessage(String path, ConfigVar... vars) {
		String message = PlayTimePlus.getInstance().getConfig().getString(path);
		String prefix = PlayTimePlus.getInstance().getConfig().getString("messages.prefix");
		message = message.replace("{{prefix}}", prefix);
		for (ConfigVar cfVar : vars) {
			message = message.replace("{{" + cfVar.getKey() + "}}", cfVar.getValue());
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
}
