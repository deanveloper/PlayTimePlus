package com.deanveloper.playtimeplus.util.query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dean
 */
public class Query extends QueryExpression {
    private String type;
    private String valueAsString;
    private LocalDateTime time;
    private LocalDateTime endTime;

    public Query(String type, String valueAsString) {
        try {
            this.type = type;
            this.valueAsString = valueAsString;

            if (type.equals("after") || type.equals("before")) {
                time = parseTime(valueAsString);
            } else if(type.equals("between")) {
                String[] timesAsStrings = valueAsString.split("\\.\\.\\.");
                if(timesAsStrings.length == 2) {
                    time = parseTime(timesAsStrings[0]);
                    endTime = parseTime(timesAsStrings[1]);
                }
            }
        } catch (QueryParseException e) {
            throw new QueryParseException("Unable to parse " + type + ":" + valueAsString + " into a query!");
        }
    }

    private static Pattern DATE_PATTERN = Pattern.compile("" +
            "(?:Y:(?<Y>\\d+))?" +
            "(?:M:(?<M>\\d+))?" +
            "(?:d:(?<d>\\d+))?" +
            "(?:h:(?<h>\\d+))?" +
            "(?:m:(?<m>\\d+))?" +
            "(?:s:(?<s>\\d+))?");
    private static Pattern REL_PATTERN = Pattern.compile("" +
            "((?<Y>\\d+)(?:Y))?" +
            "((?<M>\\d+)(?:M))?" +
            "((?<w>\\d+)(?:w))?" +
            "((?<d>\\d+)(?:d))?" +
            "((?<h>\\d+)(?:h))?" +
            "((?<m>\\d+)(?:m))?" +
            "((?<s>\\d+)(?:s))?");

    private static LocalDateTime parseTime(String string) throws QueryParseException {
        Matcher dateMatcher = DATE_PATTERN.matcher(string);
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
            Matcher relMatcher = REL_PATTERN.matcher(string);
            if(relMatcher.matches()) {
                int years = fromGroup(relMatcher.group("Y"));
                int months = fromGroup(relMatcher.group("M"));
                int weeks = fromGroup(relMatcher.group("w"));
                int days = fromGroup(relMatcher.group("d"));
                int hours = fromGroup(relMatcher.group("h"));
                int minutes = fromGroup(relMatcher.group("m"));
                int seconds = fromGroup(relMatcher.group("s"));

                Duration dur = Duration.ofSeconds(seconds)
                        .plusMinutes(minutes)
                        .plusHours(hours)
                        .plusDays(days)
                        .plus(weeks, ChronoUnit.WEEKS)
                        .plus(months, ChronoUnit.MONTHS)
                        .plus(years, ChronoUnit.YEARS);

                return now.minus(dur);
            } else {
                throw new QueryParseException("Unable to parse " + string + " into a time!");
            }
        }
    }

    private static int fromGroup(String s) {
        return fromGroup(s, 0);
    }

    private static int fromGroup(String s, int def) {
        return s.isEmpty() ? def : Integer.parseInt(s);
    }
}
