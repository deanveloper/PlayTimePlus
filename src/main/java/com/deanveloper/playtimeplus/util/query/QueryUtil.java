package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.Utils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dean
 */
public class QueryUtil {
    /**
     * Queries are ways to filter out data so you only get what you want.
     *
     * @param query The query to pass
     * @return A list of players containing players that pass the query
     */
    public static Map<UUID, NavigableSet<TimeEntry>> query(String query) throws QueryException {
        return query(query.split("\\s"));
    }

    /**
     * Queries are ways to filter out data so you only get what you want.
     *
     * @param args The queries to pass
     * @return A list of incomplete PlayerEntries with players that pass the query along with their TimeEntries
     */
    public static Map<UUID, NavigableSet<TimeEntry>> query(String... args) throws QueryException {
        Map<UUID, NavigableSet<TimeEntry>> original = PlayTimePlus.getManager().getMap();
        Map<UUID, NavigableSet<TimeEntry>> mutating = new TreeMap<>();

        for(Map.Entry<UUID, NavigableSet<TimeEntry>> entry : original.entrySet()) {
            mutating.put(entry.getKey(), new TreeSet<>(entry.getValue()));
        }

        String currentOp = "and";
        for (int i = 0; i < args.length; i++) {
            // if the argument is in a spot designated for a query
            if (i % 2 == 0) {
                switch (currentOp) {
                    case "or":
                        or(mutating, args[i]);
                        break;
                    case "and":
                        and(mutating, args[i]);
                        break;
                }
            } else {
                currentOp = args[i];
            }
        }

        mutating = mutating.stream()
                .filter(pEntry -> !pEntry.getTimes().isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));

        return mutating;
    }

    /**
     * Applies the query to the set of players with the or operator.
     * This is done by applying a query to all play times and then combining them.
     * Note that this will mutate the NavigableSets
     *
     * @param players The times to combine with
     * @param query   The query to perform
     * @throws QueryException if something goes wrong. Error message always provided.
     */
    private static void or(Map<UUID, NavigableSet<TimeEntry>> players, String query) throws QueryException {
        Map<UUID, NavigableSet<TimeEntry>> queried = new HashMap<>();
        String[] parsed = parseQuery(query);
        String type = parsed[0];
        String value = parsed[1];

        for (Map.Entry<UUID, NavigableSet<TimeEntry>> entry : PlayTimePlus.getManager().getMap().entrySet()) {
            NavigableSet<TimeEntry> times = Query.filter(type, value, entry.getValue());
            queried.put(entry.getKey(), times);
        }

        combine(players, queried);
    }

    /**
     * Applies the query to the set of players with the and operator.
     * This is done by applying a query to the set of players which was provided.
     * Note that this will mutate each PlayerEntry in the set.
     *
     * @param players The player set to query
     * @param query   The query to perform
     * @throws QueryException if something goes wrong. Error message always provided.
     */
    private static void and(Map<UUID, NavigableSet<TimeEntry>> players, String query) throws QueryException {
        String[] parsed = parseQuery(query);
        String type = parsed[0];
        String value = parsed[1];

        for (Map.Entry<UUID, NavigableSet<TimeEntry>> entry : players.entrySet()) {
            NavigableSet<TimeEntry> times = Query.filter(type, value, new TreeSet<>(entry.getValue()));
            entry.getValue().clear();
            base.getTimes().addAll(times);
            base.mutated();
        }
    }

    /**
     * Combines two sets of playerentries in a specific way so that two TimeEntries don't overlap.
     * Note that set2 merges into set1
     * <p>
     * The order of this operation is O(n^2*m^2)
     * where n is the number of players in the set,
     * and m is the average number of time entries for each player.
     */
    private static void combine(NavigableSet<TimeEntry> set1, NavigableSet<TimeEntry> set2) {
        for (PlayerEntry toMerge : set2) {
            PlayerEntry entry = null;
            for (PlayerEntry each : set1) {
                if (each.getId().equals(toMerge.getId())) {
                    entry = each;
                    break;
                }
            }

            if (entry == null) {
                set1.add(toMerge);
            } else {
                Set<PlayerEntry.TimeEntry> toAdd = new HashSet<>();
                for (PlayerEntry.TimeEntry time1 : entry.getTimes()) {
                    for (PlayerEntry.TimeEntry time2 : toMerge.getTimes()) {
                        boolean startWithin = isWithin(time1.getStart(), time2);
                        boolean endWithin = isWithin(time1.getEnd(), time2);

                        if (startWithin || endWithin) {
                            if (isWithin(time1.getStart(), time2)) {
                                time1.setStart(time2.getStart());
                            }
                            if (isWithin(time1.getEnd(), time2)) {
                                time1.setEnd(time2.getEnd());
                            }
                        } else {
                            toAdd.add(time2);
                        }
                    }
                }
                entry.getTimes().addAll(toAdd);
                entry.mutated();
            }
        }
    }

    /**
     * If one LocalDateTime is within a TimeEntry.
     *
     * @param time  The time to test with.
     * @param range The entry to test with.
     * @return Whether the time is within the range.
     */
    private static boolean isWithin(LocalDateTime time, PlayerEntry.TimeEntry range) {
        return range.getStart().isBefore(time) && range.getEnd().isAfter(time);
    }

    /**
     * Parses a query into its type and value
     *
     * @param query The query to parse
     * @return An array of strings of length 2. [0] contains the type, [1] contains the value.
     * @throws QueryException If there is an error during parsing.
     */
    private static String[] parseQuery(String query) throws QueryException {
        StringBuilder builder = new StringBuilder();
        String type = null;
        boolean doneWithType = false;

        // Parse the query
        for (char c : query.toCharArray()) {
            if (doneWithType) {
                builder.append(c);
            } else {
                if (c == ':') {
                    type = builder.toString();
                    builder.setLength(0);
                    doneWithType = true;
                } else {
                    builder.append(c);
                    if (c == '<' || c == '>') {
                        type = builder.toString();
                        builder.setLength(0);
                        doneWithType = true;
                    }
                }
            }
        }
        String value = builder.toString();

        if (type == null || value.isEmpty()) {
            throw new QueryException("Type or value is empty!");
        }

        return new String[]{type, value};
    }
}
