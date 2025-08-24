package com.saludvital.repository;

import com.saludvital.model.Alergia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlergiaRepository extends JpaRepository<Alergia, Long> {
	 Optional<Alergia> findByNombre(String nombre);
}

