package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.storage.binary.BinaryStorage;
import com.deanveloper.playtimeplus.storage.json.JsonStorage;
import com.deanveloper.playtimeplus.util.Lazy;

/**
 * @author Dean
 */
public enum StorageMethod {
    JSON(Lazy.create(JsonStorage::new), "Not very space efficient, but sorta user-readable"),
    BINARY(Lazy.create(BinaryStorage::new), "Space efficient, not user-readable");

    private final Lazy<Storage> storageSupplier;
    private final String desc;


    StorageMethod(Lazy<Storage> storageSupplier, String desc) {
        this.storageSupplier = storageSupplier;
        this.desc = desc;
    }

    public Storage getStorage() {
        return storageSupplier.get();
    }

    public String getDesc() {
        return desc;
    }
}
