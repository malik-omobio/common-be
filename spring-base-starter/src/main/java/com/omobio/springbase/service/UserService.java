package com.omobio.springbase.service;

import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.dto.user.CreateUserDTO;
import com.omobio.springbase.dto.user.FilterUserDTO;
import com.omobio.springbase.dto.user.ResponseUserDTO;
import com.omobio.springbase.dto.user.UpdateUserDTO;
import com.omobio.springbase.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);
    List<String> getPermissionNamesForUser(User user);
    ResponseUserDTO create(CreateUserDTO dto);
    ApiPaginatedResponse<ResponseUserDTO> findAll(FilterUserDTO filters);
    Object findById(UUID id, boolean withPermissions);
    ResponseUserDTO update(UUID id, UpdateUserDTO dto);
}
