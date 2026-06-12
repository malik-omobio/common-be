package com.omobio.springbase.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private List<String> permissions;
}
