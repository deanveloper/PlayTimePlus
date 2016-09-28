package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
     * @param pEntry        The PlayerEntry to use
     * @throws              QueryException If something is wrong with the query
     */
    static Set<PlayerEntry.TimeEntry> queryPlayer(String type, String valueAsString, PlayerEntry pEntry) throws QueryException {
        Set<PlayerEntry.TimeEntry> toReturn = new HashSet<>();
        final Duration duration;
        final LocalDateTime time;

        switch (type) {
            case "total<":
            case "total>":
                duration = parseDur(valueAsString);

                switch (type) {
                    case "total>":
                        if (pEntry.getTotalTime().compareTo(duration) > 0) {
                            toReturn.addAll(Utils.cloneElements(pEntry.getTimes()));
                        }
                        break;
                    case "total<":
                        if (pEntry.getTotalTime().compareTo(duration) < 0) {
                            toReturn.addAll(Utils.cloneElements(pEntry.getTimes()));
                        }
                        break;
                }
                break;
            case "after":
            case "before":
                time = parseTime(valueAsString);

                switch (type) {
                    case "after": {
                        pEntry.getTimes().stream()
                                .filter(tEntry -> tEntry.getEnd().isAfter(time))
                                .map(PlayerEntry.TimeEntry::clone)
                                .forEach(tEntry -> {
                                    if (!tEntry.getStart().isAfter(time)) {
                                        tEntry.setStart(time);
                                    }
                                    toReturn.add(tEntry);
                                });
                    }
                    case "before": {
                        pEntry.getTimes().stream()
                                .filter(tEntry -> tEntry.getStart().isBefore(time))
                                .map(PlayerEntry.TimeEntry::clone)
                                .forEach(tEntry -> {
                                    if (!tEntry.getEnd().isAfter(time)) {
                                        tEntry.setEnd(time);
                                    }
                                    toReturn.add(tEntry);
                                });
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
