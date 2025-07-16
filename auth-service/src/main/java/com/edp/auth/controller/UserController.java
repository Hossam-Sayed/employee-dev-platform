package com.edp.auth.controller;

import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import com.edp.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> createUser(UserRegisterRequestDto userRegisterRequestDto, UriComponentsBuilder uriBuilder) {
        UserResponseDto user = userService.createUser(userRegisterRequestDto);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<UserResponseDto> getUser(Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<Void> updateUser(Long id, UserUpdateRequestDto userUpdateRequestDto) {
        userService.updateUser(id, userUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}