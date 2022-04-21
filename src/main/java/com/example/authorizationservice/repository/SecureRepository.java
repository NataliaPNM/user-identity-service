package com.example.authorizationservice.repository;

import com.example.authorizationservice.model.Secure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecureRepository extends JpaRepository<Secure,Long> {
    Optional<Secure> findByLogin(String login);
}
