package com.mycompany.laboratorioapp;

public class Examen {
    private String codigo;
    private String nombre;
    private double precio;

    public Examen(String nombre, double precio) {
        this(null, nombre, precio);
    }

    public Examen(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio;
    }
}
