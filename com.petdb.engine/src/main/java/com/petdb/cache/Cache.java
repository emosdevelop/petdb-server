package com.petdb.cache;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;
import com.petdb.transaction.Transaction;

import java.util.concurrent.ConcurrentHashMap;

public final class Cache {

    private final ConcurrentHashMap<Key, Value> cache = new ConcurrentHashMap<>();

    public String set(Key key, Value value) {
        this.cache.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var value = this.cache.get(key);
        if (value == null) {
            return "Key not set";
        }
        return value.getData();
    }

    public String delete(Key key) {
        this.cache.remove(key);
        return "DELETED";
    }

    public String commit(Transaction transaction) {
        this.cache.putAll(transaction.getMap());
        return String.format("COMMIT: %s", transaction.getUuid());
    }

    public String count() {
        return String.valueOf(this.cache.size());
    }
}
