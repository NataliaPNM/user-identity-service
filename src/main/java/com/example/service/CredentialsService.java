package com.example.service;

import com.example.exception.NotFoundException;
import com.example.model.Credentials;
import com.example.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CredentialsService {

    private final CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials findByLogin(String login) throws NotFoundException {
        return credentialsRepository
                .findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Credential Service can't find a user by it's login in the database");
                    throw new NotFoundException("User can not be found. Login: " + login);
                });
    }

    @Transactional
    public Credentials findByPersonId(UUID personId) throws NotFoundException {
        return credentialsRepository.
                findByPersonId(personId)
                .orElseThrow(() -> {
                    log.error("Credential Service can't find a user by it's id in the database");
                    throw new NotFoundException("User with id can not be found. Id: " + personId);
                });
    }

    @Transactional
    public Credentials findByRefreshToken(String refreshToken) throws NotFoundException {
        return credentialsRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("Credential Service can't find a user by it's refresh token in the database");
                    throw new NotFoundException("User with refresh token can not be found. RefreshToken: " + refreshToken);
                });
    }

    @Transactional
    public Credentials unlockAfterLockTimeExpiration(Credentials credentials) {
        log.info("Unlocking credentials and removing lock time");
        credentials.setLock(false);
        credentials.setLockTime("");
        return credentialsRepository
                .save(credentials);
    }

    @Transactional
    public Credentials lockAfterMaximumAttemptsExceeded(Credentials credentials) {
        // TODO: 21.07.2022 Move all Time calculation code into an TimeUtil class
        log.info("Locking credentials after maximum attempts count reached");

        credentials.setLock(true);
        if (credentials.isAccountVerified()) {
            credentials.setLockTime(LocalDateTime.now().plusMinutes(60L).toString());
        } else {
            credentials.setLockTime(LocalDateTime.now().plusHours(24L).toString());
        }
        return credentialsRepository
                .save(credentials);
    }

    @Transactional
    public Credentials setRefreshToken(Credentials credentals, String refreshToken) {
        credentals.setRefreshToken(refreshToken);
        return credentialsRepository.save(credentals);
    }

    @Transactional
    public Credentials setTemporatyPassword(Credentials credentials, String temporaryPassword) {
        credentials.setTemporaryPassword(temporaryPassword);
        return credentialsRepository.save(credentials);
    }

    @Transactional
    public Credentials save(Credentials credentials) {
        log.info("Saving Credentials into the Database");
        return credentialsRepository
                .save(credentials);
    }
}
