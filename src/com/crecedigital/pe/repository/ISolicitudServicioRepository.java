package com.crecedigital.pe.repository;


import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.model.SolicitudServicio;

import java.util.List;

public interface ISolicitudServicioRepository {

    void agregar(SolicitudServicio solicitud);
    SolicitudServicio buscarPorId(Long id);
    void actualizar(SolicitudServicio solicitud);
    void eliminar(Long id);
    List<SolicitudServicio> listarTodas();
    SolicitudServicio obtenerSiguienteSolicitud();
    List<SolicitudServicio> buscarPorEstado(EstadoSolicitud estadoSolicitud);
}
