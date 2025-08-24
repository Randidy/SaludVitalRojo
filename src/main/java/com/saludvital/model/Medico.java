package com.saludvital.model;

import com.saludvital.enums.Especialidad;
import com.saludvital.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    // 👉 Campos adicionales según tu documento funcional
    private String apellido;
    private String numeroLicencia; // único para cada médico
    private String telefono;
    private String email;

    private String consultorio; // consultorio asignado
    private BigDecimal tarifaConsulta; // costo de la cita

    private boolean disponible; // si está activo para citas

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private List<Cita> citas;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private List<Receta> recetas;

 // Relación con Usuario (login)
    @OneToOne(mappedBy = "medico", cascade = CascadeType.ALL)
    private Usuario usuario;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioAtencion> horarios = new ArrayList<>();
    
    
 
}
