package com.crecedigital.pe.model;

import com.crecedigital.pe.enums.Rol;
import com.crecedigital.pe.exception.InvalidCredentialsException;

public class Usuario {
    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String password;
    private Rol rol;

    // Constructor completo
    public Usuario(Long id, String nombre, String apellido, String username, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Constructor sin ID (útil para crear nuevos usuarios)
    public Usuario(String nombre, String apellido, String username, String password, Rol rol) {
        this(null, nombre, apellido, username, password, rol);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean verificarPassword(String passwordIngresada) {
        return this.password.equals(passwordIngresada);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean tieneRol(Rol rol) {
        return this.rol == rol;
    }

    public void cambiarPassword(String passwordAntigua, String passwordNueva) {
        if (!verificarPassword(passwordAntigua)) {
            throw new InvalidCredentialsException("La contraseña antigua no es correcta");
        }
        this.password = passwordNueva;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", username='" + username + '\'' +
                ", rol=" + rol +
                '}';
    }
}
