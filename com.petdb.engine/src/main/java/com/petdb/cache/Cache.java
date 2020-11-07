package com.petdb.cache;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;
import com.petdb.transaction.Transaction;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Cache {

    private final static ConcurrentHashMap<Key, Value> store = new ConcurrentHashMap<>();

    public Cache() {
        //TODO load from disk
    }

    public String set(Key key, Value value) {
        Cache.store.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var value = Cache.store.get(key);
        if (value == null) {
            return "Key not set";
        }
        return value.getData();
    }

    public String delete(Key key) {
        Cache.store.remove(key);
        return "DELETED";
    }

    public String commit(Transaction transaction) {
        Cache.store.putAll(transaction.getMap());
        return String.format("COMMIT: %s", transaction.getUuid());
    }

    public int count() {
        return Cache.store.size();
    }

    public Map<Key, Value> getStore() {
        return Collections.unmodifiableMap(Cache.store);
    }

    public void clear() {
        Cache.store.clear();
    }
}
