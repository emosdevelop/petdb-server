package com.petdb.parser.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Query {
    private final Keyword keyword;
    private final Key key;
    private final Value value;

    public boolean hasKeyword() {
        return this.keyword != null;
    }

    public boolean hasKey() {
        return this.key != null && !this.key.getData().isEmpty();
    }

    public boolean hasValue() {
        return this.value != null && !this.value.getData().isEmpty();
    }
}
