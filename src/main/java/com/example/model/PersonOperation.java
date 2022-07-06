package com.example.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "person_operation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonOperation {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID personOperationId;

  @ManyToOne
  @JoinColumn(name = "person_id")
  private Person person;

  private String operationType;
  private String confirmationType;
  private String operationExpirationTime;
}
