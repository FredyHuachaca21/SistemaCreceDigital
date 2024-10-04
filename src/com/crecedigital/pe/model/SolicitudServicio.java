package com.crecedigital.pe.model;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.enums.Prioridad;

import java.time.LocalDateTime;

public class SolicitudServicio {

    private Long id;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private Tecnico tecnicoAsignado;
    private Especialidad especialidadRequerida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public SolicitudServicio(String descripcion, Prioridad prioridad, EstadoSolicitud estado, Especialidad especialidadRequerida) {
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.especialidadRequerida = especialidadRequerida;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Tecnico getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(Tecnico tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }

    public Especialidad getEspecialidadRequerida() {
        return especialidadRequerida;
    }

    public void setEspecialidadRequerida(Especialidad especialidadRequerida) {
        this.especialidadRequerida = especialidadRequerida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
