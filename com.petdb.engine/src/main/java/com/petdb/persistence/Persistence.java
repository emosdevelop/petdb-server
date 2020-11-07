package com.petdb.persistence;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Persistence {

    private final static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public String persist(Map<Key, Value> cache, int bufferCapacity) {
        return null;
    }

    public int count() {
        return 0;
    }

    public void clear() {

    }
}
