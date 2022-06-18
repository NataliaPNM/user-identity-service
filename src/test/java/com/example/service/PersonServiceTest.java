package com.example.service;

import com.example.dto.PersonContactsDto;
import com.example.mapper.PersonContactsDtoMapper;
import com.example.repository.PersonRepository;
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
    PersonContactsDto expectedResult =
        getPersonContactsDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "tttn@yandex.ru");
    when(personRepository.getById(getUUID())).thenReturn(getPersonWithoutData());
    when(personContactsDtoMapper.toPersonContactsDto(getUUID(), getPersonWithoutData().getEmail()))
        .thenReturn(getPersonContactsDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", "tttn@yandex.ru"));

    PersonContactsDto actualResult =
        personService.sendResult(
            getPersonContactsRequestDto("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
    assertEquals(expectedResult, actualResult);
  }
}
