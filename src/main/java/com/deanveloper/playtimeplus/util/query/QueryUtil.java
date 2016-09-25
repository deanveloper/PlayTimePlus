package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.storage.PlayerEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dean
 */
public class QueryUtil {
    /**
     * Queries are ways to filter out data you don't want.
     *
     * @param query The query to pass
     * @return A list of players containing players that pass the query
     */
    public static List<PlayerEntry> query(String query) throws QueryException {
        return query(query.split("\\s"));
    }

    /**
     * Queries are ways to filter out data you don't want.
     *
     * @param args The queries to pass
     * @return A list of incomplete PlayerEntries with players that pass the query along with their TimeEntries
     */
    public static List<PlayerEntry> query(String... args) throws QueryException {
        if (args.length == 1) {
            return querySingle(args[0]);
        } else {
            for (int i = 0; i < args.length; i++) {
                // if the argument is in a spot designated for a query
                List<PlayerEntry> current = new ArrayList<>();
                if(i % 2 == 1) {
                    String[] split = args[i].split(":", 2);
                    current.add(Query.query(split[0], split[1], current));
                }
            }
        }


        for (String query : args) {

            String[] split = arg.split(":", 2);
            String key = split[0];
            String value = split[1];


        }
    }

    private static List<PlayerEntry> querySingle(String query) throws QueryException {
        List<PlayerEntry> toReturn = new ArrayList<>();
        for (PlayerEntry base : PlayTimePlus.getPlayerDb().getPlayers().values()) {
            PlayerEntry entry = new PlayerEntry(base.getId());
            for (PlayerEntry.TimeEntry time : entry.getTimes()) {
                Query q = new Query();
            }
        }
    }
}
