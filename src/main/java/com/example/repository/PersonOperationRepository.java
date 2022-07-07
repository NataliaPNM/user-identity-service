package com.example.repository;

import com.example.model.PersonOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonOperationRepository extends JpaRepository<PersonOperation, UUID> {
  Optional<PersonOperation> findByPersonOperationId(UUID operationId);

  @Query(
      "select o from PersonOperation o where o.person.personId =:personId and o.operationType=:operationType and o.confirmationType=:codeType")
  Optional<PersonOperation> findByPersonId(UUID personId, String operationType, String codeType);

  @Query("select o from PersonOperation o where o.person.personId =:personId")
  List<Optional<PersonOperation>> findByPerson(UUID personId);

  @Query(
      "select o from PersonOperation o where o.person.personId =:personId and o.confirmationType=:confirmationType")
  Optional<PersonOperation> findByPersonAndConfirmationType(UUID personId, String confirmationType);
}
