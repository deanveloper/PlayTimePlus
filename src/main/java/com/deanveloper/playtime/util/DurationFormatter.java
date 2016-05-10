package com.deanveloper.playtime.util;

import java.time.Duration;

/**
 * @author Dean B
 */
public class DurationFormatter {
	public static String format(Duration dur) {
		dur = dur.abs();
		StringBuilder sb = new StringBuilder();

		if (dur.toHours() > 0) {
			sb.append(dur.toHours()).append(" hour");
			if (dur.toHours() > 1) {
				sb.append('s');
			}
			sb.append(',');
		}

		//always include minutes, even if 0
		int minutes = (int) dur.toMinutes() % 60;
		sb.append(minutes).append(" minute");
		if (minutes > 1) {
			sb.append('s');
		}

		return sb.toString();
	}
}
