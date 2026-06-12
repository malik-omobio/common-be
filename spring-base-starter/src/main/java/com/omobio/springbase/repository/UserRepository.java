package com.omobio.springbase.repository;

import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findAllByRoleId(UUID roleId);
}
