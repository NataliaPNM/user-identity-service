package com.example.mapper;

import com.example.dto.response.PersonalDataResponseDto;
import com.example.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonalDataResponseDtoMapper {
  @Mapping(target = "name", source = "person.name")
  @Mapping(target = "surname", source = "person.surname")
  @Mapping(target = "patronymic", source = "person.patronymic")
  @Mapping(target = "dateOfBirth", source = "person.passport.dateOfBirth")
  @Mapping(target = "serialNumber", source = "person.passport.serialNumber")
  @Mapping(target = "departmentCode", source = "person.passport.departmentCode")
  @Mapping(target = "departmentIssued", source = "person.passport.departmentIssued")
  @Mapping(target = "phone", source = "person.phone")
  @Mapping(target = "email", source = "person.email")
  @Mapping(target = "residentialAddress", source = "person.residentialAddress")
  @Mapping(target = "registrationAddress", source = "person.passport.registrationAddress")
  @Mapping(target = "personId", source = "person.personId")
  PersonalDataResponseDto toPersonalDataResponseDto(Person person);
}
