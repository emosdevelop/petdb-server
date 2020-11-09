module com.petdb.engine {
    opens com.petdb.transaction;
    requires com.petdb.parser;
    exports com.petdb.engine to com.petdb.server;
}
