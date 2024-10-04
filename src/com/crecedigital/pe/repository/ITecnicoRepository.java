package com.crecedigital.pe.repository;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.model.Tecnico;

import java.util.List;

public interface ITecnicoRepository {
    void agregar(Tecnico tecnico);
    Tecnico buscarPorId(Long id);
    List<Tecnico> listarTodos();
    List<Tecnico> buscarPorEspecialidad(Especialidad especialidad);
    void actualizar(Tecnico tecnico);
    void eliminar(Long id);
    List<Tecnico> buscarTecnicosDisponibles();
    Tecnico buscarPorEmail(String email);
    boolean existeEmail(String email);
}
