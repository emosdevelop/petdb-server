package com.petdb.parser.query;

import lombok.Data;

@Data
public final class Value {
    private final String data;

    public Value(String data) {
        this.data = data;
    }
}
