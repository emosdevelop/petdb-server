package com.petdb.transaction;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionHandlerTest {

    TransactionHandler transactionHandler = new TransactionHandler();

    @Test
    void rollback() {
        transactionHandler.begin();
        transactionHandler.set(new Key("key"), new Value("value"));
        transactionHandler.set(new Key("key1"), new Value("value1"));
        assertEquals(2, Integer.parseInt(transactionHandler.count()));
        transactionHandler.rollback();
        assertEquals(0, Integer.parseInt(transactionHandler.count()));
        transactionHandler.end();
    }

    @Test
    void commit() {
        transactionHandler.begin();
        transactionHandler.set(new Key("key"), new Value("value"));
        transactionHandler.set(new Key("key1"), new Value("value1"));
        var optional = transactionHandler.commit();
        var transaction = optional.get();
        assertEquals(2, transaction.getMap().size());
    }

    @Test
    void testChildTransaction() {
        transactionHandler.begin();
        transactionHandler.set(new Key("key"), new Value("value"));
        transactionHandler.set(new Key("key1"), new Value("value1"));
        transactionHandler.begin();
        assertEquals(0, Integer.parseInt(transactionHandler.count()));
        transactionHandler.end();
        assertEquals(2, Integer.parseInt(transactionHandler.count()));
        transactionHandler.end();
    }

}
