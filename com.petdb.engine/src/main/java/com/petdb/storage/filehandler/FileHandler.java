package com.petdb.storage.filehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.petdb.util.Extension;
import com.petdb.storage.StorageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.petdb.util.EngineUtil.DATE_FORMATTER;
import static com.petdb.util.EngineUtil.USER_DIR;
import static java.nio.file.StandardOpenOption.*;

public final class FileHandler {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

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
        String fileName = this.buildDumpFile(extension.getValue());
        Path file = Paths.get(USER_DIR).resolve(fileName);
        try (var channel = AsynchronousFileChannel.open(
                file, Set.of(WRITE, TRUNCATE_EXISTING, CREATE), THREAD_POOL
        )) {
            String dataAsString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(StorageHandler.getSTORE());
            var buffer = ByteBuffer.wrap(dataAsString.getBytes());
            var operation = channel.write(buffer, 0);
            while (!operation.isDone()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildDumpFile(String extension) {
        var builder = new StringBuilder();
        builder.append("PetDB_dump");
        builder.append(LocalDateTime.now().format(DATE_FORMATTER));
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }
}
