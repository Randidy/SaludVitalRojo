package com.saludvital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntradaHistorialDTO {

    @NotNull(message = "El Paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El Médico es obligatorio")
    private Long medicoId;

    @NotBlank(message = "Diagnóstico no puede estar vacío")
    private String diagnostico;

    @NotBlank(message = "Tratamiento no puede estar vacío")
    private String tratamiento;
}
