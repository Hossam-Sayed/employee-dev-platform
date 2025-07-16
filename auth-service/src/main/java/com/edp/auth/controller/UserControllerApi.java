package com.edp.auth.controller;

import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequestMapping("/api/users")
public interface UserControllerApi {

    // TODO: Add Admin role check or remove according to desired behavior
    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, UriComponentsBuilder uriBuilder);

    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> getUser(@PathVariable Long id);

    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @PutMapping("/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}
