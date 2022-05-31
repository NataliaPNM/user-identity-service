package com.example.service;

import com.example.dto.PersonContactsDto;
import com.example.mapper.PersonContactsDtoMapper;
import com.example.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {
  private final PersonRepository personRepository;
  private final PersonContactsDtoMapper personContactsDtoMapper;

  public PersonContactsDto sendResult(UUID personId) {
    var person = personRepository.getById(personId);
    return personContactsDtoMapper.toPersonContactsDto(
        personId, person.getEmail(), person.getPhone());
  }
}
