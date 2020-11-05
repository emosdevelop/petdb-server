package com.petdb.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Transaction {
    private final UUID uuid = UUID.randomUUID();
    private final Map<String, String> localStore = new HashMap<>();
    private final String timestamp;

    Transaction(String timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, String> getLocalStore() {
        return localStore;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
