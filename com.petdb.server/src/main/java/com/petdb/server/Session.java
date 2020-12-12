package com.petdb.server;

import com.petdb.engine.Engine;
import com.petdb.parser.Parser;
import lombok.Data;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@Data
public final class Session {

    private final SelectionKey key;
    private final SocketChannel channel;
    private final SocketAddress address;
    private final Parser parser = new Parser();
    private final Engine engine = new Engine();

    public Session(SelectionKey key) throws IOException {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.address = this.channel.getRemoteAddress();
    }
}
