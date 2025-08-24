package com.saludvital.service;

import com.saludvital.model.HorarioAtencion;
import com.saludvital.model.Medico;
import com.saludvital.model.Usuario;
import com.saludvital.repository.MedicoRepository;
import com.saludvital.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

	private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;  // <--- agregar
    private final PasswordEncoder passwordEncoder;      // <--- agregar

    public MedicoService(MedicoRepository medicoRepository,
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

  

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public Medico obtenerPorId(Long id) {
        return medicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado con ID: " + id));
    }

 // Para registrar un nuevo médico (con usuario)
    public Medico registrarMedico(Medico medico, String passwordPlano) {
        if (medico.getHorarios() != null) {
            medico.getHorarios().forEach(h -> h.setMedico(medico));
        }

        Medico medicoGuardado = medicoRepository.save(medico);

        Usuario usuario = new Usuario();
        usuario.setMedico(medicoGuardado);
        usuario.setPaciente(null);
        usuario.setRoles("MEDICO");
        usuario.setUsuario(medicoGuardado.getEmail());
        usuario.setPassword(passwordEncoder.encode(passwordPlano != null ? passwordPlano : "123456"));
        usuarioRepository.save(usuario);

        medicoGuardado.setUsuario(usuario);
        return medicoRepository.save(medicoGuardado);
    }

    public Medico actualizar(Long id, Medico datosActualizados) {
        Medico medico = medicoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        // Actualizar campos simples
        medico.setNombre(datosActualizados.getNombre());
        medico.setApellido(datosActualizados.getApellido());
        medico.setEspecialidad(datosActualizados.getEspecialidad());

        // Actualizar horarios
        List<HorarioAtencion> horariosExistentes = medico.getHorarios();
        horariosExistentes.clear(); // eliminar todos los horarios antiguos (orphanRemoval se encargará)
        if (datosActualizados.getHorarios() != null) {
            datosActualizados.getHorarios().forEach(h -> {
                h.setMedico(medico); // asociar con el medico
                horariosExistentes.add(h); // agregar a la lista original
            });
        }

        return medicoRepository.save(medico);
    }
    public void eliminar(Long id) {
        medicoRepository.deleteById(id);
    }
}
