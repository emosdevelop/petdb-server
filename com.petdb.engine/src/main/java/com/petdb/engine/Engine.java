package com.petdb.engine;


import com.petdb.cache.Cache;
import com.petdb.parser.query.Query;
import com.petdb.transaction.TransactionHandler;

public final class Engine {

    private final TransactionHandler transactionHandler = new TransactionHandler();
    private final Cache cache = new Cache();

    public String execute(Query query) {
        switch (query.getKeyword()) {
            case BEGIN:
                return this.transactionHandler.begin();
            case ROLLBACK:
                return this.transactionHandler.rollback();
            case COMMIT:
                var optional = this.transactionHandler.commit();
                return optional.isPresent() ? this.cache.commit(optional.get()) : "Nothing to commit";
            case END:
                return this.transactionHandler.end();
            case SET:
                return this.transactionHandler.isActive() ?
                        this.transactionHandler.set(query.getKey(), query.getValue()) :
                        this.cache.set(query.getKey(), query.getValue());
            case GET:
                return this.transactionHandler.isActive() ?
                        this.transactionHandler.get(query.getKey()) :
                        this.cache.get(query.getKey());
            case DELETE:
                return this.transactionHandler.isActive() ?
                        this.transactionHandler.delete(query.getKey()) :
                        this.cache.delete(query.getKey());
            case COUNT:
                return this.transactionHandler.isActive() ?
                        this.transactionHandler.count() :
                        this.cache.count();
            case EVICT:
                //TODO evict cache to filesystem
            case CLEAR:
                //TODO clear the cache and remove from filesystem
        }
        return "null";
    }
}
