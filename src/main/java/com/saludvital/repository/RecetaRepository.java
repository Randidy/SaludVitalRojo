package com.saludvital.repository;

import com.saludvital.model.Paciente;
import com.saludvital.model.Receta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
	 List<Receta> findByPaciente(Paciente paciente);
	    List<Receta> findByPacienteId(Long pacienteId); 
	  

}
