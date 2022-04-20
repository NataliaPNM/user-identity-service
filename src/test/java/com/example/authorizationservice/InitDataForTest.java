package com.example.authorizationservice;

import com.example.authorizationservice.dto.LoginRequest;


public class InitDataForTest {

    public static LoginRequest getLoginRequest(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("postgres");
        loginRequest.setPassword("postgres");
        return loginRequest;
    }
}
