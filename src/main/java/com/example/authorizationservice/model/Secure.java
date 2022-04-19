package com.example.authorizationservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;


@Data
@Entity
@Table(name = "SecureData")
public class Secure {

        @Id
        @EqualsAndHashCode.Exclude
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "secure_generator")
        @SequenceGenerator(allocationSize = 1, name = "secure_generator")
        private Long id;


        private String login;

        private String password;

        @OneToOne
        @JoinColumn(name = "user_id")
        private User user;
}
