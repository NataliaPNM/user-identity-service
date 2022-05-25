package com.example.mapper;

import com.example.dto.UserContactsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserContactsDtoMapper {

    UserContactsDto toUserContactsDto(Long id, String email, Long phone);
}
