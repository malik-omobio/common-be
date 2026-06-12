package com.omobio.springbase.controller.admin;

import com.omobio.springbase.common.constants.CorePermissions;
import com.omobio.springbase.common.enums.UserStatus;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.common.response.ApiResponse;
import com.omobio.springbase.dto.user.CreateUserDTO;
import com.omobio.springbase.dto.user.FilterUserDTO;
import com.omobio.springbase.dto.user.ResponseUserDTO;
import com.omobio.springbase.dto.user.UpdateUserDTO;
import com.omobio.springbase.service.UserService;
import com.omobio.springbase.util.abst.Guard;
import com.omobio.springbase.util.constants.Prefixes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("admin-user")
@RequestMapping(Prefixes.ADMIN_PATH + "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Guard({CorePermissions.CREATE_USER})
    @PostMapping
    public ApiResponse<ResponseUserDTO> create(@Valid @RequestBody CreateUserDTO dto) {
        return new ApiResponse<>(userService.create(dto), "User created successfully");
    }

    @Guard({CorePermissions.VIEW_USER})
    @GetMapping
    public ApiPaginatedResponse<ResponseUserDTO> findAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID roleId,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage) {
        return userService.findAll(new FilterUserDTO(email, roleId, status, page, perPage));
    }

    @Guard({CorePermissions.VIEW_USER})
    @GetMapping("/{id}")
    public ApiResponse<?> findById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean withPermissions) {
        return new ApiResponse<>(userService.findById(id, withPermissions), "User fetched successfully");
    }

    @Guard({CorePermissions.UPDATE_USER})
    @PutMapping("/{id}")
    public ApiResponse<ResponseUserDTO> update(
            @PathVariable UUID id,
            @RequestBody UpdateUserDTO dto) {
        return new ApiResponse<>(userService.update(id, dto), "User updated successfully");
    }
}
