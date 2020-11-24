package com.petdb.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public final class EngineUtil {

    public final static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .toFormatter(Locale.ENGLISH);
    public final static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .toFormatter(Locale.ENGLISH);
    public final static String USER_DIR = System.getProperty("user.dir");

    private EngineUtil() {
        throw new AssertionError("Util class");
    }
}
