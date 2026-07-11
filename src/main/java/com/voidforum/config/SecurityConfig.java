package com.voidforum.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 1. Inyectamos el filtro que creaste recién
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Posts: el feed personal y toda escritura requieren sesión;
                        // lectura pública (listado, búsqueda, comentarios) queda abierta.
                        .requestMatchers(HttpMethod.GET, "/api/posts/feed").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        // Comments: lectura pública vía /api/posts/**, escritura requiere sesión.
                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()

                        // Users: acciones sobre la cuenta propia y toggles de follow requieren sesión;
                        // ver un perfil público (por id, followers, following) queda abierto.
                        .requestMatchers(HttpMethod.GET, "/api/users/me", "/api/users/me/following", "/api/users/saved").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/*/isfollowing").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/*/follow").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/follow").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/saved/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/saved/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                        // Votes: emitir voto y ver los propios votos requiere sesión;
                        // el conteo agregado es público. cleanup es una herramienta de reparación
                        // de datos — sin sistema de roles todavía, se restringe a "logueado" como
                        // mínimo indispensable (no debe quedar 100% público).
                        .requestMatchers(HttpMethod.GET, "/api/votes/*/count").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/votes/user").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/votes/**").authenticated()

                        .anyRequest().permitAll()
                )
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint))
                // 2. LE DECIMOS A SPRING QUE USE NUESTRO FILTRO JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}