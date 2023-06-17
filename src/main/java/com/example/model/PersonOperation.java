package com.example.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersonOperation operation = (PersonOperation) o;
        return personOperationId != null
                && Objects.equals(personOperationId, operation.personOperationId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
