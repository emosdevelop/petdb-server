module com.petdb.parser {
    opens com.petdb.parser;
    exports com.petdb.parser;
    exports com.petdb.parser.query to com.petdb.server, com.petdb.engine;
    requires lombok;
}
