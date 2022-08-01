package com.example.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    //time in MILLIS until the lock expires
    public static String getUnlockTimeInMs(LocalDateTime lockExpirationTime) {
        return String.valueOf(ChronoUnit.MILLIS.between(LocalDateTime.now(), lockExpirationTime));
    }
}
