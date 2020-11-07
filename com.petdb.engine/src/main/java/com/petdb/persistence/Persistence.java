package com.petdb.persistence;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class Persistence {

    private final Path BASE_PATH = Path.of(this.getClass().getResource("/data").getPath());
    private final static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public String persist(Map<Key, Value> cache) {
        long start = System.nanoTime();
        cache.forEach((key, value) -> {
            try {
                Path dir = this.mkdir(key);
                var file = this.touch(key, dir);
                this.write(file, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        long elapsedTime = end - start;
        long seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        return String.format("EVICT: It took %d to write %d key[s] and value[s]", seconds, cache.size());
    }

    private void write(Path file, Value value) throws IOException {
        try (var channel = AsynchronousFileChannel.open(
                file, Set.of(WRITE, TRUNCATE_EXISTING), THREAD_POOL
        )) {
            var buffer = ByteBuffer.wrap(value.getData().getBytes());
            var operation = channel.write(buffer, 0);
            while (!operation.isDone()) ;
        }
    }

    private Path mkdir(Key key) throws IOException {
        return Files.createDirectories(BASE_PATH.resolve(key.getData()));
    }

    private Path touch(Key key, Path path) throws IOException {
        var filePath = path.resolve(key.getData());
        return Files.notExists(filePath) ? Files.createFile(filePath) : filePath;
    }

    public int count() {
        return 0;
    }

    public void clear() {

    }
}
