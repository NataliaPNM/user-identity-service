package com.example.repository;

import com.example.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
  Optional<Credentials> findByLogin(String login);

  Optional<Credentials> findByRefreshToken(String refreshToken);
}
