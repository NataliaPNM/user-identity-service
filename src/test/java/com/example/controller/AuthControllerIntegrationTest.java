package com.example.controller;

import com.example.AuthorizationServiceApplication;
import com.example.controller.AuthController;
import com.example.security.JwtPerson;
import com.example.security.JwtUtils;
import com.example.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.ArrayList;

import static com.example.Fixtures.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = AuthorizationServiceApplication.class)
@AutoConfigureMockMvc
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class AuthControllerIntegrationTest {

  @Autowired private AuthController authController;
  @MockBean private AuthService authService;
  @MockBean private JwtUtils jwtUtils;
  @Autowired private ObjectMapper mapper;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  private void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(authController)
            .setCustomArgumentResolvers(putAuthenticationPrincipal)
            .build();
  }

  @Test
  public void signInStatusOkTest() throws Exception {
    when(authService.login(getLoginRequest1())).thenReturn(getLoginResponseDto());
    mockMvc
        .perform(
            post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getLoginRequest1()))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(getLoginResponseDto())));
  }

  @Test
  public void refreshTokenStatusOkTest() throws Exception {
    when(authService.refreshJwt(getRequestNewTokensDto())).thenReturn(getLoginResponseDto2());
    mockMvc
        .perform(
            post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getRequestNewTokensDto()))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(getLoginResponseDto2())));
  }

//  @Test
//  public void validateTokenStatusOkTest() throws Exception {
//    boolean expectedResult = true;
//    when(jwtUtils.validateJwtToken(getToken1())).thenReturn(expectedResult);
//    mockMvc
//        .perform(
//            post("/auth/validateToken")
//                .param("token", getToken1())
//                    .headers()
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(getToken1()))
//                .characterEncoding("utf-8")
//                .accept(MediaType.APPLICATION_JSON))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(content().string("true"));
//  }

  @Test
  public void changePasswordStatusOkTest() throws Exception {

    given(authService.changePassword(getChangePasswordRequest(), getUUID()))
        .willReturn(getChangePasswordResult());
    mockMvc
        .perform(
            post("/auth/secure")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getChangePasswordRequest()))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(getChangePasswordResult()));
  }

  private final HandlerMethodArgumentResolver putAuthenticationPrincipal =
      new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
          return parameter.getParameterType().isAssignableFrom(JwtPerson.class);
        }

        @Override
        public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
          ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
          return new JwtPerson(getUUID(), 89999999L, "test", "test", "login", "password", authorities);
        }
      };
}
