package com.crecedigital.pe.home;

import com.crecedigital.pe.controller.InventarioController;
import com.crecedigital.pe.controller.LoginController;
import com.crecedigital.pe.controller.SoporteController;
import com.crecedigital.pe.controller.UsuarioController;
import com.crecedigital.pe.data.DataInitializer;
import com.crecedigital.pe.enums.Rol;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.repository.IProductoRepository;
import com.crecedigital.pe.repository.ISolicitudServicioRepository;
import com.crecedigital.pe.repository.ITecnicoRepository;
import com.crecedigital.pe.repository.IUsuarioRepository;
import com.crecedigital.pe.repository.impl.ProductoRepositoryImpl;
import com.crecedigital.pe.repository.impl.SolicitudServicioRepositoryImpl;
import com.crecedigital.pe.repository.impl.TecnicoRepositoryImpl;
import com.crecedigital.pe.repository.impl.UsuarioRepositoryImpl;
import com.crecedigital.pe.service.IInventarioService;
import com.crecedigital.pe.service.ISoporteService;
import com.crecedigital.pe.service.ITecnicoService;
import com.crecedigital.pe.service.IUsuarioService;
import com.crecedigital.pe.service.impl.InventarioServiceImpl;
import com.crecedigital.pe.service.impl.SoporteServiceImpl;
import com.crecedigital.pe.service.impl.TecnicoServiceImpl;
import com.crecedigital.pe.service.impl.UsuarioServiceImpl;
import com.crecedigital.pe.view.ConsoleView;

import java.nio.file.Files;
import java.nio.file.Path;

public class Principal {
    public static void main(String[] args) {
        // Inicializar repositorios
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryImpl();
        IProductoRepository productoRepository = new ProductoRepositoryImpl();
        ISolicitudServicioRepository solicitudRepository = new SolicitudServicioRepositoryImpl();
        ITecnicoRepository tecnicoRepository = new TecnicoRepositoryImpl();

        // Inicializar datos de prueba
        DataInitializer dataInitializer = new DataInitializer(
                usuarioRepository,
                productoRepository,
                solicitudRepository,
                tecnicoRepository
        );
        dataInitializer.initializeData();

        // Inicializar servicios
        IUsuarioService usuarioService = new UsuarioServiceImpl(usuarioRepository);
        IInventarioService inventarioService = new InventarioServiceImpl(productoRepository);
        ITecnicoService tecnicoService = new TecnicoServiceImpl(tecnicoRepository);
        ISoporteService soporteService = new SoporteServiceImpl(solicitudRepository, tecnicoService);

        // Inicializar vista
        ConsoleView view = new ConsoleView();

        // Mostrar título principal
        mostrarTituloPrincipal();

        // Inicializar controladores
        LoginController loginController = new LoginController(usuarioService, view);
        UsuarioController usuarioController = new UsuarioController(usuarioService, view);
        InventarioController inventarioController = new InventarioController(inventarioService, view);
        SoporteController soporteController = new SoporteController(soporteService, view);

        // Lógica principal del programa
        boolean ejecutarSistema = true;
        while (ejecutarSistema) {
            Usuario usuarioActual = null;
            while (usuarioActual == null) {
                view.limpiarPantalla();
                usuarioActual = loginController.login();
                if (usuarioActual == null) {
                    view.mostrarMensaje("Credenciales inválidas. Intente nuevamente.");
                }
            }

            view.mostrarMensaje("Bienvenido, " + usuarioActual.getNombreCompleto() + " ==> tiene el rol de " + usuarioActual.getRol());

            boolean sesionActiva = true;
            while (sesionActiva) {
                int opcion = view.mostrarMenuPrincipal(usuarioActual.getRol());
                switch (opcion) {
                    case 1:
                        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
                            usuarioController.mostrarMenuUsuarios(usuarioActual.getRol());
                        } else if (usuarioActual.getRol() == Rol.INVENTARIO) {
                            inventarioController.mostrarMenuInventario();
                        } else if (usuarioActual.getRol() == Rol.SOPORTE) {
                            soporteController.mostrarMenuSoporte();
                        }
                        break;
                    case 2:
                        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
                            inventarioController.mostrarMenuInventario();
                        } else {
                            sesionActiva = false; // Cerrar sesión para roles no administradores
                        }
                        break;
                    case 3:
                        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
                            soporteController.mostrarMenuSoporte();
                        } else {
                            if (confirmarSalida(view)) {
                                sesionActiva = false;
                                ejecutarSistema = false;
                            }
                        }
                        break;
                    case 4:
                        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
                            sesionActiva = false; // Cerrar sesión para administrador
                        }
                        break;
                    case 5:
                        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
                            if (confirmarSalida(view)) {
                                sesionActiva = false;
                                ejecutarSistema = false;
                            }
                        }
                        break;
                }
            }

            if (ejecutarSistema) {
                view.mostrarMensaje("Sesión cerrada.");
            }
        }

        view.mostrarMensaje("Gracias por usar el sistema. ¡Hasta luego!");
    }

    private static boolean confirmarSalida(ConsoleView view) {
        view.mostrarMensaje("ADVERTENCIA: Si sale del sistema, se perderán todos los cambios no guardados.");
        int opcion = view.solicitarOpcion("¿Está seguro que desea salir del sistema? (1: Sí, 2: No): ", 1, 2);
        return opcion == 1;
    }

    private static void mostrarTituloPrincipal() {
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_BLUE);

        String banner = leerBannerDesdeArchivo();
        System.out.println(banner);

        System.out.println(ANSI_RESET);
        System.out.println("Bienvenido al Sistema de Gestión de Crece Digital");
        System.out.println("Versión 1.0");
        System.out.println("\nDesarrollado por Grupo 1");
        System.out.println("\nCargando sistema...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String leerBannerDesdeArchivo() {
        try {
            var url = Principal.class.getResource("/resources/banner.txt");
            if (url == null) {
                System.err.println("No se pudo encontrar el archivo banner.txt");
                return "Crece Digital";
            }
            return Files.readString(Path.of(url.toURI()));
        } catch (Exception e) {
            System.err.println("Error al leer el banner: " + e.getMessage());
            e.printStackTrace();
            return "Crece Digital";
        }
    }
}
