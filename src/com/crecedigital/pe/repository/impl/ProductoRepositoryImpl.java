package com.crecedigital.pe.repository.impl;

import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.model.Producto;
import com.crecedigital.pe.repository.IProductoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoRepositoryImpl implements IProductoRepository {
    private final Map<Long, Producto> productos;
    private Long nextId;

    public ProductoRepositoryImpl() {
        this.productos = new HashMap<>();
        this.nextId = 1L;
    }

    @Override
    public void agregar(Producto producto) {
        producto.setId(nextId++);
        productos.put(producto.getId(), producto);
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productos.get(id);
    }

    @Override
    public void actualizar(Producto producto) {
        if (productos.containsKey(producto.getId())) {
            productos.put(producto.getId(), producto);
        } else {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + producto.getId());
        }
    }

    @Override
    public void eliminar(Long id) {
        if (productos.remove(id) == null) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public List<Producto> buscarPorCriterio(String criterio) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : productos.values()) {
            if (producto.getNombre().toLowerCase().contains(criterio.toLowerCase())) {
                resultado.add(producto);
            }
        }
        return resultado;
    }
}
