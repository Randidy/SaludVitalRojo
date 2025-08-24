package com.saludvital.controller;

import com.saludvital.model.Medicamento;
import com.saludvital.service.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @GetMapping("/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("medicamento", medicamentoService.obtenerPorId(id));
        return "medicamento/formulario";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicamentos", medicamentoService.listarTodos());
        return "medicamento/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "medicamento/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("medicamento") @Valid Medicamento medicamento,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            return "medicamento/formulario";
        }

        try {
            medicamentoService.guardar(medicamento);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "medicamento/formulario";
        }

        return "redirect:/medicamentos?exito";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("medicamento") Medicamento medicamento) {

        medicamentoService.actualizar(id, medicamento);
        return "redirect:/medicamentos?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        medicamentoService.eliminar(id);
        return "redirect:/medicamentos?eliminado";
    }
}
