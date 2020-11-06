package com.petdb.parser.tokenizer;

import com.petdb.parser.query.Keyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Tokenizer {

    private static final List<Info> INFOS = new ArrayList<>();

    static {
        var builder = new StringBuilder();
        for (Keyword keyword : Keyword.values()) {
            builder.append("|");
            builder.append(keyword);
        }
        builder.deleteCharAt(0);

        Tokenizer.INFOS.add(new Info(
                new Pattern[]{Pattern.compile("(?i)^(" + builder.toString() + ")\\z"),
                        Pattern.compile("(?i)^(" + builder.toString() + ")\\s")},
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

    public static List<Token> tokenize(String string) {
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
