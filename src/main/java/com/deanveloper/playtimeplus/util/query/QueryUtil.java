package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dean
 */
@SuppressWarnings("Duplicates")
public class QueryUtil {
    /**
     * Queries are ways to filter out data so you only get what you want.
     *
     * @param query The query to pass
     * @return A list of players containing players that pass the query
     */
    public static Set<PlayerEntry> query(String query) throws QueryException {
        return query(query.split("\\s"));
    }

    /**
     * Queries are ways to filter out data so you only get what you want.
     *
     * @param args The queries to pass
     * @return A list of incomplete PlayerEntries with players that pass the query along with their TimeEntries
     */
    public static Set<PlayerEntry> query(String... args) throws QueryException {
        Set<PlayerEntry> mutating = new HashSet<>();
        String currentOp = "or";
        for (int i = 0; i < args.length; i++) {

            // if the argument is in a spot designated for a query
            if(i % 2 == 0) {
                switch (currentOp) {
                    case "or":
                        for (PlayerEntry entry1 : mutating) {
                            for (PlayerEntry entry2 : querySingle(args[i])) {
                                or(entry1, entry2);
                            }
                        }
                        break;
                    case "and":
                        for (PlayerEntry entry1 : mutating) {
                            for (PlayerEntry entry2 : querySingle(args[i])) {
                                and(entry1, entry2);
                            }
                        }
                        break;
                }
            } else {
                currentOp = args[i];
            }
        }

        return mutating;
    }

    // OPERATIONS
    private static void or(PlayerEntry source, PlayerEntry query) {
        if(source.getId() != query.getId()) {
            return;
        }
        for(PlayerEntry.TimeEntry time1 : source.getTimes()) {
            for(PlayerEntry.TimeEntry time2 : query.getTimes()) {
                if(isWithin(time1.getStart(), time2)) {
                    time1.setStart(time2.getStart());
                }
                if(isWithin(time1.getEnd(), time2)) {
                    time1.setEnd(time2.getEnd());
                }
            }
        }
    }

    private static void and(PlayerEntry source, PlayerEntry query) {
        if(source.getId() != query.getId()) {
            return;
        }
        for(PlayerEntry.TimeEntry time1 : source.getTimes()) {
            for(PlayerEntry.TimeEntry time2 : query.getTimes()) {
                if(isWithin(time2.getStart(), time1)) {
                    time1.setStart(time2.getStart());
                }
                if(isWithin(time2.getEnd(), time1)) {
                    time1.setEnd(time2.getEnd());
                }
            }
        }
    }

    private static boolean isWithin(LocalDateTime time, PlayerEntry.TimeEntry range) {
        return range.getStart().isBefore(time) && range.getEnd().isAfter(time);
    }

    private static Set<PlayerEntry> querySingle(String query) throws QueryException {
        Set<PlayerEntry> toReturn = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        String type = null;
        boolean doneWithType = false;

        // Parse the query
        for(char c : query.toCharArray()) {
            if(doneWithType) {
                builder.append(c);
            } else {
                if(c == ':') {
                    type = builder.toString();
                    builder.setLength(0);
                    doneWithType = true;
                } else {
                    builder.append(c);
                    if(c == '<' || c == '>') {
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

        for (PlayerEntry base : PlayTimePlus.getPlayerDb().getPlayers().values()) {
            PlayerEntry entry = new PlayerEntry(base.getId());
            Query.queryPlayer(type, value, entry);
            toReturn.add(entry);
        }
        return toReturn;
    }
}
