package com.example.mapper;

import com.example.dto.PersonContactsDto;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PersonContactsDtoMapper {
  PersonContactsDto toPersonContactsDto(UUID id, String email);
}
