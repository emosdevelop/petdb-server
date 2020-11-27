package com.petdb.storage;

import com.petdb.storage.filehandler.FileHandler;
import com.petdb.parser.query.Key;
import com.petdb.parser.query.Keyword;
import com.petdb.parser.query.Value;
import com.petdb.transaction.Transaction;
import com.petdb.util.Extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class StorageHandler {

    //TODO key expiration time?

    private final static Map<Key, Value> STORE = new HashMap<>();

    private final FileHandler fileHandler = new FileHandler();

    public String set(Key key, Value value) {
        StorageHandler.STORE.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var value = StorageHandler.STORE.get(key);
        if (value == null) {
            return String.format("Key = %s, not set", key.getData());
        }
        return value.getData();
    }

    public String delete(Key key) {
        StorageHandler.STORE.remove(key);
        return Keyword.DELETE.toString();
    }

    public String commit(Transaction transaction) {
        StorageHandler.STORE.putAll(transaction.getMap());
        return String.format(Keyword.COMMIT + ": %s", transaction.getUuid());
    }

    public int count() {
        return StorageHandler.STORE.size();
    }

    public void flush() {
        StorageHandler.STORE.clear();
    }

    public void dump(Extension extension) {
        this.fileHandler.dump(extension);
    }

    public static Map<Key, Value> getSTORE() {
        return Collections.unmodifiableMap(StorageHandler.STORE);
    }
}
