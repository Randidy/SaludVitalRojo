package com.saludvital.repository;

import com.saludvital.model.HorarioAtencion;
import com.saludvital.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {
    List<HorarioAtencion> findByMedico(Medico medico);
    List<HorarioAtencion> findByMedicoAndDia(Medico medico, DayOfWeek dia);
}

