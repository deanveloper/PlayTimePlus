package com.deanveloper.playtime.storage;

/**
 * @author Dean
 */
public enum StorageMethod {
    JSON(new JsonStorage());

    private final Storage storage;

    StorageMethod(Storage storage) {

        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }
}
