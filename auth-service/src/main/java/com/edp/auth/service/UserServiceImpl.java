package com.edp.auth.service;

import com.edp.auth.data.entity.AppUser;
import com.edp.auth.data.repository.UserRepository;
import com.edp.auth.model.UserDto;
import com.edp.auth.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto dto) {
        AppUser user = userMapper.toEntity(dto);
        if (dto.getReportsToId() != null) {
            user.setReportsTo(userRepo.findById(dto.getReportsToId()).orElse(null));
        }
        return userMapper.toDto(userRepo.save(user));
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepo.findById(id).map(userMapper::toDto);
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

        AppUser updated = userMapper.toEntity(dto);
        updated.setId(id); // Ensure we're updating the correct record
        if (dto.getReportsToId() != null) {
            updated.setReportsTo(userRepo.findById(dto.getReportsToId()).orElse(null));
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepo.deleteById(id);
    }
}
