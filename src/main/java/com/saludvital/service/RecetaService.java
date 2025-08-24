package com.saludvital.service;

import com.saludvital.dto.RecetaDTO;
import com.saludvital.model.*;
import com.saludvital.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;

    // 🔹 Listar todas las recetas
    public List<Receta> listarTodas() {
        return recetaRepository.findAll();
    }

    // 🔹 Obtener receta por ID
    public Receta obtenerPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la receta no puede ser nulo");
        }
        return recetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receta no encontrada"));
    }

    // 🔹 Guardar nueva receta
    public Receta guardar(Receta receta) {
        return recetaRepository.save(receta);
    }

    // 🔹 Actualizar receta existente
    public Receta actualizar(Receta receta) {
        if (receta.getId() == null) {
            throw new IllegalArgumentException("Se requiere el ID de la Receta para actualizar");
        }
        if (!recetaRepository.existsById(receta.getId())) {
            throw new EntityNotFoundException("Receta no encontrada para actualizar");
        }
        return recetaRepository.save(receta);
    }

    // 🔹 Eliminar receta por ID
    public void eliminar(Long id) {
        if (id == null || !recetaRepository.existsById(id)) {
            throw new EntityNotFoundException("Receta no encontrada");
        }
        recetaRepository.deleteById(id);
    }

    // 🔹 Crear receta desde DTO
    public Receta crearReceta(RecetaDTO dto) {
        if (dto.getPacienteId() == null || dto.getMedicoId() == null || dto.getMedicamentoId() == null) {
            throw new IllegalArgumentException("Debe seleccionar paciente, médico y medicamento");
        }

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));
        Medicamento medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado"));

        // 🔹 Validar alergias del paciente
        if (paciente.isTieneAlergias() && paciente.getAlergias() != null) {
            for (Alergia alergia : paciente.getAlergias()) {
                if (medicamento.getNombre().equalsIgnoreCase(alergia.getNombre().trim())) {
                    throw new IllegalArgumentException("Paciente alérgico al medicamento seleccionado");
                }
            }
        }

        // 🔹 Crear receta
        Receta receta = new Receta();
        receta.setNumero(UUID.randomUUID().toString());
        receta.setFechaEmision(LocalDate.now());
        receta.setFechaCaducidad(
                dto.getFechaCaducidad() != null ? dto.getFechaCaducidad() : LocalDate.now().plusDays(30)
        );
        receta.setPaciente(paciente);
        receta.setMedico(medico);
        receta.setMedicamento(medicamento);
        receta.setDosis(dto.getDosis());
        receta.setFrecuencia(dto.getFrecuencia());

        return recetaRepository.save(receta);
    }

    private Paciente obtenerPacienteActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Paciente paciente = usuario.getPaciente();
        if (paciente == null) {
            throw new EntityNotFoundException("Paciente no encontrado");
        }
        return paciente;
    }

    public List<Receta> listarRecetasPacienteActual() {
        Paciente paciente = obtenerPacienteActual();
        return recetaRepository.findByPacienteId(paciente.getId());
    }
}
