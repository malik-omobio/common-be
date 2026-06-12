package com.omobio.springbase.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Default JWT security setup. Override by defining your own
 * {@link SecurityFilterChain} (or {@link CorsConfigurationSource}) bean —
 * the starter backs off via {@code @ConditionalOnMissingBean}.
 *
 * Add app-specific public endpoints with the
 * {@code app.security.excluded-paths} property (comma separated).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /** App-specific public paths, e.g. app.security.excluded-paths=/api/v1/web/employees/** */
    @Value("${app.security.excluded-paths:}")
    private String[] extraExcludedPaths;

    static final List<String> CORE_EXCLUDED_PATHS = List.of(
            "/api/v1/admin/auth/login",
            "/api/v1/admin/auth/refresh",
            "/api/v1/web/auth/login",
            "/api/v1/health/**"
    );

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<String> excluded = new ArrayList<>(CORE_EXCLUDED_PATHS);
        for (String path : extraExcludedPaths) {
            if (!path.isBlank()) {
                excluded.add(path.trim());
            }
        }
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(excluded.toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
