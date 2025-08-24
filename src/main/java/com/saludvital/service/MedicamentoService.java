package com.saludvital.service;

import com.saludvital.model.Medicamento;
import com.saludvital.repository.MedicamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    public Medicamento obtenerPorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado"));
    }

    public Medicamento guardar(Medicamento medicamento) {
        if (medicamento.getNombre() == null || medicamento.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del Medicamento es obligatorio");
        }

        return medicamentoRepository.save(medicamento);
    }

    public Medicamento actualizar(Long id, Medicamento nuevo) {
        Medicamento existente = obtenerPorId(id);
        existente.setNombre(nuevo.getNombre());

        return medicamentoRepository.save(existente);
    }

    public void eliminar(Long id) {
        medicamentoRepository.deleteById(id);
    }
}
