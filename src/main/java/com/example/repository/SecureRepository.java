package com.example.repository;

import com.example.model.Secure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecureRepository extends JpaRepository<Secure,Long> {
    Optional<Secure> findByLogin(String login);
    Optional<Secure> findByRefreshToken(String refreshToken);
}
