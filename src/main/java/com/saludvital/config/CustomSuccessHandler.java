package com.saludvital.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/inicio"; // valor por defecto

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();

            if (role.equals("ADMIN")) {
                redirectUrl = "/admin/inicio";
                break;
            } else if (role.equals("MEDICO")) {
                redirectUrl = "/medico/inicio";
                break;
            } else if (role.equals("PACIENTE")) {
                redirectUrl = "/paciente/inicio";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}