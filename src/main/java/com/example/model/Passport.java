package com.example.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "passport")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID passportId;

    private String dateOfBirth;
    private String serialNumber;
    private String departmentCode;
    private String departmentIssued;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address registrationAddress;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Passport passport = (Passport) o;
        return passportId != null && Objects.equals(passportId, passport.passportId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
