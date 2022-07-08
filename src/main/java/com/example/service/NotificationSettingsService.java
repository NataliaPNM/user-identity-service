package com.example.service;

import com.example.dto.request.OperationConfirmEvent;
import com.example.dto.request.SetDefaultNotificationTypeRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.DefaultConfirmationTypeResponse;
import com.example.exception.DefaultConfirmationTypeLockedException;
import com.example.exception.IncorrectDefaultNotificationTypeException;
import com.example.exception.NotFoundException;
import com.example.exception.OperationNotAvailableException;
import com.example.mapper.ChangePasswordResponseDtoMapper;
import com.example.model.ConfirmationLock;
import com.example.model.Person;
import com.example.repository.PersonOperationRepository;
import com.example.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {
  private final PersonRepository personRepository;
  private final PersonOperationRepository operationRepository;
  private final PersonOperationService personOperationService;
  private final ChangePasswordResponseDtoMapper changePasswordResponseDtoMapper;

  public void checkPersonConfirmationFullLock(Person person) {

    if (person.getNotificationSettings().getConfirmationLock().equals(ConfirmationLock.FULL_LOCK)) {
      // на данном этпа пуш не реализован, поэтому эта проверка всегда будет возвращать
      // отрицательный результат, когда добавлю пуш, тогда и буду исправлять
      if (LocalDateTime.parse(person.getNotificationSettings().getEmailLockTime())
          .isAfter(LocalDateTime.now())) {
        throw new OperationNotAvailableException(
            getUnlockTimeInMs(LocalDateTime.parse(person.getNotificationSettings().getEmailLockTime())));
      }
      person.getNotificationSettings().setConfirmationLock(ConfirmationLock.EMAIL_LOCK);
      person.getNotificationSettings().setEmailLockTime("");
      person.getNotificationSettings().setEmailLock(false);
      personRepository.save(person);
    }
  }

  public String checkPersonConfirmationDefaultTypeLock(Person person) {
    if (person
        .getNotificationSettings()
        .getConfirmationLock()
        .equals(ConfirmationLock.EMAIL_LOCK)) {

      if (LocalDateTime.parse(person.getNotificationSettings().getEmailLockTime())
          .isAfter(LocalDateTime.now())) {
        return person.getNotificationSettings().getEmailLockTime();
      }
      person.getNotificationSettings().setConfirmationLock(ConfirmationLock.NONE);
      person.getNotificationSettings().setEmailLockTime("");
      person.getNotificationSettings().setEmailLock(false);
      personRepository.save(person);
    }
    return "";
  }

  public Person getPersonById(UUID personId) {
    return personRepository
        .findById(personId)
        .orElseThrow(() -> new NotFoundException("Not found person with this id"));
  }

  public DefaultConfirmationTypeResponse getPersonDefaultNotificationType(UUID personId) {
    return  DefaultConfirmationTypeResponse
            .builder()
            .defaultConfirmationType(getPersonById(personId).getNotificationSettings().getDefaultTypeOfConfirmation())
            .build();
  }

  public void checkNewDefaultConfirmationType(
      SetDefaultNotificationTypeRequest setDefaultNotificationTypeRequest, Person person) {
    if (setDefaultNotificationTypeRequest.getNewDefaultType().equals("email")
        || setDefaultNotificationTypeRequest.getNewDefaultType().equals("push")) {
      if (person
          .getNotificationSettings()
          .getDefaultTypeOfConfirmation()
          .equals(setDefaultNotificationTypeRequest.getNewDefaultType())) {
        throw new IncorrectDefaultNotificationTypeException(
            "This is already default confirmation type");
      }
      var locks = getPersonConfirmationLocks(person.getPersonId());

      var newTypeLock = locks.get(setDefaultNotificationTypeRequest.getNewDefaultType());

      if (!newTypeLock.equals("")
          && LocalDateTime.parse(newTypeLock).isAfter(LocalDateTime.now())) {
        throw new DefaultConfirmationTypeLockedException(getUnlockTimeInMs(LocalDateTime.parse(newTypeLock)));
      }
    } else {
      throw new IncorrectDefaultNotificationTypeException("Incorrect confirmation type");
    }
  }

  public String setPersonDefaultConfirmationType(
      SetDefaultNotificationTypeRequest setTypeRequestDto) {

    var operation =
        operationRepository
            .findByPersonOperationId(setTypeRequestDto.getOperationId())
            .orElseThrow(() -> new NotFoundException("Not found such operation"));

    var person = getPersonById(operation.getPerson().getPersonId());
    checkNewDefaultConfirmationType(setTypeRequestDto, person);
    checkPersonConfirmationFullLock(operation.getPerson());

    operation
        .getPerson()
        .getNotificationSettings()
        .setDefaultTypeOfConfirmation(setTypeRequestDto.getNewDefaultType());
    operation.setConfirmationType(setTypeRequestDto.getNewDefaultType());
    operationRepository.save(operation);
    personOperationService.sendOperationConfirmRequestEvent(
        operation.getPersonOperationId(),
        personRepository.save(operation.getPerson()),
        "notification-request");
    return "true";
  }
  public String getPersonContact(Person person){
     if (person.getNotificationSettings().getDefaultTypeOfConfirmation().equals("push")) {
       //когда будет реализован пуш то добавлю возврат токена
    }
     return person.getEmail();
  }

  public ChangePasswordResponseDto sendConfirmationCodeRequest(
      Person person, String operationType) {
    var operationId = personOperationService.createOperation(person, operationType);

    var lockTime = checkPersonConfirmationDefaultTypeLock(person);
    if (lockTime.equals("")) {
      personOperationService.sendOperationConfirmRequestEvent(
          operationId, person, "notification-request");

      return changePasswordResponseDtoMapper.toChangePasswordResponsePreconditionRequiredDto(
          HttpStatus.NOT_ACCEPTABLE,
          getPersonContact(person),
          operationId,
          "This operation requires confirmation by code");
    }
    return changePasswordResponseDtoMapper.toChangePasswordResponseConfirmationLockedDto(
        HttpStatus.LOCKED,
        getPersonContact(person),
        operationId,
        "Chose another confirmation type",
            getUnlockTimeInMs(LocalDateTime.parse(lockTime)));
  }

  public void updateNotificationTypeStatus(OperationConfirmEvent confirmDto) {
    var person = getPersonById(confirmDto.getPersonId());
    switch (confirmDto.getStatus()) {
      case "confirm" -> personOperationService.operationConfirmation(confirmDto);
      case "lock" -> {
        person
                .getNotificationSettings()
                .setConfirmationLock(ConfirmationLock.EMAIL_LOCK);
        person.getNotificationSettings().setEmailLockTime(confirmDto.getLockTime());
        personRepository.save(person);
      }
      case "fullLock" -> {
        person
                .getNotificationSettings()
                .setConfirmationLock(ConfirmationLock.FULL_LOCK);
        person.getNotificationSettings().setPushLockTime(confirmDto.getLockTime());
        personRepository.save(person);
      }
    }
  }

  public Map<String, String> getPersonConfirmationLocks(UUID personId) {
    Map<String, String> locks = new HashMap<>();
    var notificationSettings = getPersonById(personId).getNotificationSettings();
    locks.put("email", notificationSettings.getEmailLockTime());
    locks.put("push", notificationSettings.getPushLockTime());
    return locks;
  }

  @KafkaListener(topics = "user-identity", groupId = "user-identity")
  @Transactional
  public void listenOperationConfirmationResponse(
      ConsumerRecord<String, OperationConfirmEvent> consumerRecord) {

    var operationConfirm = consumerRecord.value();
    updateNotificationTypeStatus(operationConfirm);
  }
  public String getUnlockTimeInMs(LocalDateTime to){

    return String.valueOf(ChronoUnit.MILLIS.between(LocalDateTime.now(), to));
  }
}
