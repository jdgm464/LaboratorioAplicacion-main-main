/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.laboratorioapp;

import javax.swing.*;
import java.net.URL;

public class CargarImagen {

    // Cargar el logo desde la carpeta resources
    public static ImageIcon obtenerLogo() {
        URL url = CargarImagen.class.getResource("/imagenes/logo.png");
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.err.println("⚠ No se encontró el archivo /imagenes/logo.png");
            return new ImageIcon(); // Devuelve un icono vacío si no se encuentra
        }
    }

    // Cargar el fondo desde la carpeta resources
    public static ImageIcon obtenerFondo() {
        URL url = CargarImagen.class.getResource("/imagenes/fondo.png");
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.err.println("⚠ No se encontró el archivo /imagenes/fondo.png");
            return new ImageIcon(); // Devuelve un icono vacío si no se encuentra
        }
    }
}

