package com.example.service;

import com.example.dto.request.NotificationRequestEvent;
import com.example.dto.request.OperationConfirmEvent;
import com.example.model.Person;
import com.example.model.PersonOperation;
import com.example.repository.CredentialsRepository;
import com.example.repository.PersonOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonOperationService {

  private final CredentialsRepository credentialsRepository;
  private final PersonOperationRepository operationRepository;
  private final KafkaTemplate<String, NotificationRequestEvent> kafkaTemplate;

  public void updateOperationRepository(UUID personId) {
    operationRepository
        .findByPerson(personId)
        .ifPresent(
            o -> {
              if (LocalDateTime.parse(o.getOperationExpirationTime())
                  .isBefore(LocalDateTime.now())) {
                operationRepository.delete(o);
              }
            });
  }

  public UUID createOperation(Person person, String operationType) {
    updateOperationRepository(person.getPersonId());
    operationRepository
        .findByPersonId(
            person.getPersonId(),
            operationType,
            person.getNotificationSettings().getDefaultTypeOfConfirmation())
        .ifPresent(operationRepository::delete);

    return operationRepository
        .save(
            PersonOperation.builder()
                .person(person)
                .operationExpirationTime(LocalDateTime.now().plusHours(2L).toString())
                .operationType("changeCredentials")
                .confirmationType(person.getNotificationSettings().getDefaultTypeOfConfirmation())
                .build())
        .getPersonOperationId();
  }

  @Transactional(transactionManager = "kafkaTransactionManager")
  public void sendOperationConfirmRequestEvent(UUID operationId, Person person, String topicName) {
    String contact;
    if (person.getNotificationSettings().getDefaultTypeOfConfirmation().equals("email")) {
      contact = person.getEmail();
    } else {
      contact = "";
    }
    kafkaTemplate.send(
        topicName,
        NotificationRequestEvent.builder()
            .operationId(operationId)
            .type(person.getNotificationSettings().getDefaultTypeOfConfirmation())
            .contact(contact)
            .personId(person.getPersonId())
            .source("user-identity")
            .build());
  }

  public void operationConfirmation(OperationConfirmEvent confirmDto) {
    var operation =
        operationRepository.findByPersonOperationId(confirmDto.getOperationId()).orElseThrow();
    if (operation.getOperationType().equals("changeCredentials")) {
      var credentials =
          credentialsRepository.findByPersonId(confirmDto.getPersonId()).orElseThrow();
      String newPassword = credentials.getTemporaryPassword();
      credentials.setPassword(newPassword);
      credentials.setTemporaryPassword("");
      credentials.setAccountVerified(true);
      credentialsRepository.save(credentials);
      operationRepository.deleteById(confirmDto.getOperationId());
    }
  }
}
