package com.petdb.exception;

import java.io.IOException;

public class ClientConnectionClosedException extends IOException {
    public ClientConnectionClosedException() {
    }

    public ClientConnectionClosedException(String message) {
        super(message);
    }
}
