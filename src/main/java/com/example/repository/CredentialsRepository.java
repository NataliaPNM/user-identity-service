package com.example.repository;

import com.example.model.Credentials;
import com.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
  @Query("select c from Credentials c join fetch c.person where c.login =:login")
  Optional<Credentials> findByLogin(String login);

  @Query("select c from Credentials c  where c.person.personId =:person")
  Optional<Credentials> findByPersonId(UUID person);

  Optional<Credentials> findByRefreshToken(String refreshToken);


}
