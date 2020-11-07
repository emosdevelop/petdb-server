package com.petdb.transaction;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Transaction {
    private final UUID uuid = UUID.randomUUID();
    private final Map<Key, Value> map = new HashMap<>();
    private final String timestamp;

    Transaction(String timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Map<Key, Value> getMap() {
        return this.map;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}
