package com.petdb.server;

import com.petdb.exception.ClientConnectionClosedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class Server {

    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    private final Selector selector = Selector.open();
    private final SessionHandler sessionHandler = new SessionHandler();

    public Server(int port) throws IOException {
        var server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.register(this.selector, SelectionKey.OP_ACCEPT);
        server.bind(new InetSocketAddress(port));
        LOGGER.info("Server started successfully");
    }

    public void start() throws IOException {
        while (this.selector.isOpen()) {
            int channels = this.selector.select();
            if (channels == 0) continue;
            var keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    this.handleAccept(key);
                } else if (key.isReadable()) {
                    this.handleRead(key);
                } else if (key.isWritable()) {
                    this.handleWrite(key);
                } else if (key.isConnectable()) {
                    // Remote connection
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep
                        (ThreadLocalRandom.current().nextInt(100, 500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey key) {
        try {
            this.sessionHandler.accept(key, this.selector);
        } catch (IOException e) {
            e.printStackTrace();
            this.sessionHandler.close(key);
        }
    }

    private void handleRead(SelectionKey key) {
        try {
            this.sessionHandler.read(key);
        } catch (ClientConnectionClosedException e) {
            this.sessionHandler.close(key);
        } catch (IOException e) {
            e.printStackTrace();
            this.sessionHandler.close(key);
        }
    }

    private void handleWrite(SelectionKey key) {
        try {
            this.sessionHandler.write(key);
        } catch (IOException e) {
            e.printStackTrace();
            this.sessionHandler.close(key);
        }
    }
}
