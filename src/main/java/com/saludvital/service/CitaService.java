package com.saludvital.service;

import com.saludvital.dto.CitaDTO;
import com.saludvital.enums.EstadoCita;
import com.saludvital.model.Cita;
import com.saludvital.model.Medico;
import com.saludvital.model.Paciente;
import com.saludvital.model.Receta;
import com.saludvital.model.Usuario;
import com.saludvital.repository.CitaRepository;
import com.saludvital.repository.MedicoRepository;
import com.saludvital.repository.PacienteRepository;
import com.saludvital.repository.RecetaRepository;
import com.saludvital.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final RecetaRepository recetaRepository;
    private final UsuarioRepository usuarioRepository; // inyectado

    // =======================
    // Métodos públicos
    // =======================

    // Listar todas las citas (para admin)
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    // Obtener cita por ID
    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
    }

    // Crear cita genérica (admin/médico)
    public Cita crearCita(CitaDTO dto) {
        validarFechaFutura(dto.getFechaHora());
        validarHorario(dto.getFechaHora());

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        verificarConflictos(paciente, medico, dto.getFechaHora());
        validarContraHorarioDelMedico(medico, dto.getFechaHora());

        Cita cita = new Cita();
        cita.setFechaHora(dto.getFechaHora());
        cita.setMotivo(dto.getMotivo());
        cita.setMedico(medico);
        cita.setPaciente(paciente);
        cita.setConsultorio(medico.getConsultorio());
        cita.setEstado(EstadoCita.ACTIVA);

        return citaRepository.save(cita);
    }

    // Crear cita como paciente logueado
    public Cita guardarCitaPacienteActual(CitaDTO dto) {
        validarFechaFutura(dto.getFechaHora());
        validarHorario(dto.getFechaHora());

        Paciente paciente = obtenerPacienteActual();
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        verificarConflictos(paciente, medico, dto.getFechaHora());
        validarContraHorarioDelMedico(medico, dto.getFechaHora());

        Cita cita = new Cita();
        cita.setFechaHora(dto.getFechaHora());
        cita.setMotivo(dto.getMotivo());
        cita.setMedico(medico);
        cita.setPaciente(paciente);
        cita.setConsultorio(medico.getConsultorio());
        cita.setEstado(EstadoCita.ACTIVA);

        return citaRepository.save(cita);
    }

    // Modificar cita (admin/médico)
    public Cita modificarCita(Long id, CitaDTO dto) {
        Cita citaExistente = obtenerPorId(id);

        if (citaExistente.getFechaHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Solo se permiten modificaciones con un mínimo de 2 horas de anticipación");
        }

        validarHorario(dto.getFechaHora());

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        verificarConflictos(paciente, medico, dto.getFechaHora());
        validarContraHorarioDelMedico(medico, dto.getFechaHora());

        citaExistente.setFechaHora(dto.getFechaHora());
        citaExistente.setMotivo(dto.getMotivo());
        citaExistente.setMedico(medico);
        citaExistente.setPaciente(paciente);
        citaExistente.setConsultorio(medico.getConsultorio());

        return citaRepository.save(citaExistente);
    }

    // Cancelar cita
    public void cancelarCita(Long id) {
        Cita cita = obtenerPorId(id);

        if (cita.getFechaHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Las citas deben cancelarse al menos 2 horas antes de su horario programado");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    // Listar citas activas del paciente logueado
    public List<Cita> listarCitasPacienteActual() {
        Paciente paciente = obtenerPacienteActual();
        return citaRepository.findByPacienteAndEstado(paciente, EstadoCita.ACTIVA);
    }

    // Listar historial completo de citas del paciente logueado
    public List<Cita> listarHistorialPacienteActual() {
        Paciente paciente = obtenerPacienteActual();
        return citaRepository.findByPaciente(paciente);
    }

    // =======================
    // Métodos privados
    // =======================

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
    private void validarFechaFutura(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La cita debe ser en el futuro");
        }
    }

    private void validarHorario(LocalDateTime fechaHora) {
    	DayOfWeek dia = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        if (dia.getValue() == 6 || dia.getValue() == 7) {
            throw new IllegalArgumentException("Las citas solo pueden programarse de lunes a viernes");
        }

        if (hora.isBefore(LocalTime.of(8, 0)) || hora.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Las citas deben estar dentro del horario de 08:00 a 17:00");
        }
    }

    private void validarContraHorarioDelMedico(Medico medico, LocalDateTime fechaHora) {
        if (medico.getHorarios() == null || medico.getHorarios().isEmpty()) return;

        DayOfWeek dia = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        boolean dentro = medico.getHorarios().stream().anyMatch(h ->
                h.getDia() == dia &&
                        !hora.isBefore(h.getHoraInicio()) &&
                        !hora.isAfter(h.getHoraFin())
        );

        if (!dentro) {
            throw new IllegalArgumentException("El horario seleccionado no está dentro del horario de atención del médico");
        }
    }

    private void verificarConflictos(Paciente paciente, Medico medico, LocalDateTime fechaHora) {
        LocalDate fecha = fechaHora.toLocalDate();
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        List<Cita> citasPaciente = citaRepository.findByPacienteAndFechaHoraBetween(paciente, inicio, fin);
        if (!citasPaciente.isEmpty()) {
            throw new IllegalArgumentException("El paciente ya tiene una cita programada en esa fecha");
        }

        if (citaRepository.existsByMedicoAndFechaHora(medico, fechaHora)) {
            throw new IllegalArgumentException("El médico ya tiene una cita programada en ese horario");
        }
    }
    
 // Listar recetas de un paciente usando su ID
    public List<Receta> listarRecetasPacientePorId(Long pacienteId) {
        return recetaRepository.findByPacienteId(pacienteId);
    }

    // Alternativamente, si ya tienes el objeto Paciente
    public List<Receta> listarRecetasPaciente(Paciente paciente) {
        return recetaRepository.findByPaciente(paciente);
    }
}
