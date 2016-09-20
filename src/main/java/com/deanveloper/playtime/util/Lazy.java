package com.deanveloper.playtime.util;

import java.util.function.Supplier;

/**
 * @author Dean
 */
public interface Lazy<T> extends Supplier<T> {
    static <T> Lazy<T> create(Lazy<T> lazy) {
        return new Lazy<T>() {
            private T field = null;

            @Override
            public T get() {
                if (field == null) {
                    field = lazy.get();
                }
                return field;
            }
        };
    }
}
