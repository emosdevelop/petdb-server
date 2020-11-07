package com.petdb.cache;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;
import com.petdb.transaction.Transaction;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Cache {

    private final ConcurrentHashMap<Key, Value> store = new ConcurrentHashMap<>();

    public String set(Key key, Value value) {
        this.store.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var value = this.store.get(key);
        if (value == null) {
            return "Key not set";
        }
        return value.getData();
    }

    public String delete(Key key) {
        this.store.remove(key);
        return "DELETED";
    }

    public String commit(Transaction transaction) {
        this.store.putAll(transaction.getMap());
        return String.format("COMMIT: %s", transaction.getUuid());
    }

    public int count() {
        return this.store.size();
    }

    public Map<Key, Value> getStore() {
        return Collections.unmodifiableMap(this.store);
    }

    public void clear() {
        this.store.clear();
    }
}
