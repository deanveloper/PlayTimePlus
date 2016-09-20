package com.deanveloper.playtime.storage;

import com.deanveloper.playtime.util.Lazy;

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
