package com.petdb.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public final class Session {

    private final SelectionKey key;
    private final SocketChannel channel;
    private final SocketAddress address;

    public Session(SelectionKey key) throws IOException {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.address = this.channel.getRemoteAddress();
    }

    public SelectionKey getKey() {
        return this.key;
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    public SocketAddress getAddress() {
        return this.address;
    }
}
