package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.util.Lazy;

/**
 * @author Dean
 */
public enum StorageMethod {
    JSON(Lazy.create(JsonStorage::new));

    private final Lazy<Storage> storageSupplier;

    StorageMethod(Lazy<Storage> storageSupplier) {
        this.storageSupplier = storageSupplier;
    }

    public Storage getStorage() {
        return storageSupplier.get();
    }
}
