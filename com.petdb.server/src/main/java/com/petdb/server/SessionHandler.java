package com.petdb.server;

import com.petdb.exception.EndOfStreamException;
import com.petdb.parser.query.Query;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public final class SessionHandler {

    private final static Logger LOGGER = Logger.getLogger(SessionHandler.class.getName());
    private final static int END_OF_STREAM = -1;
    private final static Map<SelectionKey, Session> CLIENT_SESSIONS = new HashMap<>();
    private final static ByteBuffer READ_BUFFER = ByteBuffer.allocate(1024 * 1024);

    public void accept(SelectionKey key) throws IOException {
        var client = ((ServerSocketChannel) key.channel()).accept();
        client.configureBlocking(false);
        var clientKey = client.register(key.selector(), SelectionKey.OP_READ);
        CLIENT_SESSIONS.put(clientKey, new Session(clientKey));
        LOGGER.info(String.format("New client connected : %s", client.getRemoteAddress()));
        //TODO Timeout???
    }

    public void read(SelectionKey key) throws IOException {
        var session = CLIENT_SESSIONS.get(key);
        READ_BUFFER.clear();
        int bytesRead = session.getChannel().read(READ_BUFFER);
        if (bytesRead == END_OF_STREAM) {
            throw new EndOfStreamException();
        }
        String request = new String(
                READ_BUFFER.array(), 0, bytesRead, StandardCharsets.UTF_8);
        Optional<Query> query = session.getParser().parse(request);
        String response = query.map(session.getEngine()::execute)
                .orElseGet(() -> String.format("Error: input -> \"%s\" is not a valid syntax", request));
        session.getChannel().register(
                session.getKey().selector(),
                SelectionKey.OP_WRITE,
                ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
    }

    public void write(SelectionKey key) throws IOException {
        var session = CLIENT_SESSIONS.get(key);
        var buffer = (ByteBuffer) session.getKey().attachment();
        while (buffer.hasRemaining()) {
            session.getChannel().write(buffer);
        }
        session.getKey().interestOps(SelectionKey.OP_READ);
    }

    public void close(SelectionKey key) {
        var session = CLIENT_SESSIONS.get(key);
        try {
            key.channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            key.cancel();
            CLIENT_SESSIONS.remove(key, session);
            LOGGER.info(
                    String.format("Client disconnected: %s", session.getAddress()));
        }
    }
}
