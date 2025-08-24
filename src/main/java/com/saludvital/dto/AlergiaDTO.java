package com.saludvital.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlergiaDTO {

    private Long id; // Se genera automáticamente

    @NotBlank(message = "El nombre de la alergia es obligatorio")
    private String nombre;
}
