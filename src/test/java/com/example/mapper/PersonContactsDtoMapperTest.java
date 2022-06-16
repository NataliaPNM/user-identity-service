package com.example.mapper;

import com.example.dto.PersonContactsDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonContactsDtoMapperTest {

  private final PersonContactsDtoMapper personContactsDtoMapper =
      Mappers.getMapper(PersonContactsDtoMapper.class);

  @Test
  void mappingToLoanProgramDto() {
    var expectedPersonContactsDto =
        new PersonContactsDto(
            UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttnpnm@yandex.ru");

    var actualPersonContactsDto =
        personContactsDtoMapper.toPersonContactsDto(
            UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttnpnm@yandex.ru");

    assertEquals(expectedPersonContactsDto, actualPersonContactsDto);
  }
}
