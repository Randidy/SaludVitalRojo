package com.saludvital.controller;

import com.saludvital.model.Alergia;
import com.saludvital.service.AlergiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alergias")
@RequiredArgsConstructor
public class AlergiaController {

    private final AlergiaService alergiaService;

    @GetMapping
    public String listarTodas(Model model) {
        model.addAttribute("alergias", alergiaService.listarTodas());
        return "alergia/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("alergia", new Alergia());
        return "alergia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Alergia alergia, BindingResult result) {
        if (result.hasErrors()) {
            return "alergia/formulario";
        }

        alergiaService.guardar(alergia);
        return "redirect:/alergias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Alergia alergia = alergiaService.obtenerPorId(id);
        model.addAttribute("alergia", alergia);
        return "alergia/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Alergia datos,
                             BindingResult result) {

        if (result.hasErrors()) {
            return "alergia/formulario";
        }

        Alergia alergia = alergiaService.obtenerPorId(id);
        alergia.setNombre(datos.getNombre());

        alergiaService.guardar(alergia);
        return "redirect:/alergias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alergiaService.eliminar(id);
        return "redirect:/alergias";
    }
}
