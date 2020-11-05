package com.petdb.parser.query;

import lombok.Data;

@Data
public final class Key {
    private final String data;

    public Key(String data) {
        this.data = data;
    }
}
