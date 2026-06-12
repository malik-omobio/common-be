package com.omobio.springbase.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUserDTO {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private UUID roleId;
}
