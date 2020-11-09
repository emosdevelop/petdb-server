package com.petdb.server;

import com.petdb.engine.Engine;
import com.petdb.parser.Parser;
import com.petdb.parser.query.Query;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public final class Session {
    private final static int END_OF_STREAM = -1;

    private final SelectionKey key;
    private final SocketChannel channel;
    private final Parser parser = new Parser();
    private final Engine engine = new Engine();

    public Session(SelectionKey key) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
    }

    public void read(int bufferCapacity) throws IOException {
        var buffer = ByteBuffer.allocate(bufferCapacity);
        int bytesRead = this.channel.read(buffer);
        buffer.clear();
        if (bytesRead == END_OF_STREAM) {
            throw new IOException();
        }
        String request = new String(buffer.array()).trim();
        Optional<Query> query = this.parser.parse(request);
        String response = query.map(q -> this.engine.execute(q, bufferCapacity))
                .orElseGet(() -> String.format("Error: input -> \"%s\" is not a valid syntax", request));
        this.channel.register(this.key.selector(), SelectionKey.OP_WRITE, ByteBuffer.wrap(response.getBytes()));
    }

    public void write() throws IOException {
        var buffer = (ByteBuffer) this.key.attachment();
        while (buffer.hasRemaining()) {
            this.channel.write(buffer);
        }
        this.key.interestOps(SelectionKey.OP_READ);
    }
}
