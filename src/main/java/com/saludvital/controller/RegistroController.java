package com.saludvital.controller;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.model.Paciente;
import com.saludvital.model.Usuario;
import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.UsuarioRepository;
import com.saludvital.service.AlergiaService;
import com.saludvital.service.PacienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    @Autowired
    private final PacienteController pacienteController;
    private final AlergiaService alergiaService;

    @GetMapping("/registro/paciente")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("pacienteDTO", new PacienteDTO());
        model.addAttribute("alergiasDisponibles", alergiaService.listarTodas()); 
        return "register"; 
    }

    @PostMapping("/registro/paciente")
    public String registrarPaciente(@Valid @ModelAttribute("pacienteDTO") PacienteDTO dto,
                                    BindingResult result,
                                    Model model) {
        dto.setRegistroExterno(true);
        return pacienteController.procesarRegistro(dto, result, model);
    }

 
}
