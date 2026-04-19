package com.mycompany.laboratorioapp.dao;

import com.mycompany.laboratorioapp.ConexionPostgreSQL;
import com.mycompany.laboratorioapp.pacientes.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Data Access Object para gestionar operaciones CRUD de Pacientes en PostgreSQL
 */
public class PacienteDAO {
    
    /**
     * Inserta un nuevo paciente en la base de datos
     */
    public static boolean insertar(Paciente paciente) {
        String sql = "INSERT INTO pacientes (cedula, codigo, nombre, apellido, edad, fecha_nacimiento, direccion, telefono, correo, sexo, sede, categoria, dedicacion, estatus, fecha_ingreso) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paciente.getCedula());
            pstmt.setString(2, paciente.getCodigo());
            pstmt.setString(3, paciente.getNombre());
            pstmt.setString(4, paciente.getApellido());
            pstmt.setInt(5, paciente.getEdad());
            // �� Guardar fecha de nacimiento
            if (paciente.getFechaNacimiento() != null) {
                pstmt.setDate(6, java.sql.Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                pstmt.setDate(6, null);
            }
            pstmt.setString(7, paciente.getDireccion());
            pstmt.setString(8, paciente.getTelefono());
            pstmt.setString(9, paciente.getCorreo());
            pstmt.setString(10, convertirSexo(paciente.getSexo()));
            pstmt.setString(11, paciente.getSede());
            pstmt.setString(12, paciente.getCategoria());
            pstmt.setString(13, paciente.getDedicacion());
            pstmt.setString(14, paciente.getEstatus());
            if (paciente.getFechaIngreso() != null) {
                pstmt.setDate(15, java.sql.Date.valueOf(paciente.getFechaIngreso()));
            } else {
                pstmt.setDate(15, null);
            }
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar paciente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un paciente existente
     */
    public static boolean actualizar(Paciente paciente) {
        String sql = "UPDATE pacientes SET codigo = ?, nombre = ?, apellido = ?, edad = ?, fecha_nacimiento = ?, direccion = ?, telefono = ?, correo = ?, sexo = ?, sede = ?, categoria = ?, dedicacion = ?, estatus = ?, fecha_ingreso = ? WHERE cedula = ?";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paciente.getCodigo());
            pstmt.setString(2, paciente.getNombre());
            pstmt.setString(3, paciente.getApellido());
            pstmt.setInt(4, paciente.getEdad());
            // 🆕 Actualizar fecha de nacimiento
            if (paciente.getFechaNacimiento() != null) {
                pstmt.setDate(5, java.sql.Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                pstmt.setDate(5, null);
            }
            pstmt.setString(6, paciente.getDireccion());
            pstmt.setString(7, paciente.getTelefono());
            pstmt.setString(8, paciente.getCorreo());
            pstmt.setString(9, convertirSexo(paciente.getSexo()));
            pstmt.setString(10, paciente.getSede());
            pstmt.setString(11, paciente.getCategoria());
            pstmt.setString(12, paciente.getDedicacion());
            pstmt.setString(13, paciente.getEstatus());
            if (paciente.getFechaIngreso() != null) {
                pstmt.setDate(14, java.sql.Date.valueOf(paciente.getFechaIngreso()));
            } else {
                pstmt.setDate(14, null);
            }
            pstmt.setString(15, paciente.getCedula());
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un paciente por su cédula
     */
    public static boolean eliminar(String cedula) {
        String sql = "DELETE FROM pacientes WHERE cedula = ?";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca un paciente por su cédula
     */
    public static Paciente buscarPorCedula(String cedula) {
        String sql = "SELECT * FROM pacientes WHERE cedula = ?";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearPaciente(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los pacientes
     */
    public static List<Paciente> obtenerTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes ORDER BY nombre, apellido";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener pacientes: " + e.getMessage());
        }
        
        return pacientes;
    }
    
    /**
     * Busca pacientes por nombre o apellido
     */
    public static List<Paciente> buscarPorNombre(String busqueda) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE nombre LIKE ? OR apellido LIKE ? ORDER BY nombre, apellido";
        
        try (Connection conn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar pacientes: " + e.getMessage());
        }
        
        return pacientes;
    }
    
    /**
     * Mapea un ResultSet a un objeto Paciente
     */
    private static Paciente mapearPaciente(ResultSet rs) throws SQLException {
        String sexo = "";
        try {
            sexo = rs.getString("sexo");
            if (sexo == null) sexo = "";
        } catch (SQLException e) {
            sexo = "";
        }
        
        // 🆕 Leer fecha de nacimiento
        LocalDate fechaNacimiento = null;
        try {
            java.sql.Date fechaSql = rs.getDate("fecha_nacimiento");
            if (fechaSql != null) {
                fechaNacimiento = fechaSql.toLocalDate();
            }
        } catch (SQLException e) {
            // Columna puede no existir en BD antigua
            fechaNacimiento = null;
        }
        
        Paciente paciente = new Paciente(
            rs.getString("cedula"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getInt("edad"),
            fechaNacimiento, // 🆕 Pasar fecha de nacimiento
            rs.getString("direccion"),
            rs.getString("telefono"),
            rs.getString("correo"),
            sexo
        );

        try { paciente.setCodigo(rs.getString("codigo")); } catch (SQLException ignored) {}
        try { paciente.setSede(rs.getString("sede")); } catch (SQLException ignored) {}
        try { paciente.setCategoria(rs.getString("categoria")); } catch (SQLException ignored) {}
        try { paciente.setDedicacion(rs.getString("dedicacion")); } catch (SQLException ignored) {}
        try { paciente.setEstatus(rs.getString("estatus")); } catch (SQLException ignored) {}
        try {
            java.sql.Date fechaIngresoSql = rs.getDate("fecha_ingreso");
            if (fechaIngresoSql != null) {
                paciente.setFechaIngreso(fechaIngresoSql.toLocalDate());
            }
        } catch (SQLException ignored) {}
        return paciente;
    }

    /**
     * Convierte "Masculino"/"Femenino" a "M"/"F" o viceversa
     */
    private static String convertirSexo(String sexo) {
        if (sexo == null || sexo.isEmpty()) return "";
        String s = sexo.trim();
        if (s.equalsIgnoreCase("M") || s.equalsIgnoreCase("Masculino")) return "M";
        if (s.equalsIgnoreCase("F") || s.equalsIgnoreCase("Femenino")) return "F";
        return s; // Devolver tal cual si no coincide
    }
}

