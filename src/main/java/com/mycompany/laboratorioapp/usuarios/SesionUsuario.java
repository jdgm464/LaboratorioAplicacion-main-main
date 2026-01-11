package com.mycompany.laboratorioapp.usuarios;

import com.mycompany.laboratorioapp.dao.UsuarioDAO;

/**
 * Clase estática para mantener la sesión del usuario actual
 */
public class SesionUsuario {
    private static String usuarioActual = null;
    private static Integer usuarioId = null;
    
    /**
     * Establece el usuario actual de la sesión
     */
    public static void establecerUsuario(String nombreUsuario) {
        usuarioActual = nombreUsuario;
        usuarioId = null; // Resetear para forzar nueva búsqueda
        
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            try {
                usuarioId = UsuarioDAO.obtenerIdPorUsuario(nombreUsuario);
                if (usuarioId == -1) {
                    System.err.println("Advertencia: Usuario '" + nombreUsuario + "' no encontrado en BD. Intentando crear...");
                    // El usuario no existe en BD, pero la sesión se establece igual
                    // OrdenManager intentará crear el usuario si es necesario
                }
            } catch (Exception e) {
                System.err.println("Error al obtener ID de usuario: " + e.getMessage());
                usuarioId = null;
            }
        } else {
            usuarioId = null;
        }
    }
    
    /**
     * Obtiene el nombre de usuario actual
     */
    public static String obtenerUsuario() {
        return usuarioActual;
    }
    
    /**
     * Obtiene el ID del usuario actual (necesario para crear órdenes)
     */
    public static int obtenerUsuarioId() {
        if (usuarioActual == null || usuarioActual.isEmpty()) {
            return -1;
        }
        
        // Si no tenemos el ID en caché, intentar obtenerlo
        if (usuarioId == null || usuarioId == -1) {
            try {
                usuarioId = UsuarioDAO.obtenerIdPorUsuario(usuarioActual);
            } catch (Exception e) {
                System.err.println("Error al obtener ID de usuario: " + e.getMessage());
                return -1;
            }
        }
        
        return usuarioId != null ? usuarioId : -1;
    }
    
    /**
     * Cierra la sesión del usuario
     */
    public static void cerrarSesion() {
        usuarioActual = null;
        usuarioId = null;
    }
}

