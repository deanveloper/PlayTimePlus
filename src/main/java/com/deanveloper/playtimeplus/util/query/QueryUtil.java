package com.deanveloper.playtimeplus.util.query;

import com.deanveloper.playtimeplus.storage.PlayerEntry;

import java.util.List;

/**
 * @author Dean
 */
public class QueryUtil {
    /**
     * Queries are ways to filter out data you don't want.
     *
     * @param query The query to pass
     * @return      A list of players containing players that pass the query
     */
    public static List<PlayerEntry> query(String query) {
        return query(query.split("\\s"));
    }

    /**
     * Queries are ways to filter out data you don't want.
     *
     * @param query The query to pass
     * @return      A list of players containing players that pass the query
     */
    public static List<PlayerEntry> query(String...query) {
        for(String arg : query) {
            String[] split = arg.split(":", 2);
            String key = split[0];
            String value = split[1];

        }
    }
}
