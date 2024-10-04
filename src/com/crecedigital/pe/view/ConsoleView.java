package com.crecedigital.pe.view;

import com.crecedigital.pe.enums.Rol;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";

    public void mostrarTitulo(String titulo) {
        System.out.println(ANSI_BLUE + "\n=== " + titulo.toUpperCase() + " ===" + ANSI_RESET);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(ANSI_GREEN + mensaje + ANSI_RESET);
    }

    public String solicitarInput(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    public int solicitarOpcion(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print(mensaje);
                int opcion = Integer.parseInt(scanner.nextLine());
                if (opcion >= min && opcion <= max) {
                    return opcion;
                } else {
                    System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    public int mostrarMenuPrincipal(Rol rol) {
        limpiarPantalla();
        mostrarTitulo("MENÚ PRINCIPAL");
        int opcion = 1;

        if (rol == Rol.ADMINISTRADOR) {
            System.out.println(opcion + ". Gestión de Usuarios");
            opcion++;
        }

        if (rol == Rol.ADMINISTRADOR || rol == Rol.INVENTARIO) {
            System.out.println(opcion + ". Gestión de Inventario");
            opcion++;
        }

        if (rol == Rol.ADMINISTRADOR || rol == Rol.SOPORTE) {
            System.out.println(opcion + ". Gestión de Soporte");
            opcion++;
        }

        System.out.println(opcion + ". Cerrar Sesión");
        opcion++;
        System.out.println(opcion + ". Salir del Sistema");

        return solicitarOpcion("Seleccione una opción: ", 1, opcion);
    }

    public void mostrarTabla(List<String[]> datos, String[] encabezados) {
        if (datos.isEmpty() || encabezados.length == 0) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        // Calcular el ancho máximo de cada columna
        int[] anchos = new int[encabezados.length];
        for (int i = 0; i < encabezados.length; i++) {
            anchos[i] = encabezados[i].length();
            for (String[] fila : datos) {
                if (i < fila.length) {
                    anchos[i] = Math.max(anchos[i], fila[i].length());
                }
            }
        }

        // Imprimir encabezados
        imprimirLineaDivisoria(anchos);
        for (int i = 0; i < encabezados.length; i++) {
            System.out.printf(ANSI_BLUE + "| %-" + anchos[i] + "s " + ANSI_RESET, encabezados[i]);
        }
        System.out.println("|");
        imprimirLineaDivisoria(anchos);

        // Imprimir datos
        for (String[] fila : datos) {
            for (int i = 0; i < encabezados.length; i++) {
                System.out.printf("| %-" + anchos[i] + "s ", i < fila.length ? fila[i] : "");
            }
            System.out.println("|");
        }
        imprimirLineaDivisoria(anchos);
    }

    private void imprimirLineaDivisoria(int[] anchos) {
        for (int ancho : anchos) {
            System.out.print("+-" + "-".repeat(ancho) + "-");
        }
        System.out.println("+");
    }

    public void limpiarPantalla() {
        System.out.println(">>>".repeat(25));
    }

    public void pausar() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    public String colorearTextoVerde(String texto) {
        return ANSI_GREEN + texto + ANSI_RESET;
    }

    public String colorearTextoAzul(String texto) {
        return ANSI_BLUE + texto + ANSI_RESET;
    }


}
