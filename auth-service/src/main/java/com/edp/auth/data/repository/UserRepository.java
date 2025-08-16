package com.edp.auth.data.repository;

import com.edp.auth.data.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByAdmin(boolean admin);

    List<AppUser> findByReportsTo_Id(Long managerId);

    List<AppUser> findAllByIdIn(List<Long> ids);
}
