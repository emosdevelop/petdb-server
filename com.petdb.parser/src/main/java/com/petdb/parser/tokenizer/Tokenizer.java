package com.petdb.parser.tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Tokenizer {

    private static final List<Info> INFOS = new ArrayList<>();

    static {
        Tokenizer.INFOS.add(new Info(
                new Pattern[]{Pattern.compile("(?i)^(BEGIN|ROLLBACK|COMMIT|END|SET|GET|DELETE|COUNT|EVICT|CLEAR)\\z"),
                        Pattern.compile("(?i)^(BEGIN|ROLLBACK|COMMIT|END|SET|GET|DELETE|COUNT|EVICT|CLEAR)\\s")},
                Token.KEYWORD
        ));

        Tokenizer.INFOS.add(new Info(
                new Pattern[]{Pattern.compile("^(\".*\")"), Pattern.compile("^(\\S*)")},
                Token.KEY
        ));

        Tokenizer.INFOS.add(new Info(
                new Pattern[]{Pattern.compile("^(.*)")},
                Token.VALUE
        ));
    }

    public List<Token> tokenize(String string) {
        var tokens = new ArrayList<Token>();
        while (!string.isEmpty()) {
            boolean matchFirstIteration = false;
            for (Info info : Tokenizer.INFOS) {
                for (Pattern pattern : info.patterns) {
                    var matcher = pattern.matcher(string);
                    if (matcher.find()) {
                        matchFirstIteration = true;
                        String sequence = matcher.group().trim();
                        tokens.add(new Token(info.identifier, sequence));
                        string = matcher.replaceFirst("").trim();
                        break;
                    }
                }
                if (!matchFirstIteration) return Collections.emptyList();
            }
        }
        return tokens;
    }

    private static class Info {
        private final Pattern[] patterns;
        private final int identifier;

        private Info(Pattern[] patterns, int identifier) {
            this.patterns = patterns;
            this.identifier = identifier;
        }
    }
}
