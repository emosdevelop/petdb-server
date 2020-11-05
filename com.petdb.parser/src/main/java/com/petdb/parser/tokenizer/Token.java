package com.petdb.parser.tokenizer;

import lombok.Data;

@Data
public class Token {

    public final static int KEYWORD = 1;
    public final static int KEY = 4;
    public final static int VALUE = 8;

    private final int identifier;
    private final String sequence;

    public Token(int identifier, String sequence) {
        this.identifier = identifier;
        this.sequence = sequence;
    }
}
