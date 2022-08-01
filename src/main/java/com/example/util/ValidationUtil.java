package com.example.util;

import com.example.model.Credentials;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Slf4j
public class ValidationUtil {

    public static boolean validateCredentials(Credentials credentials, Predicate<Credentials> check) {
        return check.test(credentials);
    }

    public static Predicate<Credentials> checkLockTimeIsNotEmpty() {
        return c -> !c.getLockTime().equals("");
    }

    public static Predicate<Credentials> checkLockTimeExpired(LocalDateTime now) {
        return c -> now.isAfter(LocalDateTime.parse(c.getLockTime()));
    }

    public static Predicate<Credentials> checkLockTime(LocalDateTime now) {
        return checkLockTimeIsNotEmpty().and(checkLockTimeExpired(now));
    }

}
