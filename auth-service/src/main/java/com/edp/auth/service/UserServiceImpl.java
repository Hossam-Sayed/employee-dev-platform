package com.edp.auth.service;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.data.repository.UserRepository;
import com.edp.auth.model.UserDto;
import com.edp.auth.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto dto) {
        AppUser user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getReportsToId() != null) {
            user.setReportsTo(userRepo.findById(dto.getReportsToId()).orElse(null));
        }
        return userMapper.toDto(userRepo.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepo.findById(id).map(userMapper::toDto).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void updateUser(Long id, UserDto dto) {
        AppUser existingUser = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateUserFromDto(dto, existingUser);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getReportsToId() != null) {
            existingUser.setReportsTo(userRepo.findById(dto.getReportsToId()).orElse(null));
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