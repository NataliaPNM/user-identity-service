package com.example.repository;

import com.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Cacheable;
import java.util.Optional;
import java.util.UUID;

@Repository
@Cacheable
public interface PersonRepository extends JpaRepository<Person, UUID> {

  Optional<Person> findByEmail(String email);
}
