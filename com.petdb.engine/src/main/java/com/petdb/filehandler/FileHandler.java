package com.petdb.filehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.petdb.util.EngineUtil.DATE_FORMATTER;
import static com.petdb.util.EngineUtil.USER_DIR;
import static java.nio.file.StandardOpenOption.*;

public final class FileHandler {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private final ObjectMapper mapper = new ObjectMapper();

    //TODO XML??
    public void dumpJSON(Map<Key, Value> store) {
        String fileName = "PetDB-dump" + LocalDateTime.now().format(DATE_FORMATTER) + ".json";
        Path file = Paths.get(USER_DIR).resolve(fileName);
        try (var channel = AsynchronousFileChannel.open(
                file, Set.of(WRITE, TRUNCATE_EXISTING, CREATE), THREAD_POOL
        )) {
            String json = this.mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(store);
            var buffer = ByteBuffer.wrap(json.getBytes());
            var operation = channel.write(buffer, 0);
            while (!operation.isDone()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
