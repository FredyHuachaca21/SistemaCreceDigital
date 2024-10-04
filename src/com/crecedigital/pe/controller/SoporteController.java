package com.crecedigital.pe.controller;

import com.crecedigital.pe.enums.Especialidad;
import com.crecedigital.pe.enums.EstadoSolicitud;
import com.crecedigital.pe.enums.Prioridad;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.NoHayTecnicosDisponiblesException;
import com.crecedigital.pe.model.SolicitudServicio;
import com.crecedigital.pe.model.Tecnico;
import com.crecedigital.pe.service.ISoporteService;
import com.crecedigital.pe.view.ConsoleView;

import java.util.ArrayList;
import java.util.List;

public class SoporteController {
    private final ISoporteService soporteService;
    private final ConsoleView view;

    public SoporteController(ISoporteService soporteService, ConsoleView view) {
        this.soporteService = soporteService;
        this.view = view;
    }

    public void mostrarMenuSoporte() {
        while (true) {
            view.limpiarPantalla();
            view.mostrarTitulo("GESTIÓN DE SOPORTE");
            int opcion = view.solicitarOpcion(
                    """
                    1. Listar Solicitudes
                    2. Crear Solicitud
                    3. Asignar Técnico
                    4. Asignar Técnico Automáticamente
                    5. Actualizar Estado de Solicitud
                    6. Listar Técnicos
                    7. Ver Técnicos Disponibles
                    8. Generar Reporte de Servicios
                    9. Ver Historial de Cambios
                    10. Volver al Menú Principal
                    Seleccione una opción:\s""", 1, 10);

            switch (opcion) {
                case 1: listarSolicitudes(); break;
                case 2: crearSolicitud(); break;
                case 3: asignarTecnico(); break;
                case 4: asignarTecnicoAutomaticamente(); break;
                case 5: actualizarEstadoSolicitud(); break;
                case 6: listarTecnicos(); break;
                case 7: verTecnicosDisponibles(); break;
                case 8: generarReporteServicios(); break;
                case 9: verHistorialCambios(); break;
                case 10: return;
            }
            view.pausar();
        }
    }

