package com.mycompany.laboratorioapp.dao;

import com.mycompany.laboratorioapp.ConexionMySQL;
import com.mycompany.laboratorioapp.usuarios.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para gestionar operaciones CRUD de Usuarios en MySQL
 */
public class UsuarioDAO {
    
    /**
     * Inserta un nuevo usuario en la base de datos
     */
    public static boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (cedula, nombres, apellidos, usuario, password, rol, " +
                     "modulo_ventas_laboratorio, modulo_compras, modulo_administrativos, " +
                     "puede_descuento, puede_recargo, puede_cierre, puede_cortesias, " +
                     "puede_control_precios, puede_control_resultados, puede_anular, " +
                     "puede_modificar_ordenes, permisos_administrativos) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getCedula());
            pstmt.setString(2, usuario.getNombres());
            pstmt.setString(3, usuario.getApellidos());
            pstmt.setString(4, usuario.getUsuario());
            pstmt.setString(5, usuario.getPassword());
            pstmt.setString(6, usuario.getRol());
            pstmt.setBoolean(7, usuario.hasModuloVentasLaboratorio());
            pstmt.setBoolean(8, usuario.hasModuloCompras());
            pstmt.setBoolean(9, usuario.hasModuloAdministrativos());
            pstmt.setBoolean(10, usuario.isPuedeDescuento());
            pstmt.setBoolean(11, usuario.isPuedeRecargo());
            pstmt.setBoolean(12, usuario.isPuedeCierre());
            pstmt.setBoolean(13, usuario.isPuedeCortesias());
            pstmt.setBoolean(14, usuario.isPuedeControlPrecios());
            pstmt.setBoolean(15, usuario.isPuedeControlResultados());
            pstmt.setBoolean(16, usuario.isPuedeAnular());
            pstmt.setBoolean(17, usuario.isPuedeModificarOrdenes());
            pstmt.setBoolean(18, usuario.isPermisosAdministrativos());
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            System.err.println("Error al insertar usuario: " + mensajeError);
            
            // Detectar errores específicos de estructura de base de datos
            if (mensajeError != null) {
                if (mensajeError.contains("Unknown column") || mensajeError.contains("columna") || mensajeError.contains("es desconocida")) {
                    System.err.println("ERROR CRÍTICO: La estructura de la tabla 'usuarios' no coincide con el código.");
                    System.err.println("Por favor, ejecute el script de migración: database/migrate_fix_usuarios_table.sql");
                    System.err.println("O recree la tabla con: database/recreate_usuarios_table.sql");
                } else if (mensajeError.contains("Duplicate entry") || mensajeError.contains("Duplicate key")) {
                    System.err.println("El usuario o cédula ya existe en la base de datos.");
                }
            }
            return false;
        }
    }
    
    /**
     * Actualiza un usuario existente
     */
    public static boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, password = ?, rol = ?, " +
                     "modulo_ventas_laboratorio = ?, modulo_compras = ?, modulo_administrativos = ?, " +
                     "puede_descuento = ?, puede_recargo = ?, puede_cierre = ?, puede_cortesias = ?, " +
                     "puede_control_precios = ?, puede_control_resultados = ?, puede_anular = ?, " +
                     "puede_modificar_ordenes = ?, permisos_administrativos = ? " +
                     "WHERE usuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombres());
            pstmt.setString(2, usuario.getApellidos());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getRol());
            pstmt.setBoolean(5, usuario.hasModuloVentasLaboratorio());
            pstmt.setBoolean(6, usuario.hasModuloCompras());
            pstmt.setBoolean(7, usuario.hasModuloAdministrativos());
            pstmt.setBoolean(8, usuario.isPuedeDescuento());
            pstmt.setBoolean(9, usuario.isPuedeRecargo());
            pstmt.setBoolean(10, usuario.isPuedeCierre());
            pstmt.setBoolean(11, usuario.isPuedeCortesias());
            pstmt.setBoolean(12, usuario.isPuedeControlPrecios());
            pstmt.setBoolean(13, usuario.isPuedeControlResultados());
            pstmt.setBoolean(14, usuario.isPuedeAnular());
            pstmt.setBoolean(15, usuario.isPuedeModificarOrdenes());
            pstmt.setBoolean(16, usuario.isPermisosAdministrativos());
            pstmt.setString(17, usuario.getUsuario());
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un usuario por su nombre de usuario
     */
    public static boolean eliminar(String usuario) {
        String sql = "DELETE FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca un usuario por su nombre de usuario y contraseña (para login)
     */
    public static Usuario buscarPorCredenciales(String usuario, String password) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca un usuario por su nombre de usuario
     */
    public static Usuario buscarPorUsuario(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public static List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombres, apellidos";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    /**
     * Obtiene el ID de un usuario por su nombre de usuario
     * Útil para obtener el ID necesario al crear órdenes
     */
    public static int obtenerIdPorUsuario(String usuario) {
        String sql = "SELECT id FROM usuarios WHERE usuario = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de usuario: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private static Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getString("cedula"),
            rs.getString("nombres"),
            rs.getString("apellidos"),
            rs.getString("usuario"),
            rs.getString("password"),
            rs.getString("rol"),
            rs.getBoolean("modulo_ventas_laboratorio"),
            rs.getBoolean("modulo_compras"),
            rs.getBoolean("modulo_administrativos"),
            rs.getBoolean("puede_descuento"),
            rs.getBoolean("puede_recargo"),
            rs.getBoolean("puede_cierre"),
            rs.getBoolean("puede_cortesias"),
            rs.getBoolean("puede_control_precios"),
            rs.getBoolean("puede_control_resultados"),
            rs.getBoolean("puede_anular"),
            rs.getBoolean("puede_modificar_ordenes"),
            rs.getBoolean("permisos_administrativos")
        );
    }
}

