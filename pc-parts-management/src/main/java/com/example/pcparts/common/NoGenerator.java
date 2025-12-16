package com.example.pcparts.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class NoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private NoGenerator() {
    }

    public static String generate(String prefix) {
        String time = LocalDateTime.now().format(FORMATTER);
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return prefix + time + rand;
    }
}
