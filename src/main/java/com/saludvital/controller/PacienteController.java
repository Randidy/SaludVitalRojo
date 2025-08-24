package com.saludvital.controller;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.model.Paciente;
import com.saludvital.service.AlergiaService;
import com.saludvital.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final AlergiaService alergiaService;
    private final PasswordEncoder passwordEncoder;
    
    
 // PacienteController.java
    @PostMapping("/registro")
    public String procesarRegistro(PacienteDTO dto, BindingResult result, Model model) {
        // Validación de alergias
        if (Boolean.TRUE.equals(dto.getTieneAlergias()) &&
            (dto.getAlergiaIds() == null || dto.getAlergiaIds().isEmpty())) {
            result.rejectValue("alergiaIds", "alergia.requerida", "Debe seleccionar al menos una alergia");
        }

        if (result.hasErrors()) {
            model.addAttribute("alergiasDisponibles", alergiaService.listarTodas());
            return "paciente/formulario";
        }

        try {
            // Crear paciente usando el servicio
            Paciente paciente = pacienteService.crearPaciente(dto);

            // Registrar usuario y contraseña obligatoriamente si no existe
            if (paciente.getUsuario() == null || paciente.getUsuario().isEmpty()) {
                pacienteService.actualizarPaciente(paciente.getId(), dto, passwordEncoder);
            }

        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMensaje", ex.getMessage());
            model.addAttribute("alergiasDisponibles", alergiaService.listarTodas());
            return "paciente/formulario";
        }

        if (dto.isRegistroExterno()) {
            return "redirect:/login?registrado";
        } else {
            return "redirect:/pacientes?exito";
        }
    }
    
    

    @PostMapping("/guardar")
    public String guardarPaciente(@Valid @ModelAttribute("pacienteDTO") PacienteDTO dto,
                                  BindingResult result,
                                  Model model) {
        return procesarRegistro(dto, result, model);
    }
    
    

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "paciente/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pacienteDTO", new PacienteDTO());
        model.addAttribute("alergiasDisponibles", alergiaService.listarTodas());
        return "paciente/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.obtenerPorId(id);
        PacienteDTO dto = pacienteService.convertirADTO(paciente);

        // Convertir la lista de alergias a IDs usando Collectors.toList()
        List<Long> alergiaIds = paciente.getAlergias().stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());
        dto.setAlergiaIds(alergiaIds);

        model.addAttribute("pacienteDTO", dto);
        model.addAttribute("pacienteId", id);
        model.addAttribute("alergiasDisponibles", alergiaService.listarTodas());
        return "paciente/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarPaciente(@PathVariable Long id,
                                     @Valid @ModelAttribute("pacienteDTO") PacienteDTO dto,
                                     BindingResult result,
                                     Model model) {

        if (Boolean.TRUE.equals(dto.getTieneAlergias()) &&
            (dto.getAlergiaIds() == null || dto.getAlergiaIds().isEmpty())) {
            result.rejectValue("alergiaIds", "alergia.requerida", "Debe seleccionar al menos una alergia");
        }

        if (result.hasErrors()) {
            model.addAttribute("pacienteId", id);
            model.addAttribute("alergiasDisponibles", alergiaService.listarTodas());
            return "paciente/formulario";
        }

        // Actualizar paciente usando el servicio y el passwordEncoder
        pacienteService.actualizarPaciente(id, dto, passwordEncoder);
        return "redirect:/pacientes?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return "redirect:/pacientes?eliminado";
    }
}
