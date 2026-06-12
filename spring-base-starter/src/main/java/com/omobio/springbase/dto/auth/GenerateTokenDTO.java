package com.omobio.springbase.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GenerateTokenDTO {
    private UUID sub;
    private String email;
    private String role;
}
