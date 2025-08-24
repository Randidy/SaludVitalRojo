package com.saludvital.repository;

import com.saludvital.enums.EstadoCita;
import com.saludvital.model.Cita;
import com.saludvital.model.Medico;
import com.saludvital.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

	 List<Cita> findByPacienteAndFechaHoraBetween(Paciente paciente, LocalDateTime start, LocalDateTime end);

	    List<Cita> findByMedicoAndFechaHoraBetween(Medico medico, LocalDateTime start, LocalDateTime end);

	    boolean existsByMedicoAndFechaHora(Medico medico, LocalDateTime fechaHora);
	    
	    List<Cita> findByPaciente(Paciente paciente);
	 
	    List<Cita> findByPacienteAndEstado(Paciente paciente, EstadoCita estado);
	    
	    

}
