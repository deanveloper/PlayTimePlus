package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dean
 */
public class Query {
    private static Pattern DATETIME_PATTERN = Pattern.compile("" +
            "(?:Y:(?<Y>\\d+),?)?" +
            "(?:M:(?<M>\\d+),?)?" +
            "(?:d:(?<d>\\d+),?)?" +
            "(?:h:(?<h>\\d+),?)?" +
            "(?:m:(?<m>\\d+),?)?" +
            "(?:s:(?<s>\\d+),?)?");
    private static Pattern DURATION_PATTERN = Pattern.compile("" +
            "((?<Y>\\d+)(?:Y),?)?" +
            "((?<M>\\d+)(?:M),?)?" +
            "((?<w>\\d+)(?:w),?)?" +
            "((?<d>\\d+)(?:d),?)?" +
            "((?<h>\\d+)(?:h),?)?" +
            "((?<m>\\d+)(?:m),?)?" +
            "((?<s>\\d+)(?:s),?)?");

    private Query() {
    }

    /**
     * Performs the query on the player.
     *
     * @param type          The type of query to perform
     * @param valueAsString the value of the query
     * @param times         The times to query
     * @throws              QueryException If something is wrong with the query
     */
    static NavigableSet<TimeEntry> filter(String type, String valueAsString, NavigableSet<TimeEntry> times) throws QueryException {
        NavigableSet<TimeEntry> toReturn = new TreeSet<>();
        final Duration duration;
        final LocalDateTime time;

        switch (type) {
            case "total<":
            case "total>":
                duration = parseDur(valueAsString);
                Duration total = Duration.ZERO;
                for(TimeEntry entry : times) {
                    total = total.plus(entry.getDuration());
                }

                switch (type) {
                    case "total>":
                        if (total.compareTo(duration) > 0) {
                            toReturn.addAll(times);
                        }
                        break;
                    case "total<":
                        if (total.compareTo(duration) < 0) {
                            toReturn.addAll(times);
                        }
                        break;
                }
                break;

            case "after":
            case "before":
                time = parseTime(valueAsString);
                TimeEntry entry = new TimeEntry(time, time);

                switch (type) {
                    case "after": {
                        // automatically add all elements that start after the given time
                        toReturn.addAll(times.tailSet(entry));

                        // check for an intersection on the last element not added in the original list
                        TimeEntry leftover = times.lower(entry);
                        if(leftover != null) {
                            if (leftover.getEnd().isAfter(time)) {
                                toReturn.add(new TimeEntry(time, leftover.getEnd()));
                            }
                        }
                        break;
                    }
                    case "before": {
                        // automatically add all elements that start before the given time
                        toReturn.addAll(times.headSet(entry));

                        // check for an intersection on the last element in the returning list
                        if(!toReturn.isEmpty()) {
                            if(toReturn.last().getEnd().isBefore(time)) {
                                toReturn.add(toReturn.pollLast().newEnd(time));
                            }
                        }

                        break;
                    }
                }
                break;
            default:
                throw new QueryException(type + " is not a valid query type!");
        }

        return toReturn;
    }

    private static LocalDateTime parseTime(String string) throws QueryException {
        try {
            Matcher dateMatcher = DATETIME_PATTERN.matcher(string);
            LocalDateTime now = LocalDateTime.now();
            if (dateMatcher.matches()) {
                int year = fromGroup(dateMatcher.group("Y"), now.getYear());
                int month = fromGroup(dateMatcher.group("M"), now.getMonthValue() + 1) - 1;
                int day = fromGroup(dateMatcher.group("d"), now.getDayOfMonth());
                int hour = fromGroup(dateMatcher.group("h"), now.getHour());
                int minute = fromGroup(dateMatcher.group("m"), now.getMinute());
                int second = fromGroup(dateMatcher.group("s"), now.getSecond());

                return LocalDateTime.of(year, month, day, hour, minute, second);
            } else {
                return now.minus(parseDur(string));
            }
        } catch (QueryException e) {
            throw new QueryException("Cannot parse datetime from " + string);
        }
    }

    private static Duration parseDur(String string) throws QueryException {
        Matcher relMatcher = DURATION_PATTERN.matcher(string);
        if (relMatcher.matches()) {
            int years = fromGroup(relMatcher.group("Y"));
            int months = fromGroup(relMatcher.group("M"));
            int weeks = fromGroup(relMatcher.group("w"));
            int days = fromGroup(relMatcher.group("d"));
            int hours = fromGroup(relMatcher.group("h"));
            int minutes = fromGroup(relMatcher.group("m"));
            int seconds = fromGroup(relMatcher.group("s"));

            LocalDateTime now = LocalDateTime.now();

            return Duration.between(now
                            .minusSeconds(seconds)
                            .minusMinutes(minutes)
                            .minusHours(hours)
                            .minusDays(days)
                            .minusWeeks(weeks)
                            .minusDays(days)
                            .minusMonths(months)
                            .minusYears(years),
                    now);
        } else {
            throw new QueryException("Cannot parse duration from " + string);
        }
    }

    private static int fromGroup(String s) {
        return fromGroup(s, 0);
    }

    private static int fromGroup(String s, int def) {
        return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
    }
}
