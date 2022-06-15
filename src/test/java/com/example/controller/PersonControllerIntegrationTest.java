package com.example.controller;

import com.example.AuthorizationServiceApplication;
import com.example.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.example.Fixtures.*;
import static com.example.Fixtures.getUUID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthorizationServiceApplication.class)
@AutoConfigureMockMvc
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class PersonControllerIntegrationTest {

  @MockBean private PersonService personService;
  @Autowired private ObjectMapper mapper;
  @Autowired private MockMvc mockMvc;

  @Test
  public void sendResultStatusOkTest() throws Exception {
    when(personService.sendResult(
            getPersonContactsRequestDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")))
        .thenReturn(getPersonContactsDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "tttn@yandex.ru"));
    mockMvc
        .perform(
            post("/user/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        getPersonContactsRequestDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")))
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    mapper.writeValueAsString(
                        getPersonContactsDto(
                            "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "tttn@yandex.ru"))));
  }
}
