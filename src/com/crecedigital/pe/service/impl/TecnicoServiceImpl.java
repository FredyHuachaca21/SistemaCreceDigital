package com.crecedigital.pe.service.impl;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.model.Tecnico;
import com.crecedigital.pe.repository.ITecnicoRepository;
import com.crecedigital.pe.service.ITecnicoService;

import java.util.List;

public class TecnicoServiceImpl implements ITecnicoService {

    private final ITecnicoRepository tecnicoRepository;

    public TecnicoServiceImpl(ITecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    public void registrarTecnico(Tecnico tecnico) {
        if (tecnicoRepository.existeEmail(tecnico.getEmail())) {
            throw new IllegalArgumentException("Ya existe un técnico con ese email.");
        }
        tecnicoRepository.agregar(tecnico);
    }

    @Override
    public Tecnico buscarTecnico(Long id) {
        Tecnico tecnico = tecnicoRepository.buscarPorId(id);
        if (tecnico == null) {
            throw new EntityNotFoundException("Técnico no encontrado con ID: " + id);
        }
        return tecnico;
    }

    @Override
    public List<Tecnico> listarTecnicos() {
        return tecnicoRepository.listarTodos();
    }

    @Override
    public List<Tecnico> buscarTecnicosPorEspecialidad(Especialidad especialidad) {
        return tecnicoRepository.buscarPorEspecialidad(especialidad);
    }

    @Override
    public void actualizarTecnico(Tecnico tecnico) {
        Tecnico tecnicoExistente = tecnicoRepository.buscarPorId(tecnico.getId());
        if (tecnicoExistente == null) {
            throw new EntityNotFoundException("Técnico no encontrado con ID: " + tecnico.getId());
        }

        if (!tecnicoExistente.getEmail().equals(tecnico.getEmail()) &&
                tecnicoRepository.existeEmail(tecnico.getEmail())) {
            throw new IllegalArgumentException("Ya existe un técnico con ese email.");
        }

        tecnicoRepository.actualizar(tecnico);
    }

    @Override
    public void eliminarTecnico(Long id) {
        if (tecnicoRepository.buscarPorId(id) == null) {
            throw new EntityNotFoundException("Técnico no encontrado con ID: " + id);
        }
        tecnicoRepository.eliminar(id);
    }

    @Override
    public List<Tecnico> obtenerTecnicosDisponibles() {
        return tecnicoRepository.buscarTecnicosDisponibles();
    }

    @Override
    public void cambiarDisponibilidadTecnico(Long id, boolean disponible) {
        Tecnico tecnico = buscarTecnico(id);
        tecnico.setDisponible(disponible);
        tecnicoRepository.actualizar(tecnico);
    }

    @Override
    public void actualizar(Tecnico tecnico) {
        tecnicoRepository.actualizar(tecnico);
    }
}
