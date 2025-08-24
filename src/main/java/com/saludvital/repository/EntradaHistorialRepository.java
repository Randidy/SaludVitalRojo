package com.saludvital.repository;

import com.saludvital.model.EntradaHistorial;
import com.saludvital.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntradaHistorialRepository extends JpaRepository<EntradaHistorial, Long> {

    List<EntradaHistorial> findByPacienteOrderByFechaHoraDesc(Paciente paciente);
}
