package com.deanveloper.playtimeplus.storage;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Dean
 */
public class TimeEntry implements Comparable<TimeEntry> {

	@SerializedName("s")
	private final LocalDateTime start;
	@SerializedName("e")
	private final LocalDateTime end;

	private transient Duration dur;

	public TimeEntry(LocalDateTime start, LocalDateTime end) {
		this.start = start;
		this.end = end;
		dur = Duration.between(start, end);
	}

	public Duration getDuration() {
		return dur;
	}

	public TimeEntry newEnd(LocalDateTime end) {
		return new TimeEntry(this.start, end);
	}

	@Override
	public String toString() {
		return "TimeEntry[start=" + start + ",end=" + end + "]";
	}

	@Override
	public int compareTo(TimeEntry o) {
		return start.compareTo(o.start);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TimeEntry) {
			TimeEntry entry = ((TimeEntry) o);

			return this.getStart().equals(entry.getStart()) && this.getEnd().equals(entry.getEnd());
		}

		return false;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}
}