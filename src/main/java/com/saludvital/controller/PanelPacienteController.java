package com.saludvital.controller;

import com.saludvital.dto.CitaDTO;
import com.saludvital.model.Cita;
import com.saludvital.model.EntradaHistorial;
import com.saludvital.model.Paciente;
import com.saludvital.model.Receta;
import com.saludvital.model.Usuario;
import com.saludvital.service.CitaService;
import com.saludvital.service.EntradaHistorialService;
import com.saludvital.service.MedicoService;
import com.saludvital.service.RecetaService;

import jakarta.persistence.EntityNotFoundException;

import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PanelPacienteController {

	private final MedicoService medicoService;
    private final CitaService citaService;
    private final RecetaService recetaService;
    private final EntradaHistorialService entradaHistorialService;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    
    @GetMapping("/inicio")
    public String inicioPaciente() {
        return "paciente/inicio";
    }

    @GetMapping("/citas")
    public String citasPaciente(Model model) {
        List<Cita> citas = citaService.listarCitasPacienteActual();
        model.addAttribute("citas", citas);
        if (citas.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron citas registradas.");
        }
        return "paciente/citas_consultasid";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCitaPaciente(Model model) {
        model.addAttribute("citaDTO", new CitaDTO());
        model.addAttribute("medicos", medicoService.listarTodos());
        return "paciente/citas_registro";
    }

    @PostMapping("/citas/guardar")
    public String guardarCitaPaciente(@ModelAttribute("citaDTO") CitaDTO citaDTO,
                                      Model model) {
        try {
            citaService.guardarCitaPacienteActual(citaDTO);
            return "redirect:/paciente/citas?exito";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("citaDTO", citaDTO);
            model.addAttribute("medicos", medicoService.listarTodos());
            return "paciente/citas_registro";
        }
    }

    @GetMapping("/recetas")
    public String recetasPaciente(Model model) {
        List<Receta> recetas = recetaService.listarRecetasPacienteActual();
        model.addAttribute("recetas", recetas);

        if (recetas.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron recetas para este paciente.");
        }

        return "paciente/recetas_consultasid"; 
    }
    
    @GetMapping("/historial")
    public String historialPaciente(Model model) {
        Paciente paciente = obtenerPacienteActual();
        List<EntradaHistorial> historiales = entradaHistorialService.obtenerHistorialPorPaciente(paciente.getId());
        model.addAttribute("historiales", historiales);

        if (historiales.isEmpty()) {
            model.addAttribute("mensaje", "No se encontraron registros en el historial.");
        }

        return "paciente/historial_consultasid";
    }

   
    private Paciente obtenerPacienteActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Paciente paciente = usuario.getPaciente();
        if (paciente == null) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        return paciente;
    }
}
