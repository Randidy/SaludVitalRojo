package com.saludvital.controller;

import com.saludvital.dto.CitaDTO;
import com.saludvital.model.Cita;
import com.saludvital.service.CitaService;
import com.saludvital.service.MedicoService;
import com.saludvital.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

	 private final CitaService citaService;
	    private final MedicoService medicoService;
	    private final PacienteService pacienteService;

	    /** Listar todas las citas */
	    @GetMapping
	    public String listarCitas(Model model) {
	        model.addAttribute("citas", citaService.listarTodas());
	        return "cita/lista";
	    }

	    /** Mostrar formulario de nueva cita */
	    @GetMapping("/nueva")
	    public String mostrarFormularioNueva(Model model) {
	        model.addAttribute("citaDTO", new CitaDTO());
	        cargarDatosFormulario(model);
	        return "cita/formulario";
	    }

	    /** Guardar nueva cita */
	    @PostMapping("/guardar")
	    public String guardarCita(@Valid @ModelAttribute("citaDTO") CitaDTO dto,
	                              BindingResult result,
	                              Model model) {
	        if (result.hasErrors()) {
	            cargarDatosFormulario(model);
	            return "cita/formulario";
	        }

	        try {
	            citaService.crearCita(dto);
	            return "redirect:/citas?exito";
	        } catch (IllegalArgumentException ex) {
	            model.addAttribute("error", ex.getMessage());
	            cargarDatosFormulario(model);
	            return "cita/formulario";
	        }
	    }

	    /** Mostrar formulario de edición */
	    @GetMapping("/editar/{id}")
	    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
	        Cita cita = citaService.obtenerPorId(id);

	        CitaDTO dto = new CitaDTO();
	        dto.setFechaHora(cita.getFechaHora());
	        dto.setConsultorio(cita.getConsultorio());
	        dto.setMotivo(cita.getMotivo());
	        dto.setPacienteId(cita.getPaciente().getId());
	        dto.setMedicoId(cita.getMedico().getId());

	        model.addAttribute("citaDTO", dto);
	        model.addAttribute("citaId", cita.getId());
	        cargarDatosFormulario(model);

	        return "cita/formulario";
	    }

	    /** Actualizar cita */
	    @PostMapping("/actualizar/{id}")
	    public String actualizarCita(@PathVariable Long id,
	                                 @Valid @ModelAttribute("citaDTO") CitaDTO dto,
	                                 BindingResult result,
	                                 Model model) {
	        if (result.hasErrors()) {
	            cargarDatosFormulario(model);
	            model.addAttribute("citaId", id);
	            return "cita/formulario";
	        }

	        try {
	            citaService.modificarCita(id, dto);
	            return "redirect:/citas?actualizado";
	        } catch (IllegalArgumentException ex) {
	            model.addAttribute("error", ex.getMessage());
	            cargarDatosFormulario(model);
	            model.addAttribute("citaId", id);
	            return "cita/formulario";
	        }
	    }

	    /** Cancelar cita */
	    @GetMapping("/cancelar/{id}")
	    public String cancelarCita(@PathVariable Long id, Model model) {
	        try {
	            citaService.cancelarCita(id);
	            return "redirect:/citas?cancelada";
	        } catch (IllegalArgumentException ex) {
	            return "redirect:/citas?error=" + ex.getMessage();
	        }
	    }

	    /** Método privado para cargar pacientes y médicos en el formulario */
	    private void cargarDatosFormulario(Model model) {
	        model.addAttribute("pacientes", pacienteService.listarTodos());
	        model.addAttribute("medicos", medicoService.listarTodos());
	    }
	}