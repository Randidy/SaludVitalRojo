package com.saludvital.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaDTO {

    @NotNull(message = "La Fecha y Hora es obligatoria")
    @Future(message = "La Fecha y Hora debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El Consultorio es obligatorio")
    private String consultorio;

    @NotBlank(message = "El Motivo es obligatorio")
    private String motivo;

    @NotNull(message = "Debe seleccionar un Médico")
    private Long medicoId;

    private Long pacienteId;
}
