package com.edp.auth.service;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.data.repository.UserRepository;
import com.edp.auth.model.UserRegisterRequestDto;
import com.edp.auth.model.UserResponseDto;
import com.edp.auth.model.UserUpdateRequestDto;
import com.edp.auth.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserRegisterRequestDto request) {
        AppUser user = userMapper.toAppUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getReportsToId() != null) {
            user.setReportsTo(userRepo.findById(request.getReportsToId()).orElse(null));
        }
        return userMapper.toUserResponse(userRepo.save(user));
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepo.findById(id).map(userMapper::toUserResponse);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public void updateUser(Long id, UserUpdateRequestDto request) {
        AppUser existingUser = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateAppUserFromRequest(request, existingUser);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getReportsToId() != null) {
            existingUser.setReportsTo(userRepo.findById(request.getReportsToId()).orElse(null));
        } else if (existingUser.getReportsTo() != null) {
            existingUser.setReportsTo(null);
        }
        userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepo.deleteById(id);
    }
}