package com.crecedigital.pe.repository.impl;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.model.Tecnico;
import com.crecedigital.pe.repository.ITecnicoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TecnicoRepositoryImpl implements ITecnicoRepository {

    private final Map<Long, Tecnico> tecnicos;
    private Long nextId;

    public TecnicoRepositoryImpl() {
        this.tecnicos = new HashMap<>();
        this.nextId = 1L;
    }

    @Override
    public void agregar(Tecnico tecnico) {
        tecnico.setId(nextId++);
        tecnicos.put(tecnico.getId(), tecnico);
    }

    @Override
    public Tecnico buscarPorId(Long id) {
        return tecnicos.get(id);
    }

    @Override
    public List<Tecnico> listarTodos() {
        return new ArrayList<>(tecnicos.values());
    }

    @Override
    public List<Tecnico> buscarPorEspecialidad(Especialidad especialidad) {
        return tecnicos.values().stream()
                .filter(t -> t.getEspecialidad() == especialidad)
                .collect(Collectors.toList());
    }

    @Override
    public void actualizar(Tecnico tecnico) {
        if (tecnicos.containsKey(tecnico.getId())) {
            tecnicos.put(tecnico.getId(), tecnico);
        } else {
            throw new EntityNotFoundException("Técnico no encontrado con ID: " + tecnico.getId());
        }
    }

    @Override
    public void eliminar(Long id) {
        if (tecnicos.remove(id) == null) {
            throw new EntityNotFoundException("Técnico no encontrado con ID: " + id);
        }
    }

    @Override
    public List<Tecnico> buscarTecnicosDisponibles() {
        return tecnicos.values().stream()
                .filter(Tecnico::isDisponible)
                .collect(Collectors.toList());
    }

    @Override
    public Tecnico buscarPorEmail(String email) {
        return tecnicos.values().stream()
                .filter(t -> t.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean existeEmail(String email) {
        return tecnicos.values().stream()
                .anyMatch(t -> t.getEmail().equalsIgnoreCase(email));
    }
}
