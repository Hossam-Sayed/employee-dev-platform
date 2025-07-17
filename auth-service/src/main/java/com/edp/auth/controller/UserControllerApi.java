package com.edp.auth.controller;

import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequestMapping("/api/users")
public interface UserControllerApi {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, UriComponentsBuilder uriBuilder);

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.edp.auth.data.entity.AppUser).cast(authentication.principal).id")
    ResponseEntity<UserResponseDto> getUser(@PathVariable Long id);

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.edp.auth.data.entity.AppUser).cast(authentication.principal).id")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}
