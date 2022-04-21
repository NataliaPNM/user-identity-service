package com.example.authorizationservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Entity;
import javax.persistence.Table;


import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
