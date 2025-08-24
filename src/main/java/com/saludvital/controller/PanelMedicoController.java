package com.saludvital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medico")
public class PanelMedicoController {

    @GetMapping("/inicio")
    public String inicioMedico() {
        return "inicio_medico"; // archivo en templates/inicio_medico.html
    }
}