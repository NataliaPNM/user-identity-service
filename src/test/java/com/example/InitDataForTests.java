package com.example;

import com.example.dto.ChangePasswordRequest;
import com.example.model.Secure;
import com.example.model.User;
import com.example.model.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class InitDataForTests {

    public static User getUserForTests(){
        return User.builder()
                .id(3L)
                .name("Gektor")
                .surname("Gon")
                .patronymic("Isaev")
                .phone(88005553535L)
                .email("gggg@yandex.ru")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
    }

    public static Secure getSecureForTests(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return Secure.builder()
                .id(3L)
                .login("gektor")
                .password(passwordEncoder.encode("11111"))
                .user(getUserForTests())
                .build();
    }
    public static ChangePasswordRequest getChangePasswordRequestForTest(){
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("new");
        return changePasswordRequest;
    }
}
