package com.edp.auth.mapper;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "birthdate", target = "birthdate"),
            @Mapping(source = "phoneNumber", target = "phoneNumber"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "admin", target = "admin"),
            @Mapping(source = "reportsTo.id", target = "reportsToId")
    })
    UserDto toDto(AppUser user);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "birthdate", target = "birthdate"),
            @Mapping(source = "phoneNumber", target = "phoneNumber"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "admin", target = "admin"),
            @Mapping(target = "reportsTo", ignore = true)
    })
    AppUser toEntity(UserDto dto);
}
