package com.utn.UDEE.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtilsTest {

    public static LocalDateTime aLocalDateTimeSince(){
        return LocalDateTime.parse("2021-06-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime aLocalDateTimeUntil(){
        return LocalDateTime.parse("2021-06-23 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
