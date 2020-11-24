package com.petdb.driver;

import com.petdb.server.Server;

import java.io.IOException;
import java.util.logging.Logger;

public final class Driver {

    private final static Logger LOGGER = Logger.getLogger(Driver.class.getName());

    public static void main(String[] args) {
        try {
            int port = 0;
            if (args.length != 1) {
                port = 12542;
                LOGGER.info("Default server start up");
            } else {
                port = Integer.parseInt(args[0]);
            }
            LOGGER.info(String.format("work dir = %s", System.getProperty("user.dir")));
            LOGGER.info(String.format("Port = %d", port));
            new Server(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
