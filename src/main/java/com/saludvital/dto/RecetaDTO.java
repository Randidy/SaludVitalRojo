package com.saludvital.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecetaDTO {
    private Long pacienteId;
    private Long medicoId;
    private Long medicamentoId;
    private String dosis;
    private String frecuencia;
    private LocalDate fechaCaducidad;
}
