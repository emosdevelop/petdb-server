package com.petdb.parser;


import com.petdb.parser.query.Key;
import com.petdb.parser.query.Query;
import com.petdb.parser.query.Value;
import com.petdb.parser.tokenizer.Token;
import com.petdb.parser.tokenizer.Tokenizer;

import java.util.List;
import java.util.Optional;

import static com.petdb.parser.query.Keyword.valueOf;

public final class Parser {

    public Optional<Query> parse(String string) {
        if (string == null || string.isEmpty() || string.isBlank()) {
            return Optional.empty();
        }
        var tokens = Tokenizer.tokenize(string);
        if (tokens.isEmpty()) {
            return Optional.empty();
        }
        var query = this.build(tokens);
        var isValid = this.validate(query);
        return isValid ? Optional.of(query) : Optional.empty();
    }

    private Query build(List<Token> tokens) {
        var queryBuilder = Query.builder();
        for (Token token : tokens) {
            switch (token.getIdentifier()) {
                case Token.KEYWORD:
                    queryBuilder.keyword(valueOf(token.getSequence().toUpperCase()));
                    break;
                case Token.KEY:
                    queryBuilder.key(new Key(token.getSequence()));
                    break;
                case Token.VALUE:
                    queryBuilder.value(new Value(token.getSequence()));
                    break;
            }
        }
        return queryBuilder.build();
    }

    private boolean validate(Query query) {
        switch (query.getKeyword()) {
            case COUNT:
            case FLUSH:
            case BEGIN:
            case ROLLBACK:
            case COMMIT:
            case END:
                return !query.hasKey();
            case SET:
                return query.hasKey() && query.hasValue();
            case DELETE:
            case GET:
            case DUMP:
                return query.hasKey() && !query.hasValue();
            default:
                return false;
        }
    }
}
