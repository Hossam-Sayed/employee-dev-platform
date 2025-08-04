package com.edp.auth.mapper;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "birthdate", target = "birthdate"),
            @Mapping(source = "phoneNumber", target = "phoneNumber"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "admin", target = "admin"),
            @Mapping(source = "reportsTo.id", target = "reportsToId")
    })
    UserResponseDto toUserResponse(AppUser user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
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
    AppUser toAppUser(UserRegisterRequestDto request);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(target = "password", ignore = true),
            @Mapping(source = "birthdate", target = "birthdate"),
            @Mapping(source = "phoneNumber", target = "phoneNumber"),
            @Mapping(source = "department", target = "department"),
            @Mapping(source = "position", target = "position"),
            @Mapping(source = "admin", target = "admin"),
            @Mapping(target = "reportsTo", ignore = true),
    })
    void updateAppUserFromRequest(UserUpdateRequestDto request, @MappingTarget AppUser entity);

    List<UserResponseDto> toUserResponseDtoList(List<AppUser> users);

}