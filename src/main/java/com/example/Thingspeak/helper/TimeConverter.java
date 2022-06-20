package com.example.Thingspeak.helper;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class TimeConverter {
    public Instant convertZuluToInstant(String pattern) {
        return Instant.parse(pattern);
    }

    public LocalDateTime convertInstantToLDT(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public Instant convertLDTToInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant((ZoneOffset) ZoneId.systemDefault());
    }
}
