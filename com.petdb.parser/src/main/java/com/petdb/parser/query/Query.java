package com.petdb.parser.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Query {
    private final Keyword keyword;
    private final String key;
    private final String value;

    public boolean hasKeyword() {
        return this.keyword != null;
    }

    public boolean hasKey() {
        return this.key != null && !this.key.isEmpty();
    }

    public boolean hasValue() {
        return this.value != null && !this.value.isEmpty();
    }
}
