package com.deanveloper.playtimeplus.util.query;

/**
 * @author Dean
 */
public class QueryParseException extends RuntimeException {
    public QueryParseException(String message) {
        super(message);
    }

    public QueryParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
