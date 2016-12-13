package com.deanveloper.playtimeplus.storage;

import com.deanveloper.playtimeplus.storage.binary.BinaryManager;
import com.deanveloper.playtimeplus.storage.json.JsonManager;
import com.deanveloper.playtimeplus.util.Lazy;

/**
 * @author Dean
 */
public enum StorageMethod {
    JSON(Lazy.create(JsonManager::new), "Not very space efficient, but sorta user-readable"),
    BINARY(Lazy.create(BinaryManager::new), "Space efficient, not user-readable");

    private final Lazy<Manager> storageSupplier;
    private final String desc;


    StorageMethod(Lazy<Manager> storageSupplier, String desc) {
        this.storageSupplier = storageSupplier;
        this.desc = desc;
    }

    public Manager getStorage() {
        return storageSupplier.get();
    }

    public String getDesc() {
        return desc;
    }
}
