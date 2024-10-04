package com.crecedigital.pe.service;

import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.model.SolicitudServicio;
import com.crecedigital.pe.model.Tecnico;

import java.util.List;

public interface ISoporteService {
    void crearSolicitud(SolicitudServicio solicitud);
    void asignarTecnico(Long solicitudId, Long tecnicoId);
    void actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitud nuevoEstado);
    List<SolicitudServicio> listarSolicitudesPendientes();
    public String generarReporteServicios();
    SolicitudServicio obtenerSiguienteSolicitud();
    List<SolicitudServicio> listarSolicitudes();
    List<Tecnico> listarTecnicos();
    List<Tecnico> obtenerTecnicosDisponibles();
    String obtenerHistorialCambios();
    SolicitudServicio buscarSolicitud(Long solicitudId);
    void asignarTecnicoAutomaticamente(Long solicitudId);
}
