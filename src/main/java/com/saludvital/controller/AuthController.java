package com.saludvital.controller;

import com.saludvital.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/inicio")
    public String inicio(Authentication authentication) {
        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        if (rol.equals("ADMIN")) {
            return "inicio_admin";   // templates/inicio_admin.html
        } else if (rol.equals("MEDICO")) {
            return "inicio_medico"; // templates/inicio_medico.html
        } else if (rol.equals("PACIENTE")) {
            return "inicio_paciente"; // templates/inicio_paciente.html
        } else {
            return "redirect:/login?error";
        }
    }
}
