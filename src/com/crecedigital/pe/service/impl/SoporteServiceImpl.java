package com.crecedigital.pe.service.impl;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.enums.Prioridad;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.NoHayTecnicosDisponiblesException;
import com.crecedigital.pe.model.SolicitudServicio;
import com.crecedigital.pe.model.Tecnico;
import com.crecedigital.pe.repository.ISolicitudServicioRepository;
import com.crecedigital.pe.service.ISoporteService;
import com.crecedigital.pe.service.ITecnicoService;

import java.util.*;
import java.util.stream.Collectors;

public class SoporteServiceImpl implements ISoporteService {

    private final ISolicitudServicioRepository solicitudRepository;
    private final ITecnicoService tecnicoService;
    private final PriorityQueue<SolicitudServicio> solicitudesPendientes;
    private final Stack<String> historialCambios;

    public SoporteServiceImpl(ISolicitudServicioRepository solicitudRepository, ITecnicoService tecnicoService) {
        this.solicitudRepository = solicitudRepository;
        this.tecnicoService = tecnicoService;
        this.solicitudesPendientes = new PriorityQueue<>(
                Comparator.comparing(SolicitudServicio::getPrioridad).reversed()
        );
        this.historialCambios = new Stack<>();
    }

    @Override
    public void crearSolicitud(SolicitudServicio solicitud) {
        solicitudRepository.agregar(solicitud);
        solicitudesPendientes.offer(solicitud);
        historialCambios.push("Creada solicitud: " + solicitud.getId());
    }

    @Override
    public void asignarTecnico(Long solicitudId, Long tecnicoId) {
        SolicitudServicio solicitud = buscarSolicitud(solicitudId);
        Tecnico tecnico = tecnicoService.buscarTecnico(tecnicoId);

        solicitud.setTecnicoAsignado(tecnico);
        solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitudRepository.actualizar(solicitud);
        tecnicoService.cambiarDisponibilidadTecnico(tecnicoId, false);

        solicitudesPendientes.remove(solicitud);
        historialCambios.push("Asignado técnico " + tecnicoId + " a solicitud " + solicitudId);
    }

