package com.crecedigital.pe.controller;

import com.crecedigital.pe.enums.Rol;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.InvalidCredentialsException;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.service.IUsuarioService;
import com.crecedigital.pe.view.ConsoleView;

import java.util.ArrayList;
import java.util.List;

public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final ConsoleView view;

    public UsuarioController(IUsuarioService usuarioService, ConsoleView view) {
        this.usuarioService = usuarioService;
        this.view = view;
    }

    public void mostrarMenuUsuarios(Rol rolUsuarioActual) {
        while (true) {
            view.limpiarPantalla();
            view.mostrarTitulo("Gestión de Usuarios");
            int opcion = view.solicitarOpcion(
                    """
                            1. Listar Usuarios
                            2. Crear Usuario
                            3. Modificar Usuario
                            4. Eliminar Usuario
                            5. Cambiar Rol de Usuario
                            6. Listar Usuarios por Rol
                            7. Cambiar Contraseña
                            8. Volver al Menú Principal
                            Seleccione una opción:\s""", 1, 8);

            switch (opcion) {
                case 1: listarUsuarios(); break;
                case 2:
                    if (rolUsuarioActual == Rol.ADMINISTRADOR) {
                        crearUsuario();
                    } else {
                        view.mostrarMensaje("No tiene permisos para esta acción.");
                    }
                    break;
                case 3:
                    if (rolUsuarioActual == Rol.ADMINISTRADOR) {
                        modificarUsuario();
                    } else {
                        view.mostrarMensaje("No tiene permisos para esta acción.");
                    }
                    break;
                case 4:
                    if (rolUsuarioActual == Rol.ADMINISTRADOR) {
                        eliminarUsuario();
                    } else {
                        view.mostrarMensaje("No tiene permisos para esta acción.");
                    }
                    break;
                case 5:
                    if (rolUsuarioActual == Rol.ADMINISTRADOR) {
                        cambiarRolUsuario();
                    } else {
                        view.mostrarMensaje("No tiene permisos para esta acción.");
                    }
                    break;
                case 6: listarUsuariosPorRol(); break;
                case 7: cambiarContrasena(); break;
                case 8: return;
            }
            view.pausar();
        }
    }

    private void listarUsuarios() {
        view.mostrarTitulo("Listado de Usuarios");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        List<String[]> datosTabla = new ArrayList<>();
        for (Usuario u : usuarios) {
            datosTabla.add(new String[]{
                    String.valueOf(u.getId()),
                    u.getNombre(),
                    u.getApellido(),
                    u.getUsername(),
                    u.getRol().toString()
            });
        }
        String[] encabezados = {"ID", "Nombre", "Apellido", "Código", "Rol"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void crearUsuario() {
        String name = view.solicitarInput("Ingrese el nombre de usuario: ");
        String lastname = view.solicitarInput("Ingrese el apellido de usuario: ");
        String username = view.solicitarInput("Ingrese el código de usuario: ");
        String password = view.solicitarInput("Ingrese la contraseña: ");
        Rol rol = solicitarRol();

        try {
            Usuario nuevoUsuario = new Usuario(name, lastname, username, password, rol);
            usuarioService.registrarUsuario(nuevoUsuario);
            view.mostrarMensaje("Usuario creado exitosamente.");
        } catch (IllegalArgumentException e) {
            view.mostrarMensaje("Error al crear usuario: " + e.getMessage());
        }
    }

    private void modificarUsuario() {
        while (true) {
            try {
                Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del usuario a modificar: "));
                Usuario usuario = usuarioService.buscarUsuario(id);

                if (usuario == null) {
                    view.mostrarMensaje("Usuario no encontrado.");
                    if (view.solicitarOpcion("¿Desea intentar con otro ID? (1: Sí, 2: No): ", 1, 2) == 2) {
                        return;
                    }
                    continue;
                }

                view.mostrarMensaje("Detalles actuales del usuario:");
                mostrarDetallesUsuario(usuario);

                if (view.solicitarOpcion("¿Desea proceder con la modificación? (1: Sí, 2: No): ", 1, 2) == 2) {
                    view.mostrarMensaje("Operación cancelada.");
                    return;
                }

                // Solicitar nuevos datos
                String nuevoNombre = view.solicitarInput("Ingrese el nuevo nombre (deje en blanco para no cambiar): ");
                if (!nuevoNombre.isEmpty()) {
                    usuario.setNombre(nuevoNombre);
                }

                String nuevoApellido = view.solicitarInput("Ingrese el nuevo apellido (deje en blanco para no cambiar): ");
                if (!nuevoApellido.isEmpty()) {
                    usuario.setApellido(nuevoApellido);
                }

                String nuevoUsername = view.solicitarInput("Ingrese el nuevo nombre de usuario (deje en blanco para no cambiar): ");
                if (!nuevoUsername.isEmpty()) {
                    usuario.setUsername(nuevoUsername);
                }

                // Cambio de rol (opcional)
                if (view.solicitarOpcion("¿Desea cambiar el rol del usuario? (1: Sí, 2: No): ", 1, 2) == 1) {
                    Rol nuevoRol = solicitarRol();
                    usuario.setRol(nuevoRol);
                }

                // Actualizar usuario
                usuarioService.actualizarUsuario(usuario);
                view.mostrarMensaje("Usuario actualizado exitosamente.");

                // Mostrar los detalles actualizados
                view.mostrarMensaje("Detalles actualizados del usuario:");
                mostrarDetallesUsuario(usuario);

                return;

            } catch (NumberFormatException e) {
                view.mostrarMensaje("Error: ID inválido. Debe ser un número.");
            } catch (EntityNotFoundException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }

            if (view.solicitarOpcion("¿Desea intentar modificar otro usuario? (1: Sí, 2: No): ", 1, 2) == 2) {
                return;
            }
        }
    }

    private void eliminarUsuario() {
        Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del usuario a eliminar: "));
        try {
            usuarioService.eliminarUsuario(id);
            view.mostrarMensaje("Usuario eliminado exitosamente.");
        } catch (EntityNotFoundException e) {
            view.mostrarMensaje("Error: " + e.getMessage());
        }
    }

    private Rol solicitarRol() {
        int opcionRol = view.solicitarOpcion(
                "Seleccione el rol:\n1. Administrador\n2. Inventario\n3. Soporte\nOpción: ", 1, 3);
        return switch (opcionRol) {
            case 1 -> Rol.ADMINISTRADOR;
            case 2 -> Rol.INVENTARIO;
            case 3 -> Rol.SOPORTE;
            default -> throw new IllegalArgumentException("Rol inválido");
        };
    }

    private void cambiarRolUsuario() {
        Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del usuario: "));
        try {
            Usuario usuario = usuarioService.buscarUsuario(id);
            Rol nuevoRol = solicitarRol();
            usuarioService.cambiarRolUsuario(id, nuevoRol);
            view.mostrarMensaje("Rol de usuario actualizado exitosamente.");
        } catch (EntityNotFoundException e) {
            view.mostrarMensaje("Error: " + e.getMessage());
        }
    }

    private void listarUsuariosPorRol() {
        view.mostrarTitulo("Listado de Usuarios por Rol");
        Rol rol = solicitarRol();
        List<Usuario> usuarios = usuarioService.listarUsuariosPorRol(rol);
        if (usuarios.isEmpty()) {
            view.mostrarMensaje("No hay usuarios con el rol " + rol);
        } else {
            List<String[]> datosTabla = new ArrayList<>();
            for (Usuario u : usuarios) {
                datosTabla.add(new String[]{
                        String.valueOf(u.getId()),
                        u.getUsername(),
                        u.getNombreCompleto()
                });
            }
            String[] encabezados = {"ID", "Username", "Nombre Completo"};
            view.mostrarTabla(datosTabla, encabezados);
        }
    }

    private void mostrarDetallesUsuario(Usuario usuario) {
        if (usuario == null) {
            view.mostrarMensaje("Usuario no encontrado.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        datosTabla.add(new String[]{"ID", String.valueOf(usuario.getId())});
        datosTabla.add(new String[]{"Nombre", usuario.getNombre()});
        datosTabla.add(new String[]{"Apellido", usuario.getApellido()});
        datosTabla.add(new String[]{"Nombre de Usuario", usuario.getUsername()});
        datosTabla.add(new String[]{"Rol", usuario.getRol().toString()});

        String[] encabezados = {"Campo", "Valor"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void cambiarContrasena() {
        while (true) {
            try {
                String username = view.solicitarInput("Ingrese el nombre de usuario: ");
                Usuario usuario = usuarioService.buscarPorUsername(username);

                if (usuario == null) {
                    view.mostrarMensaje("Usuario no encontrado.");
                    if (view.solicitarOpcion("¿Desea intentar con otro usuario? (1: Sí, 2: No): ", 1, 2) == 2) {
                        return;
                    }
                    continue;
                }

                view.mostrarMensaje("Detalles del usuario:");
                mostrarDetallesUsuario(usuario);

                if (view.solicitarOpcion("¿Desea proceder con el cambio de contraseña? (1: Sí, 2: No): ", 1, 2) == 2) {
                    view.mostrarMensaje("Operación cancelada.");
                    return;
                }

                String passwordActual = view.solicitarInput("Ingrese su contraseña actual: ");
                String nuevaPassword = view.solicitarInput("Ingrese la nueva contraseña: ");

                usuarioService.cambiarPassword(username, passwordActual, nuevaPassword);
                view.mostrarMensaje("Contraseña cambiada exitosamente.");
                return;

            } catch (InvalidCredentialsException e) {
                view.mostrarMensaje("Error: Contraseña actual incorrecta.");
            } catch (IllegalArgumentException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }

            if (view.solicitarOpcion("¿Desea intentar nuevamente? (1: Sí, 2: No): ", 1, 2) == 2) {
                view.mostrarMensaje("Operación de cambio de contraseña cancelada.");
                return;
            }
        }
    }
}
