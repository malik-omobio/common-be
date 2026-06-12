package com.omobio.springbase.controller;

import com.omobio.springbase.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        return new ApiResponse<>(Map.of("status", "UP"), "Service is healthy");
    }
}
