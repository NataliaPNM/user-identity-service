package com.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Data
@Entity
@Table(name = "credentials")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID credentialsId;

  private String refreshToken;
  private String login;
  private String password;

  @OneToOne
  @JoinColumn(name = "person_id")
  private Person person;
}
