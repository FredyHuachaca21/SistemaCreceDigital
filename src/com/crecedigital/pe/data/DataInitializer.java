package com.crecedigital.pe.data;

import com.crecedigital.pe.enums.*;
import com.crecedigital.pe.model.Producto;
import com.crecedigital.pe.model.SolicitudServicio;
import com.crecedigital.pe.model.Tecnico;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.repository.IProductoRepository;
import com.crecedigital.pe.repository.ISolicitudServicioRepository;
import com.crecedigital.pe.repository.ITecnicoRepository;
import com.crecedigital.pe.repository.IUsuarioRepository;

import java.math.BigDecimal;
import java.util.List;

public class DataInitializer {
    private final IUsuarioRepository usuarioRepository;
    private final IProductoRepository productoRepository;
    private final ISolicitudServicioRepository solicitudServicioRepository;
    private final ITecnicoRepository tecnicoRepository;

    public DataInitializer(IUsuarioRepository usuarioRepository,
                           IProductoRepository productoRepository,
                           ISolicitudServicioRepository solicitudServicioRepository,
                           ITecnicoRepository tecnicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.solicitudServicioRepository = solicitudServicioRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public void initializeData() {
        initializeUsuarios();
        initializeProductos();
        initializeTecnicos();
        initializeSolicitudesServicio();
    }

    private void initializeUsuarios() {
        usuarioRepository.agregar(new Usuario("Fredy", "Huachaca", "admin", "admin123", Rol.ADMINISTRADOR));
        usuarioRepository.agregar(new Usuario("Luis", "Ayala", "inventario", "inv123", Rol.INVENTARIO));
        usuarioRepository.agregar(new Usuario("Alvaro", "Prado", "soporte", "sop123", Rol.SOPORTE));
    }

    private void initializeProductos() {
        productoRepository.agregar(new Producto("LAP001", "Laptop HP", new BigDecimal("899.99"), 10, EstadoProducto.ACTIVO));
        productoRepository.agregar(new Producto("CEL001", "Smartphone Samsung", new BigDecimal("599.99"), 15, EstadoProducto.ACTIVO));
        productoRepository.agregar(new Producto("TAB001", "Tablet Apple", new BigDecimal("499.99"), 8, EstadoProducto.ACTIVO));
    }

    private void initializeTecnicos() {
        tecnicoRepository.agregar(new Tecnico("Juan", "Pérez", "juan.perez@gmail.com", Especialidad.HARDWARE));
        tecnicoRepository.agregar(new Tecnico("María", "García", "maria.garcia@gmail.com", Especialidad.SOFTWARE));
        tecnicoRepository.agregar(new Tecnico("Carlos", "Rodríguez", "carlos.rodriguez@gmail.com", Especialidad.REDES));
        tecnicoRepository.agregar(new Tecnico("Ana", "Martínez", "ana.martinez@gmail.com", Especialidad.SEGURIDAD));
        tecnicoRepository.agregar(new Tecnico("Luis", "Sánchez", "luis.sanchez@gmail.com", Especialidad.BASES_DE_DATOS));
    }

    private void initializeSolicitudesServicio() {
        List<Tecnico> tecnicos = tecnicoRepository.listarTodos();

        SolicitudServicio solicitud1 = new SolicitudServicio(
                "Problema con laptop",
                Prioridad.ALTA,
                EstadoSolicitud.PENDIENTE,
                Especialidad.HARDWARE
        );
        solicitud1.setTecnicoAsignado(tecnicos.getFirst());
        solicitudServicioRepository.agregar(solicitud1);

        SolicitudServicio solicitud2 = new SolicitudServicio(
                "Actualización de software",
                Prioridad.MEDIA,
                EstadoSolicitud.PENDIENTE,
                Especialidad.SOFTWARE
        );
        solicitud2.setTecnicoAsignado(tecnicos.get(1));
        solicitudServicioRepository.agregar(solicitud2);

        SolicitudServicio solicitud3 = new SolicitudServicio(
                "Configuración de red",
                Prioridad.BAJA,
                EstadoSolicitud.PENDIENTE,
                Especialidad.REDES
        );
        solicitudServicioRepository.agregar(solicitud3);

        // Actualiza la disponibilidad de los técnicos asignados
        tecnicos.get(0).setDisponible(false);
        tecnicoRepository.actualizar(tecnicos.get(0));
        tecnicos.get(1).setDisponible(false);
        tecnicoRepository.actualizar(tecnicos.get(1));
    }
}