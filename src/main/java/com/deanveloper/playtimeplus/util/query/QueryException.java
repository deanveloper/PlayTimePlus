package com.deanveloper.playtimeplus.util.query;

/**
 * @author Dean
 */
public class QueryException extends Exception {
    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
