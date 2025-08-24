package com.saludvital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true) // el username debe ser único
	    private String usuario;  // nombre de usuario / login

	    @Column(nullable = false)
	    private String password;

	    @Column(nullable = false)
	    private String roles; // Ej: "ADMIN", "PACIENTE", "MEDICO"

	    // Relación opcional con Paciente
	    @OneToOne
	    @JoinColumn(name = "paciente_id")
	    private Paciente paciente;

	    // Relación opcional con Médico
	    @OneToOne
	    @JoinColumn(name = "medico_id")
	    private Medico medico;
	
	}
