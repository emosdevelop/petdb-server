module com.petdb.engine {
    opens com.petdb.transaction;
    requires com.petdb.parser;
    requires com.fasterxml.jackson.databind;
    exports com.petdb.engine to com.petdb.server;
}
