package com.crecedigital.pe.repository;

import com.crecedigital.pe.model.Producto;

import java.util.List;

public interface IProductoRepository {

    void agregar(Producto producto);
    Producto buscarPorId(Long id);
    void actualizar(Producto producto);
    void eliminar(Long id);
    List<Producto> listarTodos();
    List<Producto> buscarPorCriterio(String criterio);
}