    @Override
    public void actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitud nuevoEstado) {
        SolicitudServicio solicitud = buscarSolicitud(solicitudId);
        solicitud.setEstado(nuevoEstado);
        solicitudRepository.actualizar(solicitud);

        if (nuevoEstado == EstadoSolicitud.COMPLETADA || nuevoEstado == EstadoSolicitud.CANCELADA) {
            Tecnico tecnico = solicitud.getTecnicoAsignado();
            if (tecnico != null) {
                tecnicoService.cambiarDisponibilidadTecnico(tecnico.getId(), true);
            }
            solicitudesPendientes.remove(solicitud);
        }

        historialCambios.push("Actualizado estado de solicitud " + solicitudId + " a " + nuevoEstado);
    }

    @Override
    public List<SolicitudServicio> listarSolicitudesPendientes() {
        return new ArrayList<>(solicitudesPendientes);
    }

    @Override
    public String generarReporteServicios() {
        List<SolicitudServicio> solicitudes = listarSolicitudes();
        StringBuilder reporte = new StringBuilder("Reporte de Servicios:\n");
        generarReporteRecursivo(solicitudes, 0, reporte);
        return reporte.toString();
    }

    private void generarReporteRecursivo(List<SolicitudServicio> solicitudes, int index, StringBuilder reporte) {
        if (index >= solicitudes.size()) {
            return;
        }

        SolicitudServicio solicitud = solicitudes.get(index);
        reporte.append(String.format("ID: %d, Estado: %s, Prioridad: %s, Técnico: %s\n",
                solicitud.getId(),
                solicitud.getEstado(),
                solicitud.getPrioridad(),
                solicitud.getTecnicoAsignado() != null ? solicitud.getTecnicoAsignado().getNombre() : "No asignado"));

        generarReporteRecursivo(solicitudes, index + 1, reporte);
    }

    @Override
    public SolicitudServicio obtenerSiguienteSolicitud() {
        return solicitudesPendientes.poll();
    }

    @Override
    public List<SolicitudServicio> listarSolicitudes() {
        return solicitudRepository.listarTodas();
    }

    @Override
    public List<Tecnico> listarTecnicos() {
        return tecnicoService.listarTecnicos();
    }

    @Override
    public List<Tecnico> obtenerTecnicosDisponibles() {
        return tecnicoService.obtenerTecnicosDisponibles();
    }

    @Override
    public String obtenerHistorialCambios() {
        StringBuilder historial = new StringBuilder("Historial de Cambios:\n");
        for (String cambio : historialCambios) {
            historial.append(cambio).append("\n");
        }
        return historial.toString();
    }

    @Override
    public SolicitudServicio buscarSolicitud(Long solicitudId) {
        return solicitudRepository.buscarPorId(solicitudId);
    }

    @Override
    public void asignarTecnicoAutomaticamente(Long solicitudId) {
        SolicitudServicio solicitud = buscarSolicitud(solicitudId);
        if (solicitud == null) {
            throw new EntityNotFoundException("Solicitud no encontrada con ID: " + solicitudId);
        }

        Tecnico tecnicoAsignado = buscarTecnicoDisponible(solicitud.getEspecialidadRequerida());

        if (tecnicoAsignado == null && (solicitud.getPrioridad() == Prioridad.ALTA || solicitud.getPrioridad() == Prioridad.CRITICA)) {
            tecnicoAsignado = buscarTecnicoReasignable(solicitud.getEspecialidadRequerida(), solicitud.getPrioridad());
        }

        if (tecnicoAsignado != null) {
            asignarTecnico(solicitud, tecnicoAsignado);
        } else {
            solicitudesPendientes.offer(solicitud);
            throw new NoHayTecnicosDisponiblesException("No hay técnicos disponibles para la solicitud. Se ha puesto en cola de espera.");
        }
    }

    @Override
    public List<SolicitudServicio> listarSolicitudesPorPrioridad() {
        List<SolicitudServicio> todasLasSolicitudes = solicitudRepository.listarTodas();
        return todasLasSolicitudes.stream()
                .sorted(Comparator.comparing(SolicitudServicio::getPrioridad).reversed())
                .collect(Collectors.toList());
    }


    private Tecnico buscarTecnicoDisponible(Especialidad especialidadRequerida) {
        return tecnicoService.obtenerTecnicosDisponibles().stream()
                .filter(t -> t.getEspecialidad() == especialidadRequerida)
                .findFirst()
                .orElse(null);
    }

    private Tecnico buscarTecnicoReasignable(Especialidad especialidadRequerida, Prioridad prioridadSolicitud) {
        List<SolicitudServicio> solicitudesEnProceso = solicitudRepository.buscarPorEstado(EstadoSolicitud.EN_PROCESO);
        Optional<SolicitudServicio> solicitudAReasignar = solicitudesEnProceso.stream()
                .filter(s -> s.getPrioridad().ordinal() < prioridadSolicitud.ordinal()
                        && s.getTecnicoAsignado().getEspecialidad() == especialidadRequerida)
                .min(Comparator.comparing(SolicitudServicio::getPrioridad));

        if (solicitudAReasignar.isPresent()) {
            SolicitudServicio solicitud = solicitudAReasignar.get();
            Tecnico tecnico = solicitud.getTecnicoAsignado();
            reasignarSolicitud(solicitud);
            return tecnico;
        }
        return null;
    }

    private void reasignarSolicitud(SolicitudServicio solicitud) {
        Tecnico tecnico = solicitud.getTecnicoAsignado();
        solicitud.setTecnicoAsignado(null);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitudRepository.actualizar(solicitud);
        solicitudesPendientes.offer(solicitud);
        // Técnico disponible nuevamente
        tecnico.setDisponible(true);
        tecnicoService.actualizar(tecnico);
    }

    private void asignarTecnico(SolicitudServicio solicitud, Tecnico tecnico) {
        solicitud.setTecnicoAsignado(tecnico);
        solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitudRepository.actualizar(solicitud);
        tecnicoService.cambiarDisponibilidadTecnico(tecnico.getId(), false);
        historialCambios.push("Asignado técnico " + tecnico.getId() + " a solicitud " + solicitud.getId());
    }

    private Tecnico buscarTecnicoDisponibleRecursivo(List<Tecnico> tecnicos, int index, Especialidad especialidadRequerida) {
        if (index >= tecnicos.size()) {
            return null; // No se encontró ningún técnico disponible
        }

        Tecnico tecnico = tecnicos.get(index);
        if (tecnico.isDisponible() && tecnico.getEspecialidad() == especialidadRequerida) {
            return tecnico;
        }

        return buscarTecnicoDisponibleRecursivo(tecnicos, index + 1, especialidadRequerida);
    }
}
