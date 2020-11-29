package com.petdb.storage.filehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.petdb.storage.StorageHandler;
import com.petdb.util.Extension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.petdb.util.EngineUtil.DATE_FORMATTER;
import static com.petdb.util.EngineUtil.USER_DIR;
import static java.nio.file.StandardOpenOption.*;

public final class FileHandler {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Path USER_DIR_AS_PATH = Paths.get(USER_DIR);
    private static final String PERSISTENCE_FILE_NAME = "data.json";

    public void persist() {
        Path file = USER_DIR_AS_PATH.resolve(PERSISTENCE_FILE_NAME);
        try (var channel = AsynchronousFileChannel.open(
                file, Set.of(WRITE, CREATE), THREAD_POOL
        )) {
            String mapAsJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(StorageHandler.getSTORE());
            var buffer = ByteBuffer.wrap(mapAsJsonString.getBytes());
            var operation = channel.write(buffer, 0);
            operation.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void dump(Extension extension) {
        switch (extension) {
            case JSON:
                this.writeDump(extension, new ObjectMapper());
                return;
            case XML:
                this.writeDump(extension, new XmlMapper());
                return;
            default:
        }
    }

    private void writeDump(Extension extension, ObjectMapper mapper) {
        String fileName = this.buildDumpFileName(extension.getValue());
        Path file = USER_DIR_AS_PATH.resolve(fileName);
        try (var channel = AsynchronousFileChannel.open(
                file, Set.of(WRITE, TRUNCATE_EXISTING, CREATE), THREAD_POOL
        )) {
            String mapAsJsonString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(StorageHandler.getSTORE());
            var buffer = ByteBuffer.wrap(mapAsJsonString.getBytes());
            var operation = channel.write(buffer, 0);
            operation.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private String buildDumpFileName(String extension) {
        var builder = new StringBuilder();
        builder.append("PetDB_dump");
        builder.append(LocalDateTime.now().format(DATE_FORMATTER));
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }

    public Map<String, String> loadFromDiskIfExists() throws IOException {
        if (Files.exists(USER_DIR_AS_PATH.resolve(PERSISTENCE_FILE_NAME))) {
            TypeReference<HashMap<String, String>> typeRef
                    = new TypeReference<>() {
            };
            var mapAsJsonString = Files.readString(USER_DIR_AS_PATH.resolve(PERSISTENCE_FILE_NAME));
            return new ObjectMapper().readValue(mapAsJsonString, typeRef);
        }
        return Collections.emptyMap();
    }
}
