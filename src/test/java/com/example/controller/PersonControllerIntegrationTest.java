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
import static com.example.Fixtures.*;
import static com.example.Fixtures.getUUID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
  public void signInStatusOkTest() throws Exception {
    when(personService.sendResult(getPersonContactsRequestDto1())).thenReturn(getPersonContactsDto1());
    mockMvc
        .perform(
            get("/user/contact")
                .param("personId", getUUID().toString())
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(getPersonContactsDto1())));
  }
}
