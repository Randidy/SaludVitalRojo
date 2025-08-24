package com.saludvital.controller;

import com.saludvital.enums.Especialidad;
import com.saludvital.model.HorarioAtencion;
import com.saludvital.model.Medico;
import com.saludvital.service.MedicoService;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // Listar todos
    @GetMapping
    public String listarMedicos(Model model, @RequestParam(value = "eliminado", required = false) String eliminado) {
        model.addAttribute("medicos", medicoService.listarTodos());
        if (eliminado != null) {
            model.addAttribute("mensaje", "Médico eliminado correctamente.");
        }
        return "medico/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        Medico medico = new Medico();
        medico.setHorarios(new ArrayList<>());
        medico.getHorarios().add(new HorarioAtencion()); // agregamos un horario inicial
        model.addAttribute("medico", medico);
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("edicion", false); // Flag para Thymeleaf
        return "medico/formulario";
    }

    /// Guardar médico
    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute Medico medico,
                                @RequestParam(name = "password", required = false) String password,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("especialidades", Especialidad.values());
            return "medico/formulario";
        }
        medicoService.registrarMedico(medico, password);
        return "redirect:/medicos?guardado"; // CORRECTO
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Medico medico = medicoService.obtenerPorId(id);
        model.addAttribute("medico", medico);
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("edicion", true); // Flag para Thymeleaf
        return "medico/formulario";
    }
    
		 @PostMapping("/actualizar/{id}")
		 public String actualizarMedico(@PathVariable Long id,
		         @ModelAttribute Medico medico,
		         BindingResult result,
		         Model model) {
		if (result.hasErrors()) {
		model.addAttribute("especialidades", Especialidad.values());
		return "medico/editar";
		}
		medicoService.actualizar(id, medico);
		return "redirect:/medicos?actualizado";
		}
 // Eliminar médico
    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.eliminar(id);
        return "redirect:/medicos?eliminado"; // CORRECTO
    }
}
