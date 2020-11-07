package com.petdb.driver;

import com.petdb.server.Server;

import java.io.IOException;
import java.util.logging.Logger;

public final class Driver {

    private final static Logger LOGGER = Logger.getLogger(Driver.class.getName());

    public static void main(String[] args) {
        try {
            int port = 0;
            int bufferCapacity = 0;
            if (args.length != 2) {
                port = 12542;
                bufferCapacity = 1024;
                LOGGER.info("Default server start up");
            } else {
                port = Integer.parseInt(args[0]);
                bufferCapacity = Integer.parseInt(args[1]);
            }
            LOGGER.info(String.format("Port = %d", port));
            LOGGER.info(String.format("Buffer Capacity = %d", bufferCapacity));
            new Server(port, bufferCapacity).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
