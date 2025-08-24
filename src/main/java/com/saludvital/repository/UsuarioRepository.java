package com.saludvital.repository;
import com.saludvital.model.Paciente;
import com.saludvital.model.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	   Optional<Usuario> findByUsuario(String usuario);
	    boolean existsByUsuario(String usuario);
	    Optional<Usuario> findByPaciente(Paciente paciente);
	    
	    
	    
}
