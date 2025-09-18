package com.mycompany.laboratorioapp;

public class Usuario {
    private final String rol;
    private final String usuario;
    private final String password;

    public Usuario(String rol, String usuario, String password) {
        this.rol = rol;
        this.usuario = usuario;
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
}
