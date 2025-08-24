package com.saludvital.repository;

import com.saludvital.model.Paciente;
import com.saludvital.model.Receta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
	  Optional<Paciente> findByNumeroIdentificacion(String numeroIdentificacion);
    
    

}
