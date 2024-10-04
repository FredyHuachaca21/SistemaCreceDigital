package com.crecedigital.pe.controller;

import com.crecedigital.pe.exception.InvalidCredentialsException;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.service.IUsuarioService;
import com.crecedigital.pe.view.ConsoleView;

public class LoginController {
    private final IUsuarioService usuarioService;
    private final ConsoleView view;

    public LoginController(IUsuarioService usuarioService, ConsoleView view) {
        this.usuarioService = usuarioService;
        this.view = view;
    }

    public Usuario login() {
        view.mostrarTitulo("Iniciar Sesión");
        while (true) {
            try {
                String username = view.solicitarInput("Ingrese su código de usuario: ");
                String password = view.solicitarInput("Ingrese su contraseña: ");
                return usuarioService.autenticarUsuario(username, password);
            } catch (InvalidCredentialsException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
                int opcion = view.solicitarOpcion("¿Desea intentar nuevamente? (1: Sí, 2: No): ", 1, 2);
                if (opcion == 2) {
                    return null;
                }
            }
        }
    }
}
