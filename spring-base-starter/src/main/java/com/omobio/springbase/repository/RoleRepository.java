package com.omobio.springbase.repository;

import com.omobio.springbase.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(String name);
}
