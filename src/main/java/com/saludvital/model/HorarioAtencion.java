package com.saludvital.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dia;        // LUNES, MARTES, etc.

    @Column(nullable = false)
    private LocalTime horaInicio; // ej. 08:00

    @Column(nullable = false)
    private LocalTime horaFin;    // ej. 17:00

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Medico medico;
    
    
}