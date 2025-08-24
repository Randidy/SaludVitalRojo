package com.saludvital.controller;

import com.saludvital.dto.EntradaHistorialDTO;
import com.saludvital.enums.Rol;
import com.saludvital.model.Paciente;
import com.saludvital.service.EntradaHistorialService;
import com.saludvital.service.MedicoService;
import com.saludvital.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class EntradaHistorialController {

    private final EntradaHistorialService historialService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @GetMapping("/paciente/{id}")
    public String verHistorial(@PathVariable("id") Long pacienteId, Model model) {

        model.addAttribute("historial", historialService.obtenerHistorialPorPaciente(pacienteId));
        model.addAttribute("paciente", pacienteService.obtenerPorId(pacienteId));

        return "historial/ver";
    }

    @PostMapping("/guardar")
    public String guardarEntrada(@Valid @ModelAttribute("entrada") EntradaHistorialDTO dto,
                                 BindingResult result,
                                 Model model) {

        if (result.hasErrors()) {
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "historial/formulario";
        }

        try {
            historialService.agregarEntrada(dto, Rol.MEDICO);

        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "historial/formulario";
        }

        return "redirect:/historial/paciente/" + dto.getPacienteId();
    }

    @GetMapping("/nueva")
    public String nuevaEntrada(Model model) {

        model.addAttribute("entrada", new EntradaHistorialDTO());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarTodos());

        return "historial/formulario";
    }

    @GetMapping
    public String mostrarMenu(Model model) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "historial/menu";
    }
}
