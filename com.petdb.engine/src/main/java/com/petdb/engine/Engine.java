package com.petdb.engine;


import com.petdb.cache.Cache;
import com.petdb.filehandler.Extension;
import com.petdb.filehandler.FileHandler;
import com.petdb.parser.query.Keyword;
import com.petdb.parser.query.Query;
import com.petdb.transaction.TransactionHandler;

import java.util.concurrent.TimeUnit;

import static com.petdb.filehandler.Extension.JSON;
import static com.petdb.filehandler.Extension.XML;

public final class Engine {

    private final TransactionHandler transactionHandler = new TransactionHandler();
    private final Cache cache = new Cache();
    private final FileHandler fileHandler = new FileHandler();

    public String execute(Query query, int bufferCapacity) {
        switch (query.getKeyword()) {
            case BEGIN:
                return this.transactionHandler.begin();
            case ROLLBACK:
                return this.transactionHandler.rollback();
            case COMMIT:
                var optional = this.transactionHandler.commit();
                return optional.isPresent() ?
                        this.cache.commit(optional.get()) : "Nothing to " + Keyword.COMMIT;
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
                if (this.transactionHandler.isActive()) {
                    return String.format
                            (Keyword.COUNT + ": Transaction = %s", this.transactionHandler.count());
                }
                return String.valueOf(this.cache.count());
            case DUMP:
                if (this.transactionHandler.isActive()) return "Pending transaction[s]";
                var data = query.getKey().getData();
                if (!(data.equalsIgnoreCase(JSON.getValue()) ||
                        data.equalsIgnoreCase(XML.getValue()))) {
                    return "XML or JSON";
                }
                return this.dump(Extension.valueOf(data.toUpperCase()));
            case FLUSH:
                if (this.transactionHandler.isActive()) return "Pending transaction[s]";
                return this.flush();
            default:
                return "null";
        }
    }

    private String dump(Extension extension) {
        long start = System.nanoTime();
        this.fileHandler.dump(extension);
        long end = System.nanoTime();
        long elapsedTime = end - start;
        long seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        return String.format(Keyword.DUMP + ": It took %dSECONDS", seconds);
    }

    private String flush() {
        long start = System.nanoTime();
        this.cache.flush();
        long end = System.nanoTime();
        long elapsedTime = end - start;
        long seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        return String.format(Keyword.FLUSH + ": It took %dSECONDS", seconds);
    }
}
