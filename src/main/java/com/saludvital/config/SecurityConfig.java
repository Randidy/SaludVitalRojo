package com.saludvital.config;

import com.saludvital.service.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final CustomSuccessHandler customSuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF (útil en dev, en prod puedes habilitar)
            .authorizeHttpRequests(auth -> auth
                // rutas públicas
                .requestMatchers("/login", "/registro/**", "/css/**", "/js/**", "/images/**").permitAll()

                // rutas solo ADMIN
                .requestMatchers("/admin/**", "/alergias/**", "/medicamentos/**", "/usuarios/**").hasAuthority("ADMIN")

                // rutas para MEDICO y ADMIN
                .requestMatchers("/medicos/**", "/pacientes/**").hasAnyAuthority("ADMIN", "MEDICO")

                // rutas para PACIENTE y MEDICO
                .requestMatchers("/historial/**", "/recetas/**").hasAnyAuthority("MEDICO", "PACIENTE")

                // rutas exclusivas de PACIENTE
                .requestMatchers("/paciente/**").hasAuthority("PACIENTE")

                // rutas de citas accesibles a todos los roles
                .requestMatchers("/citas/**").hasAnyAuthority("ADMIN", "MEDICO", "PACIENTE")

                // cualquier otra ruta debe estar autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // tu plantilla login.html
                .successHandler(customSuccessHandler) // redirección por rol
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // opcional, por defecto es /logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // para encriptar contraseñas
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
