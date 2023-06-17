package com.example.repository;

import com.example.model.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<Passport, UUID> {

    @Query("select c from Passport c  where c.person.personId =:person")
    Optional<Passport> findByPersonId(UUID person);
}
