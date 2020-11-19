package com.petdb.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public final class Server {

    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public final static Map<SelectionKey, Session> CLIENT_SESSIONS = new HashMap<>();

    private final Selector selector;

    public Server(int port) throws IOException {
        this.selector = Selector.open();
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
                if (!key.isValid()) continue;
                if (key.isAcceptable()) this.handleAccept(key);
                else if (key.isReadable()) this.handleRead(key);
                else if (key.isWritable()) this.handleWrite(key);
            }
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        var client = ((ServerSocketChannel) key.channel()).accept();
        client.configureBlocking(false);
        var clientKey = client.register(this.selector, SelectionKey.OP_READ);
        Server.CLIENT_SESSIONS.put(clientKey, new Session(clientKey));
        LOGGER.info(String.format("New client connected : %s", client.getRemoteAddress()));
        //TODO Timeout???
    }

    private void handleRead(SelectionKey key) {
        var session = Server.CLIENT_SESSIONS.get(key);
        try {
            session.read();
        } catch (IOException e) {
            this.closeSession(key, session);
        }
    }

    private void handleWrite(SelectionKey key) {
        var session = Server.CLIENT_SESSIONS.get(key);
        try {
            session.write();
        } catch (IOException e) {
            this.closeSession(key, session);
        }
    }

    private void closeSession(SelectionKey key, Session session) {
        try {
            key.channel().close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            key.cancel();
            Server.CLIENT_SESSIONS.remove(key, session);
        }
    }
}
