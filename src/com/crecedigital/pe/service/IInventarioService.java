package com.crecedigital.pe.service;

import com.crecedigital.pe.model.Producto;

import java.util.Comparator;
import java.util.List;

public interface IInventarioService {
    void agregarProducto(Producto producto);
    Producto buscarProducto(Long id);
    void actualizarProducto(Producto producto);
    void eliminarProducto(Long id);
    List<Producto> listarProductos();
    List<Producto> ordenarProductos(Comparator<Producto> comparator);
    String generarReporteInventario();
    void actualizarStock(Long productoId, int cantidad);
    List<Producto> buscarProductos(String criterio);
}
