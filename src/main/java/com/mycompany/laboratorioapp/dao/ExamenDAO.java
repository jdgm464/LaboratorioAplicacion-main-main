package com.mycompany.laboratorioapp.dao;

import com.mycompany.laboratorioapp.ConexionMySQL;
import com.mycompany.laboratorioapp.examenes.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para gestionar operaciones CRUD de Exámenes en MySQL
 */
public class ExamenDAO {
    
    /**
     * Inserta un nuevo examen en la base de datos
     */
    public static boolean insertar(Examen examen) {
        String sql = "INSERT INTO examenes (codigo, nombre, precio) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, examen.getCodigo());
            pstmt.setString(2, examen.getNombre());
            pstmt.setDouble(3, examen.getPrecio());
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar examen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un examen existente
     */
    public static boolean actualizar(Examen examen) {
        String sql = "UPDATE examenes SET nombre = ?, precio = ? WHERE codigo = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, examen.getNombre());
            pstmt.setDouble(2, examen.getPrecio());
            pstmt.setString(3, examen.getCodigo());
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar examen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un examen por su código
     */
    public static boolean eliminar(String codigo) {
        String sql = "DELETE FROM examenes WHERE codigo = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar examen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca un examen por su código
     */
    public static Examen buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM examenes WHERE codigo = ? AND activo = TRUE";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearExamen(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar examen: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los exámenes activos
     */
    public static List<Examen> obtenerTodos() {
        List<Examen> examenes = new ArrayList<>();
        String sql = "SELECT * FROM examenes WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                examenes.add(mapearExamen(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener exámenes: " + e.getMessage());
        }
        
        return examenes;
    }
    
    /**
     * Busca exámenes por nombre
     */
    public static List<Examen> buscarPorNombre(String busqueda) {
        List<Examen> examenes = new ArrayList<>();
        String sql = "SELECT * FROM examenes WHERE nombre LIKE ? AND activo = TRUE ORDER BY nombre";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + busqueda + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                examenes.add(mapearExamen(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar exámenes: " + e.getMessage());
        }
        
        return examenes;
    }
    
    /**
     * Obtiene el precio de un examen por su código
     */
    public static Double obtenerPrecio(String codigo) {
        String sql = "SELECT precio FROM examenes WHERE codigo = ? AND activo = TRUE";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("precio");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener precio: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene el nombre de un examen por su código
     */
    public static String obtenerNombrePorCodigo(String codigo) {
        String sql = "SELECT nombre FROM examenes WHERE codigo = ? AND activo = TRUE";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nombre");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre por código: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Mapea un ResultSet a un objeto Examen
     */
    private static Examen mapearExamen(ResultSet rs) throws SQLException {
        return new Examen(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getDouble("precio")
        );
    }
}

