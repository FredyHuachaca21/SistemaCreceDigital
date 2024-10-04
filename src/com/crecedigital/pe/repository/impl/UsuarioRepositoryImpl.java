package com.crecedigital.pe.repository.impl;

import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.repository.IUsuarioRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private final Map<Long, Usuario> usuarios;
    private Long nextId;

    public UsuarioRepositoryImpl() {
        this.usuarios = new HashMap<>();
        this.nextId = 1L;
    }

    @Override
    public void agregar(Usuario usuario) {
        usuario.setId(nextId++);
        usuarios.put(usuario.getId(), usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarios.get(id);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarios.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void actualizar(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            usuarios.put(usuario.getId(), usuario);
        } else {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuario.getId());
        }
    }

    @Override
    public void eliminar(Long id) {
        if (usuarios.remove(id) == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }
}
