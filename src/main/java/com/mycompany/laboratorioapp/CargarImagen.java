/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.laboratorioapp;

import javax.swing.*;
import java.net.URL;

public class CargarImagen {

    // Carga el primer recurso encontrado de la lista de rutas dadas (classpath y fallback a archivo local)
    private static ImageIcon cargarDesdeRutas(String... rutas) {
        for (String r : rutas) {
            try {
                // 1) Classpath absoluto o relativo
                URL url = CargarImagen.class.getResource(r.startsWith("/") ? r : "/" + r);
                if (url == null) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) url = cl.getResource(r);
                }
                if (url != null) return new ImageIcon(url);

                // 2) Fallback a ruta de archivo local (útil en ejecución desde IDE)
                java.io.File f = new java.io.File(r);
                if (!f.isAbsolute()) f = new java.io.File(System.getProperty("user.dir", "."), r);
                if (f.exists()) return new ImageIcon(f.getAbsolutePath());
            } catch (Exception ignored) {}
        }
        System.err.println("⚠ No se encontró ninguna de las rutas de imagen: " + java.util.Arrays.toString(rutas));
        return new ImageIcon();
    }

    // Cargar el logo desde resources (nombre actual en el proyecto) con retrocompatibilidad
    public static ImageIcon obtenerLogo() {
        return cargarDesdeRutas(
                "/logo para la aplicacion .jpg",        // ruta actual en resources
                "logo para la aplicacion .jpg",
                "/imagenes/logo.png",                   // rutas antiguas
                "src/main/resources/logo para la aplicacion .jpg"
        );
    }

    // Cargar el fondo desde resources priorizando "fondo para la aplicacion.jpeg" y con fallbacks
    public static ImageIcon obtenerFondo() {
        try {
            System.out.println("user.dir=" + System.getProperty("user.dir"));
            System.out.println("classpath:/fondo para la aplicacion.jpeg= " + CargarImagen.class.getResource("/fondo para la aplicacion.jpeg"));
            System.out.println("classpath:/fondo para la aplicacion.jpg= " + CargarImagen.class.getResource("/fondo para la aplicacion.jpg"));
            System.out.println("classpath:/fondo.jpg= " + CargarImagen.class.getResource("/fondo.jpg"));
            System.out.println("classpath:/fondo.png= " + CargarImagen.class.getResource("/fondo.png"));
        } catch (Exception ignored) {}
        return cargarDesdeRutas(
                // Prioridad: nueva imagen con extensión .jpeg
                "/fondo para la aplicacion.jpeg", "fondo para la aplicacion.jpeg",
                // Sin espacio como fallback
                "/fondo para la aplicacion.jpg", "fondo para la aplicacion.jpg",
                // Con espacio como fallback
                "/fondo para la aplicacion .jpg", "fondo para la aplicacion .jpg",
                // Otras variantes comunes
                "/fondo.jpg", "fondo.jpg",
                "/fondo.png", "fondo.png",
                // Fallbacks absolutos/relativos de desarrollo (sin Maven)
                "src/main/resources/fondo para la aplicacion.jpeg",
                "src/main/resources/fondo para la aplicacion.jpg",
                "src/main/resources/fondo.jpg",
                "src/main/resources/fondo.png",
                "target/classes/fondo para la aplicacion.jpeg",
                "target/classes/fondo para la aplicacion.jpg",
                "resources/fondo para la aplicacion.jpeg",
                "resources/fondo para la aplicacion.jpg",
                "resources/fondo.jpg",
                "resources/fondo.png"
        );
    }
}

