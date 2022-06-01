package com.example.service;

import com.example.dto.PersonContactsDto;
import com.example.mapper.PersonContactsDtoMapper;
import com.example.repository.PersonRepository;
import com.example.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.example.Fixtures.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class PersonServiceTest {

  @InjectMocks private PersonService personService;
  @Mock private PersonRepository personRepository;
  @Mock private PersonContactsDtoMapper personContactsDtoMapper;

  @Test
  void sendResultMethodReturnPersonContactsDtoTest() {
    PersonContactsDto expectedResult = getPersonContactsDto1();
    when(personRepository.getById(getUUID())).thenReturn(getPerson());
    when(personContactsDtoMapper.toPersonContactsDto(
            getUUID(), getPerson().getEmail(), getPerson().getPhone()))
        .thenReturn(getPersonContactsDto1());

    PersonContactsDto actualResult = personService.sendResult(getPersonContactsRequestDto1());
    assertEquals(expectedResult, actualResult);
  }
}
