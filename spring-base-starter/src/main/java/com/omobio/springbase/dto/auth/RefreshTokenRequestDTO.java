package com.omobio.springbase.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank
    private String refreshToken;
}
