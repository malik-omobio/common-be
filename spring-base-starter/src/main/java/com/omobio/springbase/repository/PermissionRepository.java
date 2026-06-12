package com.omobio.springbase.repository;

import com.omobio.springbase.model.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);

    @Query(value = "SELECT * FROM role_permissions WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    Optional<Object> findByRoleIdAndPermissionId(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);

    @Query(value = "SELECT * FROM user_permissions WHERE user_id = :userId AND permission_id = :permissionId", nativeQuery = true)
    Optional<Object> findByUserIdAndPermissionId(@Param("userId") UUID userId, @Param("permissionId") UUID permissionId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM role_permissions WHERE role_id = :roleId", nativeQuery = true)
    void deleteAllByRoleId(@Param("roleId") UUID roleId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_permissions WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT p.* FROM permissions p INNER JOIN role_permissions rp ON p.id = rp.permission_id WHERE rp.role_id = :roleId", nativeQuery = true)
    List<PermissionEntity> findAllPermissionsByRoleId(@Param("roleId") UUID roleId);
}
