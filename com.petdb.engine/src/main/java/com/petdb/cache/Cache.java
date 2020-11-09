package com.petdb.cache;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;
import com.petdb.transaction.Transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Cache {

    private final static Map<Key, Value> STORE = new HashMap<>();

    public Cache() {
        //TODO load from disk
    }

    public String set(Key key, Value value) {
        Cache.STORE.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var value = Cache.STORE.get(key);
        if (value == null) {
            return String.format("Key = %s, not set", key.getData());
        }
        return value.getData();
    }

    public String delete(Key key) {
        Cache.STORE.remove(key);
        return "DELETED";
    }

    public String commit(Transaction transaction) {
        Cache.STORE.putAll(transaction.getMap());
        return String.format("COMMIT: %s", transaction.getUuid());
    }

    public int count() {
        return Cache.STORE.size();
    }

    public Map<Key, Value> getStore() {
        return Collections.unmodifiableMap(Cache.STORE);
    }

    public void clear() {
        Cache.STORE.clear();
    }
}
