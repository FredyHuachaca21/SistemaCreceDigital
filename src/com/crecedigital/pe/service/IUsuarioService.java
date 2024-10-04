package com.crecedigital.pe.service;

import com.crecedigital.pe.enums.Rol;
import com.crecedigital.pe.model.Usuario;

import java.util.List;

public interface IUsuarioService {
    void registrarUsuario(Usuario usuario);
    Usuario autenticarUsuario(String username, String password);
    List<Usuario> listarUsuarios();
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    Usuario buscarUsuario(Long id);
    void cambiarRolUsuario(Long userId, Rol nuevoRol);
    List<Usuario> listarUsuariosPorRol(Rol rol);
    void cambiarPassword(String username, String passwordActual, String nuevaPassword);
    Usuario buscarPorUsername(String username);
}
