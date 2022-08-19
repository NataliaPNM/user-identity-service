package com.example.repository;

import com.example.model.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {

  @Query("select n from NotificationSettings n  where n.person.personId =:person")
  Optional<NotificationSettings> findByPerson(UUID person);
}
