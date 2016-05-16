package com.deanveloper.playtime.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.UUID;

/**
 * @author Dean B
 */
public class Utils {
	private static BiMap<UUID, CaseInsensitiveString> nameIdMap = HashBiMap.create();

	public static UUID getUuid(String name) {
		return nameIdMap.inverse().get(new CaseInsensitiveString(name));
	}

	public static String getName(UUID id) {
		return nameIdMap.get(id).toString();
	}

	public static String correctCase(String s) {
		for(CaseInsensitiveString cis : nameIdMap.values()) {
			if(cis.equals(s)) return cis.toString();
		}
		return null;
	}

	public static void update(UUID id, String name) {
		nameIdMap.forcePut(id, new CaseInsensitiveString(name));
	}

	public static String format(int seconds) {
		StringBuilder sb = new StringBuilder();

		int hours = seconds / 60 / 60;
		if (hours > 0) {
			sb.append(hours).append(" hour");
			if (hours > 1) {
				sb.append('s');
			}
			sb.append(',');
		}

		//always include minutes, even if 0
		int minutes = hours % 60;
		sb.append(minutes).append(" minute");
		if (minutes > 1) {
			sb.append('s');
		}

		return sb.toString();
	}
}
