package com.example.model;

import com.example.model.enums.PersonRole;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
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
  @Column(name = "person_id")
  private UUID personId;

  private Long phone;

  private String name;

  private String surname;
  private String patronymic;

  private String email;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "address_id")
  private Address residentialAddress;

  @OneToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "passport_id")
  private Passport passport;

  @Enumerated(EnumType.STRING)
  private PersonRole role;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Person person = (Person) o;
    return personId != null && Objects.equals(personId, person.personId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
