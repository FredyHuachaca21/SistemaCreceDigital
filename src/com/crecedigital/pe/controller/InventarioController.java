package com.crecedigital.pe.controller;

import com.crecedigital.pe.enums.EstadoProducto;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.InsufficientStockException;
import com.crecedigital.pe.model.Producto;
import com.crecedigital.pe.service.IInventarioService;
import com.crecedigital.pe.view.ConsoleView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InventarioController {

    private final IInventarioService inventarioService;
    private final ConsoleView view;

    public InventarioController(IInventarioService inventarioService, ConsoleView view) {
        this.inventarioService = inventarioService;
        this.view = view;
    }

    public void mostrarMenuInventario() {
        while (true) {
            view.limpiarPantalla();
            view.mostrarMensaje("=== GESTIÓN DE INVENTARIO ===");
            int opcion = view.solicitarOpcion(
                    """
                            1. Listar Productos
                            2. Agregar Producto
                            3. Modificar Producto
                            4. Eliminar Producto
                            5. Buscar Producto
                            6. Actualizar Stock
                            7. Generar Reporte de Inventario
                            8. Volver al Menú Principal
                            Seleccione una opción:\s""", 1, 8);

            switch (opcion) {
                case 1: listarProductos(); break;
                case 2: agregarProducto(); break;
                case 3: modificarProducto(); break;
                case 4: eliminarProducto(); break;
                case 5: buscarProducto(); break;
                case 6: actualizarStock(); break;
                case 7: generarReporteInventario(); break;
                case 8: return;
            }
            view.pausar();
        }
    }

    private void listarProductos() {
        view.mostrarTitulo("LISTADO DE PRODUCTOS");
        List<Producto> productos = inventarioService.listarProductos();
        mostrarProductosEnTabla(productos);
    }

    private void mostrarProductosEnTabla(List<Producto> productos) {
        if (productos.isEmpty()) {
            view.mostrarMensaje("No hay productos para mostrar.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        for (Producto p : productos) {
            datosTabla.add(new String[]{
                    String.valueOf(p.getId()),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getPrecio().toString(),
                    String.valueOf(p.getCantidad()),
                    p.getEstado().toString()
            });
        }
        String[] encabezados = {"ID", "Código", "Nombre", "Precio", "Cantidad", "Estado"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void mostrarDetallesProducto(Producto producto) {
        if (producto == null) {
            view.mostrarMensaje("Producto no encontrado.");
            return;
        }

        List<String[]> datosTabla = new ArrayList<>();
        datosTabla.add(new String[]{"ID", String.valueOf(producto.getId())});
        datosTabla.add(new String[]{"Código", producto.getCodigo()});
        datosTabla.add(new String[]{"Nombre", producto.getNombre()});
        datosTabla.add(new String[]{"Precio", producto.getPrecio().toString()});
        datosTabla.add(new String[]{"Cantidad", String.valueOf(producto.getCantidad())});
        datosTabla.add(new String[]{"Estado", producto.getEstado().toString()});

        String[] encabezados = {"Campo", "Valor"};
        view.mostrarTabla(datosTabla, encabezados);
    }

    private void agregarProducto() {
        String codigo = view.solicitarInput("Ingrese el código del producto: ");
        String nombre = view.solicitarInput("Ingrese el nombre del producto: ");
        BigDecimal precio = new BigDecimal(view.solicitarInput("Ingrese el precio del producto: "));
        int cantidad = Integer.parseInt(view.solicitarInput("Ingrese la cantidad del producto: "));
        EstadoProducto estado = solicitarEstadoProducto();

        Producto nuevoProducto = new Producto(codigo, nombre, precio, cantidad, estado);
        inventarioService.agregarProducto(nuevoProducto);
        view.mostrarMensaje("Producto agregado exitosamente.");
    }

    private void modificarProducto() {
        while (true) {
            try {
                Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del producto a modificar: "));
                Producto producto = inventarioService.buscarProducto(id);

                if (producto == null) {
                    view.mostrarMensaje("Producto no encontrado.");
                    if (view.solicitarOpcion("¿Desea intentar con otro ID? (1: Sí, 2: No): ", 1, 2) == 2) {
                        return;
                    }
                    continue;
                }

                view.mostrarMensaje("Detalles actuales del producto:");
                mostrarDetallesProducto(producto);

                if (view.solicitarOpcion("¿Desea proceder con la modificación? (1: Sí, 2: No): ", 1, 2) == 2) {
                    view.mostrarMensaje("Operación cancelada.");
                    return;
                }

                String nuevoCodigo = view.solicitarInput("Ingrese el nuevo código (deje en blanco para no cambiar): ");
                if (!nuevoCodigo.isEmpty()) producto.setCodigo(nuevoCodigo);

                String nuevoNombre = view.solicitarInput("Ingrese el nuevo nombre (deje en blanco para no cambiar): ");
                if (!nuevoNombre.isEmpty()) producto.setNombre(nuevoNombre);

                String nuevoPrecio = view.solicitarInput("Ingrese el nuevo precio (deje en blanco para no cambiar): ");
                if (!nuevoPrecio.isEmpty()) producto.setPrecio(new BigDecimal(nuevoPrecio));

                String nuevaCantidad = view.solicitarInput("Ingrese la nueva cantidad (deje en blanco para no cambiar): ");
                if (!nuevaCantidad.isEmpty()) producto.setCantidad(Integer.parseInt(nuevaCantidad));

                if (view.solicitarOpcion("¿Desea cambiar el estado del producto? (1: Sí, 2: No): ", 1, 2) == 1) {
                    producto.setEstado(solicitarEstadoProducto());
                }

                inventarioService.actualizarProducto(producto);
                view.mostrarMensaje("Producto actualizado exitosamente.");
                mostrarDetallesProducto(producto);
                return;

            } catch (NumberFormatException e) {
                view.mostrarMensaje("Error: Entrada inválida. Debe ser un número.");
            } catch (EntityNotFoundException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }

            if (view.solicitarOpcion("¿Desea intentar modificar otro producto? (1: Sí, 2: No): ", 1, 2) == 2) {
                return;
            }
        }
    }

    private void eliminarProducto() {
        while (true) {
            try {
                Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del producto a eliminar: "));
                Producto producto = inventarioService.buscarProducto(id);

                if (producto == null) {
                    view.mostrarMensaje("Producto no encontrado.");
                    if (view.solicitarOpcion("¿Desea intentar con otro ID? (1: Sí, 2: No): ", 1, 2) == 2) {
                        return;
                    }
                    continue;
                }

                view.mostrarMensaje("Detalles del producto a eliminar:");
                mostrarDetallesProducto(producto);

                if (view.solicitarOpcion("¿Está seguro de que desea eliminar este producto? (1: Sí, 2: No): ", 1, 2) == 2) {
                    view.mostrarMensaje("Operación cancelada.");
                    return;
                }

                inventarioService.eliminarProducto(id);
                view.mostrarMensaje("Producto eliminado exitosamente.");
                return;

            } catch (NumberFormatException e) {
                view.mostrarMensaje("Error: ID inválido. Debe ser un número.");
            } catch (EntityNotFoundException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }

            if (view.solicitarOpcion("¿Desea intentar eliminar otro producto? (1: Sí, 2: No): ", 1, 2) == 2) {
                return;
            }
        }
    }

    private void buscarProducto() {
        String criterio = view.solicitarInput("Ingrese el código o nombre del producto a buscar: ");
        List<Producto> productos = inventarioService.buscarProductos(criterio);
        if (productos.isEmpty()) {
            view.mostrarMensaje("No se encontraron productos con ese criterio.");
        } else {
            view.mostrarMensaje("Productos encontrados:");
            mostrarProductosEnTabla(productos);
        }
    }

    private void actualizarStock() {
        while (true) {
            try {
                Long id = Long.parseLong(view.solicitarInput("Ingrese el ID del producto para actualizar stock: "));
                Producto producto = inventarioService.buscarProducto(id);

                if (producto == null) {
                    view.mostrarMensaje("Producto no encontrado.");
                    if (view.solicitarOpcion("¿Desea intentar con otro ID? (1: Sí, 2: No): ", 1, 2) == 2) {
                        return;
                    }
                    continue;
                }

                view.mostrarMensaje("Detalles actuales del producto:");
                mostrarDetallesProducto(producto);

                int cantidad = Integer.parseInt(view.solicitarInput("Ingrese la cantidad a añadir/restar del stock (use números negativos para restar): "));
                inventarioService.actualizarStock(id, cantidad);
                view.mostrarMensaje("Stock actualizado exitosamente.");

                producto = inventarioService.buscarProducto(id); // Obtener el producto actualizado
                mostrarDetallesProducto(producto);
                return;

            } catch (NumberFormatException e) {
                view.mostrarMensaje("Error: Entrada inválida. Debe ser un número.");
            } catch (EntityNotFoundException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (InsufficientStockException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (Exception e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }

            if (view.solicitarOpcion("¿Desea intentar actualizar el stock de otro producto? (1: Sí, 2: No): ", 1, 2) == 2) {
                return;
            }
        }
    }

    private void generarReporteInventario() {
        String reporte = inventarioService.generarReporteInventario();
        view.mostrarMensaje(reporte);
    }

    private EstadoProducto solicitarEstadoProducto() {
        int opcionEstado = view.solicitarOpcion(
                "Seleccione el estado del producto:\n1. Activo\n2. Inactivo\n3. Agotado\nOpción: ", 1, 3);
        return switch (opcionEstado) {
            case 1 -> EstadoProducto.ACTIVO;
            case 2 -> EstadoProducto.INACTIVO;
            case 3 -> EstadoProducto.AGOTADO;
            default -> throw new IllegalArgumentException("Estado inválido");
        };
    }

}
