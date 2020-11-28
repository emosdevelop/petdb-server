package com.petdb.storage;

import com.petdb.parser.query.Keyword;
import com.petdb.storage.filehandler.FileHandler;
import com.petdb.transaction.Transaction;
import com.petdb.util.Extension;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class StorageHandler {

    private final static Map<String, String> STORE = new HashMap<>();
    private final static int MAX_MODIFIED = 5;

    private final FileHandler fileHandler = new FileHandler();
    private final AtomicInteger count = new AtomicInteger(0);

    public StorageHandler() {
        try {
            StorageHandler.STORE.putAll(this.fileHandler.loadFromDiskIfExists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String set(String key, String value) {
        StorageHandler.STORE.put(key, value);
        this.persistIfCountEquals();
        return "OK";
    }

    public String get(String key) {
        var value = StorageHandler.STORE.get(key);
        if (value == null) {
            return String.format("Key = %s, not set", key);
        }
        return value;
    }

    public String delete(String key) {
        StorageHandler.STORE.remove(key);
        return Keyword.DELETE.toString();
    }

    public String commit(Transaction transaction) {
        transaction.getMap().forEach(this::set);
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

    private void persistIfCountEquals() {
        int value = count.incrementAndGet();
        if (value == MAX_MODIFIED) {
            this.fileHandler.persist();
        }
    }

    public static Map<String, String> getSTORE() {
        return Collections.unmodifiableMap(StorageHandler.STORE);
    }
}
