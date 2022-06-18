package com.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.transaction.spi.JoinStatus;
import org.hibernate.sql.JoinType;

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
    query = "select c from Credentials c join fetch c.person where c.login =:login")
public class Credentials {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID credentialsId;

  private String refreshToken;
  private String login;
  private String password;
  private boolean lock;
  private String lockTime;
  @OneToOne
  @JoinColumn(name = "person_id")
  private Person person;
}
