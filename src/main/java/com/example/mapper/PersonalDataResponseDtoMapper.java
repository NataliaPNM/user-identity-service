package com.example.mapper;

import com.example.dto.response.PersonalDataResponseDto;
import com.example.model.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonalDataResponseDtoMapper {

    @Mapping(target = "name", source = "passport.person.name")
    @Mapping(target = "surname", source = "passport.person.surname")
    @Mapping(target = "patronymic", source = "passport.person.patronymic")
    @Mapping(target = "dateOfBirth", source = "passport.dateOfBirth")
    @Mapping(target = "serialNumber", source = "passport.serialNumber")
    @Mapping(target = "departmentCode", source = "passport.departmentCode")
    @Mapping(target = "departmentIssued", source = "passport.departmentIssued")
    @Mapping(target = "phone", source = "passport.person.phone")
    @Mapping(target = "email", source = "passport.person.email")
    @Mapping(target = "residentialAddress", source = "passport.person.residentialAddress")
    @Mapping(target = "registrationAddress", source = "passport.registrationAddress")
    @Mapping(target = "personId", source = "passport.person.personId")
    PersonalDataResponseDto toPersonalDataResponseDto(Passport passport);
}
