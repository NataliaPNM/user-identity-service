package com.example.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "notification_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {
  @Enumerated(EnumType.STRING)
  public ConfirmationLock confirmationLock;

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID notificationSettingsId;

  private String defaultTypeOfConfirmation;
  private Boolean emailLock;
  private String emailLockTime;
  private Boolean pushLock;
  private String pushLockTime;
}
