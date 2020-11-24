package com.petdb.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    private final Parser parser = new Parser();

    private final String[] shouldBeValid = {
            "bEgiN",
            "Rollback",
            "COMMIT",
            "END",
            "SET myKey myValue",
            "get myKey",
            "delete mykey",
            "count",
            "dump",
            "fluSh",
    };

    private final String[] shouldBeInValid = {
            null,
            "rollback 2 222",
            "rollbackz",
            "commitnas",
            "",
            "asz",
            "delete key value",
            "count 123",
            "cleaR hello",
    };

    @Test
    void validCommands() {
        for (String command : this.shouldBeValid) {
            var query = parser.parse(command);
            assertTrue(query.isPresent());
        }
    }

    @Test
    void invalidCommands() {
        for (String command : this.shouldBeInValid) {
            var query = parser.parse(command);
            assertFalse(query.isPresent());
        }
    }
}
