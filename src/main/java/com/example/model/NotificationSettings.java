package com.example.model;

import com.example.model.enums.ConfirmationLock;
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

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NotificationSettings that = (NotificationSettings) o;
        return notificationSettingsId != null
                && Objects.equals(notificationSettingsId, that.notificationSettingsId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
