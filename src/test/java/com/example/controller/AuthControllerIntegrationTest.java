package com.example.controller;

import com.example.AuthorizationServiceApplication;
import com.example.security.userdetails.JwtUserDetails;
import com.example.util.JwtUtil;
import com.example.service.AuthenticationService;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.ArrayList;
import java.util.UUID;

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
class AuthControllerIntegrationTest {

  @Autowired private AuthController authController;
  @MockBean private AuthenticationService authenticationService;
  @MockBean private JwtUtil jwtUtil;
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
  void signInStatusOkTest() throws Exception {
    when(authenticationService.login(getLoginRequest("postgres", "postgres")))
        .thenReturn(getLoginResponseDto("eyJhbGciOiJI", "UCkErJNzC0rcAd", 0, 0, UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a")));
    mockMvc
        .perform(
            post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getLoginRequest("postgres", "postgres")))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    mapper.writeValueAsString(
                        getLoginResponseDto("eyJhbGciOiJI", "UCkErJNzC0rcAd", 0, 0,UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a")))));
  }

  @Test
  void refreshTokenStatusOkTest() throws Exception {
    when(authenticationService.refreshJwt(getRequestNewTokensDto("UCkErJNzC0rcAd")))
        .thenReturn(getLoginResponseDto("XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0,UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a")));
    mockMvc
        .perform(
            post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getRequestNewTokensDto("UCkErJNzC0rcAd")))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    mapper.writeValueAsString(
                        getLoginResponseDto("XOFsm1P2tSDo", "P3yzy8a91ixRrB", 0, 0,UUID.fromString("d6d83746-d862-4562-99d4-4ec5a664a59a")))));
  }

  @Test
  void validateTokenStatusOkTest() throws Exception {
    boolean expectedResult = true;
    when(jwtUtil.validateJwtToken("token")).thenReturn(expectedResult);
    mockMvc
        .perform(
            post("/auth/validateToken").header("Authorization","Bearer token")
                .param("token", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString("Bearer token"))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  void changePasswordStatusOkTest() throws Exception {

    given(authenticationService.changePassword(getChangePasswordRequest("newPassword", getUUID().toString())))
        .willReturn(getChangePasswordResult());
    mockMvc
        .perform(
            post("/auth/secure")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getChangePasswordRequest("newPassword", getUUID().toString())))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isPreconditionRequired()).andExpect(
                    content()
                            .json(
                                    mapper.writeValueAsString(
                                            getChangePasswordResult())));

  }

  private final HandlerMethodArgumentResolver putAuthenticationPrincipal =
      new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
          return parameter.getParameterType().isAssignableFrom(JwtUserDetails.class);
        }

        @Override
        public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
          ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
          return new JwtUserDetails(
              getUUID(), 89999999L, "test", "test", "login", "password", authorities);
        }
      };
}