    private void listarSolicitudes() {
        view.mostrarTitulo("Lista de Solicitudes");
        List<SolicitudServicio> solicitudes = soporteService.listarSolicitudes();
        if (solicitudes.isEmpty()) {
            view.mostrarMensaje("No hay solicitudes registradas.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        for (SolicitudServicio solicitud : solicitudes) {
            datosTabla.add(new String[]{
                    String.valueOf(solicitud.getId()),
                    solicitud.getDescripcion(),
                    solicitud.getEstado().toString(),
                    solicitud.getPrioridad().toString(),
                    solicitud.getTecnicoAsignado() != null ? solicitud.getTecnicoAsignado().getNombre() : "No asignado"
            });
        }

        String[] encabezados = {"ID", "Descripción", "Estado", "Prioridad", "Técnico"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void crearSolicitud() {
        String descripcion = view.solicitarInput("Ingrese la descripción de la solicitud: ");
        Prioridad prioridad = solicitarPrioridad();
        Especialidad especialidad = solicitarEspecialidad();
        SolicitudServicio nuevaSolicitud = new SolicitudServicio(
                descripcion,
                prioridad,
                EstadoSolicitud.PENDIENTE,
                especialidad
        );
        soporteService.crearSolicitud(nuevaSolicitud);
        view.mostrarMensaje("Solicitud creada exitosamente con ID: " + nuevaSolicitud.getId());
    }

    private Especialidad solicitarEspecialidad() {
        view.mostrarMensaje("Seleccione la especialidad requerida:");
        for (int i = 0; i < Especialidad.values().length; i++) {
            view.mostrarMensaje((i + 1) + ". " + Especialidad.values()[i]);
        }
        int opcion = view.solicitarOpcion("Opción: ", 1, Especialidad.values().length);
        return Especialidad.values()[opcion - 1];
    }

    private void asignarTecnico() {
        try {
            Long solicitudId = Long.parseLong(view.solicitarInput("Ingrese el ID de la solicitud: "));

            // Mostrar la lista de técnicos disponibles
            List<Tecnico> tecnicosDisponibles = soporteService.obtenerTecnicosDisponibles();
            if (tecnicosDisponibles.isEmpty()) {
                view.mostrarMensaje("No hay técnicos disponibles en este momento.");
                return;
            }

            view.mostrarMensaje("=== TÉCNICOS DISPONIBLES ===");
            for (Tecnico tecnico : tecnicosDisponibles) {
                view.mostrarMensaje(String.format("ID: %d, Nombre: %s %s, Especialidad: %s",
                        tecnico.getId(),
                        tecnico.getNombre(),
                        tecnico.getApellido(),
                        tecnico.getEspecialidad()));
            }

            Long tecnicoId = Long.parseLong(view.solicitarInput("Ingrese el ID del técnico a asignar: "));

            soporteService.asignarTecnico(solicitudId, tecnicoId);
            view.mostrarMensaje("Técnico asignado exitosamente a la solicitud.");
        } catch (NumberFormatException e) {
            view.mostrarMensaje("Error: Por favor, ingrese un número válido para el ID.");
        } catch (EntityNotFoundException e) {
            view.mostrarMensaje("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarMensaje("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarMensaje("Error inesperado al asignar técnico: " + e.getMessage());
        }
    }

    private void actualizarEstadoSolicitud() {
        try {
            Long solicitudId = Long.parseLong(view.solicitarInput("Ingrese el ID de la solicitud: "));
            SolicitudServicio solicitud = soporteService.buscarSolicitud(solicitudId);

            if (solicitud == null) {
                view.mostrarMensaje("No se encontró una solicitud con el ID proporcionado.");
                return;
            }

            view.mostrarMensaje("Estado actual: " + view.colorearTextoVerde(solicitud.getEstado().toString()));

            List<EstadoSolicitud> estadosDisponibles = obtenerEstadosDisponibles(solicitud.getEstado());

            if (estadosDisponibles.isEmpty()) {
                view.mostrarMensaje("No hay estados disponibles para cambiar desde el estado actual.");
                return;
            }

            view.mostrarMensaje("Seleccione el nuevo estado:");
            for (int i = 0; i < estadosDisponibles.size(); i++) {
                view.mostrarMensaje((i + 1) + ". " + estadosDisponibles.get(i));
            }

            int opcion = view.solicitarOpcion("Opción: ", 1, estadosDisponibles.size());
            EstadoSolicitud nuevoEstado = estadosDisponibles.get(opcion - 1);

            soporteService.actualizarEstadoSolicitud(solicitudId, nuevoEstado);
            view.mostrarMensaje("Estado de la solicitud actualizado exitosamente a " +
                    view.colorearTextoVerde(nuevoEstado.toString()));
        } catch (NumberFormatException e) {
            view.mostrarMensaje("Error: Ingrese un número válido para el ID.");
        } catch (Exception e) {
            view.mostrarMensaje("Error al actualizar el estado: " + e.getMessage());
        }
    }

    private List<EstadoSolicitud> obtenerEstadosDisponibles(EstadoSolicitud estadoActual) {
        List<EstadoSolicitud> estadosDisponibles = new ArrayList<>();
        switch (estadoActual) {
            case PENDIENTE:
                estadosDisponibles.add(EstadoSolicitud.EN_PROCESO);
                estadosDisponibles.add(EstadoSolicitud.CANCELADA);
                break;
            case EN_PROCESO:
                estadosDisponibles.add(EstadoSolicitud.COMPLETADA);
                estadosDisponibles.add(EstadoSolicitud.CANCELADA);
                break;
            case COMPLETADA:
            case CANCELADA:
                // No se permiten cambios desde estos estados
                break;
        }
        return estadosDisponibles;
    }

    private void listarTecnicos() {
        view.mostrarTitulo("Lista de Técnicos");
        List<Tecnico> tecnicos = soporteService.listarTecnicos();
        if (tecnicos.isEmpty()) {
            view.mostrarMensaje("No hay técnicos registrados.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        for (Tecnico tecnico : tecnicos) {
            datosTabla.add(new String[]{
                    String.valueOf(tecnico.getId()),
                    tecnico.getNombre() + " " + tecnico.getApellido(),
                    tecnico.getEspecialidad().toString(),
                    tecnico.isDisponible() ? "Sí" : "No"
            });
        }

        String[] encabezados = {"ID", "Nombre", "Especialidad", "Disponible"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void verTecnicosDisponibles() {
        List<Tecnico> tecnicosDisponibles = soporteService.obtenerTecnicosDisponibles();
        if (tecnicosDisponibles.isEmpty()) {
            view.mostrarMensaje("No hay técnicos disponibles en este momento.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        for (Tecnico tecnico : tecnicosDisponibles) {
            datosTabla.add(new String[]{
                    String.valueOf(tecnico.getId()),
                    tecnico.getNombre() + " " + tecnico.getApellido(),
                    tecnico.getEspecialidad().toString()
            });
        }

        String[] encabezados = {"ID", "Nombre", "Especialidad"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void generarReporteServicios() {
        view.mostrarTitulo("Reporte de Servicios");
        List<SolicitudServicio> solicitudes = soporteService.listarSolicitudes();
        List<String[]> datosTabla = new ArrayList<>();
        for (SolicitudServicio solicitud : solicitudes) {
            datosTabla.add(new String[]{
                    String.valueOf(solicitud.getId()),
                    solicitud.getDescripcion(),
                    solicitud.getEstado().toString(),
                    solicitud.getPrioridad().toString(),
                    solicitud.getTecnicoAsignado() != null ? solicitud.getTecnicoAsignado().getNombre() : "No asignado",
                    solicitud.getFechaCreacion() != null ? solicitud.getFechaCreacion().toString() : "N/A",
                    solicitud.getFechaActualizacion() != null ? solicitud.getFechaActualizacion().toString() : "N/A"
            });
        }

        String[] encabezados = {"ID", "Descripción", "Estado", "Prioridad", "Técnico", "Fecha Creación", "Última Actualización"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void verHistorialCambios() {
        String historial = soporteService.obtenerHistorialCambios();
        view.mostrarMensaje("=== HISTORIAL DE CAMBIOS ===");
        view.mostrarMensaje(historial);
    }

    private Prioridad solicitarPrioridad() {
        int opcion = view.solicitarOpcion(
                "Seleccione la prioridad:\n1. Baja\n2. Media\n3. Alta\n4. Crítica\nOpción: ", 1, 4);
        return switch (opcion) {
            case 1 -> Prioridad.BAJA;
            case 2 -> Prioridad.MEDIA;
            case 3 -> Prioridad.ALTA;
            case 4 -> Prioridad.CRITICA;
            default -> throw new IllegalArgumentException("Opción inválida");
        };
    }
    private void asignarTecnicoAutomaticamente() {
        try {
            Long solicitudId = Long.parseLong(view.solicitarInput("Ingrese el ID de la solicitud: "));
            soporteService.asignarTecnicoAutomaticamente(solicitudId);
            view.mostrarMensaje("Técnico asignado automáticamente a la solicitud.");
        } catch (NumberFormatException e) {
            view.mostrarMensaje("Error: Por favor, ingrese un número válido para el ID.");
        } catch (EntityNotFoundException e) {
            view.mostrarMensaje("Error: Solicitud no encontrada. " + e.getMessage());
        } catch (NoHayTecnicosDisponiblesException e) {
            view.mostrarMensaje("Aviso: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarMensaje("Error inesperado al asignar técnico automáticamente: " + e.getMessage());
        }
    }

    private EstadoSolicitud solicitarEstadoSolicitud() {
        int opcion = view.solicitarOpcion(
                "Seleccione el nuevo estado:\n1. Pendiente\n2. En Proceso\n3. Completada\n4. Cancelada\nOpción: ", 1, 4);
        return switch (opcion) {
            case 1 -> EstadoSolicitud.PENDIENTE;
            case 2 -> EstadoSolicitud.EN_PROCESO;
            case 3 -> EstadoSolicitud.COMPLETADA;
            case 4 -> EstadoSolicitud.CANCELADA;
            default -> throw new IllegalArgumentException("Opción inválida");
        };
    }
}
