package com.crecedigital.pe.service.impl;

import com.crecedigital.pe.enums.Rol;
import com.crecedigital.pe.exception.EntityNotFoundException;
import com.crecedigital.pe.exception.InvalidCredentialsException;
import com.crecedigital.pe.model.Usuario;
import com.crecedigital.pe.repository.IUsuarioRepository;
import com.crecedigital.pe.service.IUsuarioService;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorUsername(usuario.getUsername()) != null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        usuarioRepository.agregar(usuario);
    }

    @Override
    public Usuario autenticarUsuario(String username, String password) {
        Usuario usuario = usuarioRepository.buscarPorUsername(username);
        if (usuario == null || !usuario.verificarPassword(password)) {
            throw new InvalidCredentialsException("Nombre de usuario o contraseña incorrectos");
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorId(usuario.getId()) == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuario.getId());
        }
        usuarioRepository.actualizar(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (usuarioRepository.buscarPorId(id) == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.eliminar(id);
    }

    @Override
    public Usuario buscarUsuario(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        return usuario;
    }

    @Override
    public void cambiarRolUsuario(Long userId, Rol nuevoRol) {
        Usuario usuario = buscarUsuario(userId);
        usuario.setRol(nuevoRol);
        actualizarUsuario(usuario);
    }

    @Override
    public List<Usuario> listarUsuariosPorRol(Rol rol) {
        return usuarioRepository.listarTodos().stream()
                .filter(u -> u.getRol() == rol)
                .collect(Collectors.toList());
    }

    @Override
    public void cambiarPassword(String username, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioRepository.buscarPorUsername(username);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado: " + username);
        }

        if (!usuario.verificarPassword(passwordActual)) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        if (passwordActual.equals(nuevaPassword)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        if (nuevaPassword.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(nuevaPassword);
        usuarioRepository.actualizar(usuario);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        Usuario usuario = usuarioRepository.buscarPorUsername(username);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado: " + username);
        }
        return usuario;
    }
}
