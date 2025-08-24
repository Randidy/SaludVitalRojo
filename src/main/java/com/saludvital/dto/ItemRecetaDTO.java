package com.saludvital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRecetaDTO {

    @NotNull(message = "Debe seleccionar un Medicamento")
    private Long medicamentoId;

    @NotBlank(message = "Ingrese la dosis")
    private String dosis;

    @NotBlank(message = "Ingrese la frecuencia")
    private String frecuencia;
}
