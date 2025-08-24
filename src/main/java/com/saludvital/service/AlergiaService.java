package com.saludvital.service;

import com.saludvital.model.Alergia;
import com.saludvital.repository.AlergiaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlergiaService {

    private final AlergiaRepository alergiaRepository;

    public List<Alergia> listarTodas() {
        return alergiaRepository.findAll();
    }

    public Alergia guardar(Alergia alergia) {
        return alergiaRepository.save(alergia);
    }

    public Alergia obtenerPorId(Long id) {
        return alergiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alergia no encontrada"));
    }

    public void eliminar(Long id) {
        if (!alergiaRepository.existsById(id)) {
            throw new EntityNotFoundException("Alergia no encontrada");
        }
        alergiaRepository.deleteById(id);
    }
}
