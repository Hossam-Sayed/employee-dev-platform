package com.edp.auth.service;

import com.edp.auth.model.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);

    Optional<UserDto> getUserById(Long id);

    List<UserDto> getAllUsers();

    void updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
