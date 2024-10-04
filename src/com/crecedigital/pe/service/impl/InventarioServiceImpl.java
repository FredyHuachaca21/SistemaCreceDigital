package com.crecedigital.pe.service.impl;

import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.InsufficientStockException;
import com.crecedigital.pe.model.Producto;
import com.crecedigital.pe.repository.IProductoRepository;
import com.crecedigital.pe.service.IInventarioService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioServiceImpl implements IInventarioService {

    private final IProductoRepository productoRepository;

    public InventarioServiceImpl(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void agregarProducto(Producto producto) {
        productoRepository.agregar(producto);
    }

    @Override
    public Producto buscarProducto(Long id) {
        Producto producto = productoRepository.buscarPorId(id);
        if (producto == null) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        return producto;
    }

    @Override
    public void actualizarProducto(Producto producto) {
        if (productoRepository.buscarPorId(producto.getId()) == null) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + producto.getId());
        }
        productoRepository.actualizar(producto);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (productoRepository.buscarPorId(id) == null) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.eliminar(id);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.listarTodos();
    }

    @Override
    public List<Producto> ordenarProductos(Comparator<Producto> comparator) {
        return productoRepository.listarTodos().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public String generarReporteInventario() {
        List<Producto> productos = productoRepository.listarTodos();
        StringBuilder reporte = new StringBuilder("Reporte de Inventario:\n");
        reporte.append(String.format("%-10s %-30s %-10s %-10s %-15s\n", "ID", "Nombre", "Precio", "Cantidad", "Estado"));
        for (Producto producto : productos) {
            reporte.append(String.format("%-10d %-30s %-10.2f %-10d %-15s\n",
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getCantidad(),
                    producto.getEstado()));
        }
        return reporte.toString();
    }

    @Override
    public void actualizarStock(Long productoId, int cantidad) {
        Producto producto = buscarProducto(productoId);
        int nuevoStock = producto.getCantidad() + cantidad;
        if (nuevoStock < 0) {
            throw new InsufficientStockException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        producto.setCantidad(nuevoStock);
        actualizarProducto(producto);
    }

    @Override
    public List<Producto> buscarProductos(String criterio) {
        return productoRepository.buscarPorCriterio(criterio);
    }
}
