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
    private boolean isAccountVerified;
    private String temporaryPassword;
    private String password;

    private boolean lock;
    private String lockTime;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Credentials that = (Credentials) o;
        return credentialsId != null && Objects.equals(credentialsId, that.credentialsId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
