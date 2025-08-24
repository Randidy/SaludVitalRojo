package com.saludvital.dto;

import lombok.Data;

import java.util.List;

@Data
public class PacienteDTO {
    // Datos básicos del paciente
    private String nombre;
    private String numeroIdentificacion;
    private String fechaNacimiento; // tipo String para input type="date"
    private String telefono;
    private String direccion;
    private String email;

    // Datos de usuario (opcional si admin registra)
    private String usuario;
    private String password;

    // Alergias
    private Boolean tieneAlergias = false; // por defecto falso
    private List<Long> alergiaIds;

    // Indica si viene del registro externo
    private boolean registroExterno = false;

    // Getter explícito para evitar errores en Thymeleaf o validaciones
    public boolean isRegistroExterno() {
        return registroExterno;
    }
}
