package com.crecedigital.pe.repository.impl;

import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.model.SolicitudServicio;
import com.crecedigital.pe.repository.ISolicitudServicioRepository;

import java.util.*;
import java.util.stream.Collectors;

public class SolicitudServicioRepositoryImpl implements ISolicitudServicioRepository {
    private final Map<Long, SolicitudServicio> solicitudes;
    private final PriorityQueue<SolicitudServicio> colaPrioridad;
    private Long nextId;

    public SolicitudServicioRepositoryImpl() {
        this.solicitudes = new HashMap<>();
        this.colaPrioridad = new PriorityQueue<>(Comparator.comparing(SolicitudServicio::getPrioridad).reversed());
        this.nextId = 1L;
    }

    @Override
    public void agregar(SolicitudServicio solicitud) {
        solicitud.setId(nextId++);
        solicitudes.put(solicitud.getId(), solicitud);
        colaPrioridad.offer(solicitud);
    }

    @Override
    public SolicitudServicio buscarPorId(Long id) {
        return solicitudes.get(id);
    }

    @Override
    public void actualizar(SolicitudServicio solicitud) {
        if (solicitudes.containsKey(solicitud.getId())) {
            SolicitudServicio oldSolicitud = solicitudes.get(solicitud.getId());
            solicitudes.put(solicitud.getId(), solicitud);
            colaPrioridad.remove(oldSolicitud);
            colaPrioridad.offer(solicitud);
        } else {
            throw new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + solicitud.getId());
        }
    }

    @Override
    public void eliminar(Long id) {
        SolicitudServicio solicitud = solicitudes.remove(id);
        if (solicitud != null) {
            colaPrioridad.remove(solicitud);
        } else {
            throw new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id);
        }
    }

    @Override
    public List<SolicitudServicio> listarTodas() {
        return new ArrayList<>(solicitudes.values());
    }

    @Override
    public SolicitudServicio obtenerSiguienteSolicitud() {
        return colaPrioridad.poll();
    }

    @Override
    public List<SolicitudServicio> buscarPorEstado(EstadoSolicitud estadoSolicitud) {
        return solicitudes.values().stream()
                .filter(s -> s.getEstado() == estadoSolicitud)
                .collect(Collectors.toList());
    }
}
