package com.omobio.springbase.controller.admin;

import com.omobio.springbase.common.response.ApiResponse;
import com.omobio.springbase.dto.auth.LoginRequestDTO;
import com.omobio.springbase.dto.auth.LoginResponseDTO;
import com.omobio.springbase.dto.auth.RefreshTokenRequestDTO;
import com.omobio.springbase.security.JwtFilter;
import com.omobio.springbase.service.AuthService;
import com.omobio.springbase.util.constants.Prefixes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("admin-auth")
@RequestMapping(Prefixes.ADMIN_PATH + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtFilter jwtFilter;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return new ApiResponse<>(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return new ApiResponse<>(authService.refresh(request), "Token refreshed successfully");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = jwtFilter.extractTokenFromHeader(request);
        if (token != null) {
            authService.logout(token);
        }
        return new ApiResponse<>("Logged out successfully");
    }
}
