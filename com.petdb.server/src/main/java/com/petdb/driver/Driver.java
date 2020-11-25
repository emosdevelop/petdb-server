package com.petdb.driver;

import com.petdb.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public final class Driver {

    private final static Logger LOGGER = Logger.getLogger(Driver.class.getName());
    private final static int DEFAULT_PORT = 12542;

    static {
        printBanner();
    }

    public static void main(String[] args) {
        int port = Driver.getPort(args);
        try {
            new Server(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getPort(String[] args) {
        int port = 0;
        if (args.length != 1) {
            LOGGER.info(String.format("Default port = %d", DEFAULT_PORT));
            return DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }
        LOGGER.info(String.format("Port = %d", port));
        return port;
    }

    private static void printBanner() {
        try (var reader = new BufferedReader(
                new InputStreamReader(
                        Driver.class.getResourceAsStream("/petdb-banner.txt")
                ))) {
            reader.lines().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
