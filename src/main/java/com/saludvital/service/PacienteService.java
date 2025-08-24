package com.saludvital.service;

import com.saludvital.dto.PacienteDTO;
import com.saludvital.enums.Rol;
import com.saludvital.model.Alergia;
import com.saludvital.model.Paciente;
import com.saludvital.model.Usuario;
import com.saludvital.repository.AlergiaRepository;
import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;


    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
    }

    public Paciente crearPaciente(PacienteDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        paciente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento())); // 👈 aquí corregido
        paciente.setTelefono(dto.getTelefono());
        paciente.setDireccion(dto.getDireccion());
        paciente.setEmail(dto.getEmail());
        paciente.setTieneAlergias(dto.getTieneAlergias());
        if (dto.getAlergiaIds() != null) {
            paciente.setAlergias(alergiaRepository.findAllById(dto.getAlergiaIds()));
        }

        // Guardamos paciente
        Paciente guardado = pacienteRepository.save(paciente);

        // Si es registro externo, creamos también el usuario
        if (dto.isRegistroExterno()) {
            Usuario usuario = new Usuario();
            usuario.setUsuario(dto.getUsuario());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setRoles("PACIENTE"); 
            usuario.setPaciente(guardado); 

            usuarioRepository.save(usuario);
        }

        return guardado;
    }

    public Paciente actualizarPaciente(Long id, PacienteDTO dto, PasswordEncoder passwordEncoder) {
        Paciente paciente = obtenerPorId(id);

        paciente.setNombre(dto.getNombre());
        paciente.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        paciente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        paciente.setTelefono(dto.getTelefono());
        paciente.setDireccion(dto.getDireccion());
        paciente.setEmail(dto.getEmail());
        paciente.setTieneAlergias(Boolean.TRUE.equals(dto.getTieneAlergias()));

        if (paciente.isTieneAlergias() && dto.getAlergiaIds() != null && !dto.getAlergiaIds().isEmpty()) {
            List<Alergia> alergias = alergiaRepository.findAllById(dto.getAlergiaIds());
            paciente.setAlergias(alergias);
        } else {
            if (paciente.getAlergias() != null) {
                paciente.getAlergias().clear();
            }
        }

        paciente.setRol(Rol.PACIENTE);

        // Guardamos cambios del paciente
        pacienteRepository.save(paciente);

        // --- Manejo del Usuario ---
        if ((dto.getUsuario() != null && !dto.getUsuario().isEmpty()) &&
            (dto.getPassword() != null && !dto.getPassword().isEmpty())) {

            // Buscar si ya existe un usuario para este paciente
            Usuario usuario = usuarioRepository.findByPaciente(paciente).orElse(new Usuario());
            usuario.setPaciente(paciente);
            usuario.setUsuario(dto.getUsuario());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setRoles("PACIENTE");

            usuarioRepository.save(usuario);
        }

        return paciente;
    }

    public void eliminarPaciente(Long id) {
        Paciente paciente = obtenerPorId(id);
        pacienteRepository.delete(paciente);
    }

    public int calcularEdad(Paciente paciente) {
        return Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears();
    }

    public PacienteDTO convertirADTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setNombre(paciente.getNombre());
        dto.setNumeroIdentificacion(paciente.getNumeroIdentificacion());
        dto.setFechaNacimiento(paciente.getFechaNacimiento().toString());
        dto.setTelefono(paciente.getTelefono());
        dto.setDireccion(paciente.getDireccion());
        dto.setEmail(paciente.getEmail());
        dto.setTieneAlergias(paciente.isTieneAlergias());

        // Pasar los IDs de las alergias al DTO
        if (paciente.getAlergias() != null && !paciente.getAlergias().isEmpty()) {
            List<Long> alergiaIds = new ArrayList<>();
            for (Alergia alergia : paciente.getAlergias()) {
                alergiaIds.add(alergia.getId());
            }
            dto.setAlergiaIds(alergiaIds);
        } else {
            dto.setAlergiaIds(null);
        }

        return dto;
    }
}
