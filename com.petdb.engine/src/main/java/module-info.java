module com.petdb.engine {
    requires com.petdb.parser;
    exports com.petdb.engine to com.petdb.server;
}