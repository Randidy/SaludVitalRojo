package com.saludvital.controller;

import com.saludvital.dto.RecetaDTO;
import com.saludvital.model.Medicamento;
import com.saludvital.model.Medico;
import com.saludvital.model.Paciente;
import com.saludvital.service.MedicoService;
import com.saludvital.service.MedicamentoService;
import com.saludvital.service.PacienteService;
import com.saludvital.service.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final PacienteService pacienteService;
    private final MedicamentoService medicamentoService;
    private final MedicoService medicoService;

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("receta") @Valid RecetaDTO recetaDTO,
                          BindingResult result,
                          Model model) {

        // Validar errores de formulario
        if (result.hasErrors() || recetaDTO.getPacienteId() == null || recetaDTO.getMedicoId() == null || recetaDTO.getMedicamentoId() == null) {
            model.addAttribute("error", "Debe seleccionar paciente, médico y medicamento correctamente.");
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicamentos", medicamentoService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "receta/formulario";
        }

        try {
            recetaService.crearReceta(recetaDTO);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("medicamentos", medicamentoService.listarTodos());
            model.addAttribute("medicos", medicoService.listarTodos());
            return "receta/formulario";
        }

        return "redirect:/recetas?exito";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("recetas", recetaService.listarTodas());
        return "receta/lista";
    }

    @GetMapping("/nueva")
    public String formulario(Model model) {
        RecetaDTO recetaDTO = new RecetaDTO();
        recetaDTO.setPacienteId(null);
        recetaDTO.setMedicoId(null);
        recetaDTO.setMedicamentoId(null);

        model.addAttribute("receta", recetaDTO);

        // Obtener listas
        List<Paciente> pacientes = pacienteService.listarTodos();
        if (pacientes == null) pacientes = new ArrayList<>();
        model.addAttribute("pacientes", pacientes);

        List<Medicamento> medicamentos = medicamentoService.listarTodos();
        if (medicamentos == null) medicamentos = new ArrayList<>();
        model.addAttribute("medicamentos", medicamentos);

        List<Medico> medicos = medicoService.listarTodos();
        if (medicos == null) medicos = new ArrayList<>();
        model.addAttribute("medicos", medicos);

        return "receta/formulario";
    }
}
