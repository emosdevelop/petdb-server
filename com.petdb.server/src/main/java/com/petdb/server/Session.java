package com.petdb.server;

import com.petdb.engine.Engine;
import com.petdb.parser.Parser;
import com.petdb.parser.query.Query;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class Session {

    private final static int END_OF_STREAM = -1;

    private final SelectionKey key;
    private final SocketChannel channel;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 1024);
    private final Parser parser = new Parser();
    private final Engine engine = new Engine();

    public Session(SelectionKey key) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
    }

    public void read() throws IOException {
        this.readBuffer.clear();
        int bytesRead = this.channel.read(this.readBuffer);
        if (bytesRead == END_OF_STREAM) {
            throw new IOException();
        }
        String request = new String(this.readBuffer.array(), 0, bytesRead, StandardCharsets.UTF_8);
        Optional<Query> query = this.parser.parse(request);
        String response = query.map(q -> this.engine.execute(q, this.readBuffer.capacity()))
                .orElseGet(() -> String.format("Error: input -> \"%s\" is not a valid syntax", request));
        this.channel.register(
                this.key.selector(),
                SelectionKey.OP_WRITE,
                ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
    }

    public void write() throws IOException {
        var buffer = (ByteBuffer) this.key.attachment();
        while (buffer.hasRemaining()) {
            this.channel.write(buffer);
        }
        this.key.interestOps(SelectionKey.OP_READ);
    }
}
