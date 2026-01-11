package com.mycompany.laboratorioapp.pacientes;

import java.time.LocalDate;

public class Paciente {
    private String cedula;
    private String nombre;
    private String apellido;
    private int edad;
    private LocalDate fechaNacimiento; // 🆕 Campo para fecha de nacimiento
    private String direccion;
    private String telefono;
    private String correo;
    private String sexo; // M o F

    public Paciente(String cedula, String nombre, String apellido, int edad,
                    String direccion, String telefono, String correo) {
        this(cedula, nombre, apellido, edad, direccion, telefono, correo, "");
    }

    public Paciente(String cedula, String nombre, String apellido, int edad,
                    String direccion, String telefono, String correo, String sexo) {
        this(cedula, nombre, apellido, edad, null, direccion, telefono, correo, sexo);
    }

    // 🆕 Constructor completo con fecha de nacimiento
    public Paciente(String cedula, String nombre, String apellido, int edad,
                    LocalDate fechaNacimiento, String direccion, String telefono, 
                    String correo, String sexo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.sexo = sexo != null ? sexo : "";
    }

    // --- Getters y Setters ---
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    // 🆕 Getters y Setters para fechaNacimiento
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        // Calcular edad automáticamente si hay fecha de nacimiento
        if (fechaNacimiento != null) {
            this.edad = java.time.Period.between(fechaNacimiento, java.time.LocalDate.now()).getYears();
        }
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSexo() {
        return sexo != null ? sexo : "";
    }

    public void setSexo(String sexo) {
        this.sexo = sexo != null ? sexo : "";
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + cedula + ")";
    }
}

