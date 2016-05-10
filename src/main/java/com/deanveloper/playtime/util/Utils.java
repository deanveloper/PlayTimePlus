package com.deanveloper.playtime.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collector;

/**
 * @author Dean B
 */
public class Utils {
	public static String format(int seconds) {
		Duration dur = Duration.of(seconds, ChronoUnit.SECONDS);

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

	/**
	 * From http://stackoverflow.com/questions/30476127/get-last-n-elements-from-stream
	 *
	 * @param n     The number of things to return
	 * @param <T>   Type of stream
	 * @return      A collector that will return the last N elements of the stream
	 */
	public static <T> Collector<T, ?, List<T>> lastN(int n) {
		return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
			if(acc.size() == n)
				acc.pollFirst();
			acc.add(t);
		}, (acc1, acc2) -> {
			while(acc2.size() < n && !acc1.isEmpty()) {
				acc2.addFirst(acc1.pollLast());
			}
			return acc2;
		}, ArrayList<T>::new);
	}
}
