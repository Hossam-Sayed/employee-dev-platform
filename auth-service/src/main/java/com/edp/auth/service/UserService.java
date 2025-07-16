package com.edp.auth.service;

import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserUpdateRequestDto;
import com.edp.auth.model.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDto createUser(UserRegisterRequestDto request);

    Optional<UserResponseDto> getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    void updateUser(Long id, UserUpdateRequestDto request);

    void deleteUser(Long id);
}