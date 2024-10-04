package com.crecedigital.pe.repository;

import com.crecedigital.pe.model.Usuario;

import java.util.List;

public interface IUsuarioRepository {
    void agregar(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorUsername(String username);
    void actualizar(Usuario usuario);
    void eliminar(Long id);
    List<Usuario> listarTodos();
}
