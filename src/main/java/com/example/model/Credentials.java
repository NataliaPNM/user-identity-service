package com.example.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "credentials")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(
    name = "getConfirmationCode",
    query =
        "select c from Credentials c join fetch c.person join  c.person.notificationSettings where c.login =:login")
public class Credentials {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID credentialsId;

  private String refreshToken;
  private String login;
  private boolean isAccountVerified;
  private String temporaryPassword;
  private String password;

  private boolean lock;
  private String lockTime;

  @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JoinColumn(name = "person_id")
  private Person person;
}
