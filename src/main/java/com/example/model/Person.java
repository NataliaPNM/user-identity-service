package com.example.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "person")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID personId;

  private Long phone;

  private String name;

  private String surname;
  private String patronymic;

  private String email;


  @OneToOne(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
  @JoinColumn(name = "notification_settings_id")
  private NotificationSettings notificationSettings;

  @Enumerated(EnumType.STRING)
  private PersonRole role;
}
