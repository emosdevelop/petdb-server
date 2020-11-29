package com.petdb.exception;

import java.io.IOException;

public class EndOfStreamException extends IOException {
    public EndOfStreamException() {
    }

    public EndOfStreamException(String message) {
        super(message);
    }
}
