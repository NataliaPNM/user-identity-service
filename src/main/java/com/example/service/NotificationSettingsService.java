package com.example.service;

import com.example.dto.request.OperationConfirmEvent;
import com.example.dto.request.SetDefaultNotificationTypeRequest;
import com.example.dto.response.ChangePasswordResponseDto;
import com.example.dto.response.DefaultConfirmationTypeResponse;
import com.example.dto.response.SetDefaultConfirmationTypeResponse;
import com.example.exception.DefaultConfirmationTypeLockedException;
import com.example.exception.IncorrectDefaultNotificationTypeException;
import com.example.exception.NotFoundException;
import com.example.exception.OperationNotAvailableException;
import com.example.mapper.ChangePasswordResponseDtoMapper;
import com.example.model.enums.ConfirmationLock;
import com.example.model.NotificationSettings;
import com.example.model.Person;
import com.example.repository.NotificationSettingsRepository;
import com.example.repository.PersonOperationRepository;
import com.example.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.util.TimeUtil.getUnlockTimeInMs;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {
  private final PersonRepository personRepository;
  private final PersonOperationRepository operationRepository;
  private final PersonOperationService personOperationService;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final ChangePasswordResponseDtoMapper changePasswordResponseDtoMapper;
 public NotificationSettings getNotificationSettings(UUID personId){
   return notificationSettingsRepository.findByPerson(personId).orElseThrow();
 }
  public void checkPersonConfirmationFullLock(Person person) {
    var notificationSettings = getNotificationSettings(person.getPersonId());

    if (notificationSettings.getConfirmationLock().equals(ConfirmationLock.FULL_LOCK)) {
      // на данном этпа пуш не реализован, поэтому эта проверка всегда будет возвращать
      // отрицательный результат, когда добавлю пуш, тогда и буду исправлять
      if (LocalDateTime.parse(notificationSettings.getEmailLockTime())
          .isAfter(LocalDateTime.now())) {
        throw new OperationNotAvailableException(
                getUnlockTimeInMs(LocalDateTime.parse(notificationSettings.getEmailLockTime())));
      }
      notificationSettings.setConfirmationLock(ConfirmationLock.EMAIL_LOCK);
      notificationSettings.setEmailLockTime("");
      notificationSettings.setEmailLock(false);
      notificationSettingsRepository.save(notificationSettings);
    }
  }

  public String checkPersonConfirmationDefaultTypeLock(Person person) {
    var notificationSettings = getNotificationSettings(person.getPersonId());
    if (notificationSettings
        .getConfirmationLock()
        .equals(ConfirmationLock.EMAIL_LOCK)) {

      if (LocalDateTime.parse(notificationSettings.getEmailLockTime())
          .isAfter(LocalDateTime.now())) {
        return notificationSettings.getEmailLockTime();
      }
      notificationSettings.setConfirmationLock(ConfirmationLock.NONE);
      notificationSettings.setEmailLockTime("");
      notificationSettings.setEmailLock(false);
      notificationSettingsRepository.save(notificationSettings);
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
            .defaultConfirmationType(notificationSettingsRepository.findByPerson(personId).orElseThrow(() -> new NotFoundException("Not found person with this id")).getDefaultTypeOfConfirmation())
            .build();
  }

  public void checkNewDefaultConfirmationType(
      SetDefaultNotificationTypeRequest setDefaultNotificationTypeRequest, Person person) {

    if (setDefaultNotificationTypeRequest.getNewDefaultType().equals("email")
        || setDefaultNotificationTypeRequest.getNewDefaultType().equals("push")) {
      if (getNotificationSettings(person.getPersonId())
          .getDefaultTypeOfConfirmation()
          .equals(setDefaultNotificationTypeRequest.getNewDefaultType())) {
        throw new IncorrectDefaultNotificationTypeException("This is already default confirmation type");
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

  public SetDefaultConfirmationTypeResponse setPersonDefaultConfirmationType(
      SetDefaultNotificationTypeRequest setTypeRequestDto) {
    if(!setTypeRequestDto.getNewDefaultType().equals("email") && !setTypeRequestDto.getNewDefaultType().equals("push")){
      throw new IncorrectDefaultNotificationTypeException("Incorrect confirmation type");
    }
    var operation =
        operationRepository
            .findByPersonOperationId(setTypeRequestDto.getOperationId())
            .orElseThrow(() -> new NotFoundException("Not found such operation"));

    var person = getPersonById(operation.getPerson().getPersonId());
    checkNewDefaultConfirmationType(setTypeRequestDto, person);
    checkPersonConfirmationFullLock(operation.getPerson());
      var notificationSettings = getNotificationSettings(person.getPersonId());
    notificationSettings
        .setDefaultTypeOfConfirmation(setTypeRequestDto.getNewDefaultType());
    operation.setConfirmationType(setTypeRequestDto.getNewDefaultType());
    operationRepository.save(operation);
    personOperationService.sendOperationConfirmRequestEvent(
        operation.getPersonOperationId(),
        notificationSettingsRepository.save(notificationSettings).getPerson(),
        "notification-request");
    if(setTypeRequestDto.getNewDefaultType().equals("email")){
      return SetDefaultConfirmationTypeResponse.builder().personContact(operation.getPerson().getEmail()).build();
    }
    return SetDefaultConfirmationTypeResponse.builder().personContact("deviceToken?").build();
  }
  public String getPersonContact(Person person){
      var notificationSettings = getNotificationSettings(person.getPersonId());
     if (notificationSettings.getDefaultTypeOfConfirmation().equals("push")) {
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
    var notificationSettings = getNotificationSettings(confirmDto.getPersonId());
    switch (confirmDto.getStatus()) {
      case "confirm" -> personOperationService.operationConfirmation(confirmDto);
      case "lock" -> {
        notificationSettings
                .setConfirmationLock(ConfirmationLock.EMAIL_LOCK);
        notificationSettings.setEmailLockTime(confirmDto.getLockTime());
        notificationSettingsRepository.save(notificationSettings);
      }
      case "fullLock" -> {
        notificationSettings
                .setConfirmationLock(ConfirmationLock.FULL_LOCK);
       notificationSettings.setPushLockTime(confirmDto.getLockTime());
          notificationSettingsRepository.save(notificationSettings);
      }
    }
  }

  public Map<String, String> getPersonConfirmationLocks(UUID personId) {
    Map<String, String> locks = new HashMap<>();

    var notificationSettings = getNotificationSettings(personId);
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

}
