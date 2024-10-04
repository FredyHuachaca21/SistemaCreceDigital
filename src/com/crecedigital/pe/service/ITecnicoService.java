package com.crecedigital.pe.service;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.model.Tecnico;

import java.util.List;

public interface ITecnicoService {
    void registrarTecnico(Tecnico tecnico);
    Tecnico buscarTecnico(Long id);
    List<Tecnico> listarTecnicos();
    List<Tecnico> buscarTecnicosPorEspecialidad(Especialidad especialidad);
    void actualizarTecnico(Tecnico tecnico);
    void eliminarTecnico(Long id);
    List<Tecnico> obtenerTecnicosDisponibles();
    void cambiarDisponibilidadTecnico(Long id, boolean disponible);
    void actualizar(Tecnico tecnico);
}
