package com.omobio.springbase.service;

import com.omobio.springbase.dto.auth.LoginRequestDTO;
import com.omobio.springbase.dto.auth.LoginResponseDTO;
import com.omobio.springbase.dto.auth.RefreshTokenRequestDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
    LoginResponseDTO refresh(RefreshTokenRequestDTO request);
    void logout(String token);
}
