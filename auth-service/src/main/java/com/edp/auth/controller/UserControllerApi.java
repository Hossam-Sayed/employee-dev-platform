package com.edp.auth.controller;

import com.edp.auth.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequestMapping("/api/users")
public interface UserControllerApi {

    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody UserDto userDto, UriComponentsBuilder uriBuilder);

    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUser(@PathVariable Long id);

    @GetMapping
    ResponseEntity<List<UserDto>> getAllUsers();

    @PutMapping("/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserDto userDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}
