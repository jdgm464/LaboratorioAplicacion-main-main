package com.mycompany.laboratorioapp.dao;

import com.mycompany.laboratorioapp.ConexionMySQL;
import com.mycompany.laboratorioapp.ordenes.Orden;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para gestionar operaciones CRUD de Órdenes en MySQL
 */
public class OrdenDAO {
    
    /**
     * Inserta una nueva orden en la base de datos
     * Busca automáticamente el paciente por cédula y requiere usuario_id
     * @param orden La orden a insertar
     * @param usuarioId ID del usuario que crea la orden
     * @return true si se insertó correctamente
     */
    public static boolean insertar(Orden orden, int usuarioId) {
        // Verificar si la columna sexo existe en la tabla
        boolean tieneSexo = verificarColumnaSexo();
        
        String sql;
        if (tieneSexo) {
            sql = "INSERT INTO ordenes (numero_orden, numero_factura, numero_control, numero_lote, " +
                  "fecha_registro, hora_registro, paciente_id, usuario_id, codigo_paciente, cedula, " +
                  "nombres, apellidos, direccion, telefono, correo, codigo_empresa, empresa, sexo, total, estatus) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // SQL sin el campo sexo (para bases de datos antiguas)
            sql = "INSERT INTO ordenes (numero_orden, numero_factura, numero_control, numero_lote, " +
                     "fecha_registro, hora_registro, paciente_id, usuario_id, codigo_paciente, cedula, " +
                     "nombres, apellidos, direccion, telefono, correo, codigo_empresa, empresa, total, estatus) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        Connection conn = null;
        try {
            System.out.println("🔌 Obteniendo conexión a MySQL...");
            conn = ConexionMySQL.obtenerConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            System.out.println("✅ Conexión establecida");
            
            // Buscar el paciente por cédula para obtener su ID
            System.out.println("🔍 Buscando paciente con cédula: " + orden.getCedula());
            int pacienteId = obtenerPacienteIdPorCedula(conn, orden.getCedula());
            if (pacienteId == -1) {
                System.out.println("⚠️ Paciente no encontrado, creando nuevo paciente...");
                // Si el paciente no existe, crearlo primero
                pacienteId = crearPacienteSiNoExiste(conn, orden);
                if (pacienteId == -1) {
                    throw new SQLException("No se pudo crear o encontrar el paciente con cédula: " + orden.getCedula());
                }
                System.out.println("✅ Paciente creado con ID: " + pacienteId);
            } else {
                System.out.println("✅ Paciente encontrado con ID: " + pacienteId);
            }
            
            // Insertar la orden
            System.out.println("📝 Preparando inserción de orden: " + orden.getNumeroOrden());
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, orden.getNumeroOrden());
                pstmt.setString(2, orden.getNumeroFactura());
                pstmt.setString(3, orden.getNumeroControl());
                pstmt.setString(4, orden.getNumeroLote());
                // Manejar fecha (convertir de DD/MM/YYYY a YYYY-MM-DD si es necesario)
                String fechaStr = orden.getFechaRegistro();
                Date fechaDate;
                if (fechaStr != null && !fechaStr.isEmpty()) {
                    try {
                        // Intentar parsear formato DD/MM/YYYY
                        if (fechaStr.contains("/")) {
                            java.text.SimpleDateFormat sdfInput = new java.text.SimpleDateFormat("dd/MM/yyyy");
                            java.util.Date fechaUtil = sdfInput.parse(fechaStr);
                            fechaDate = new Date(fechaUtil.getTime());
                        } else {
                            // Asumir formato YYYY-MM-DD
                            fechaDate = Date.valueOf(fechaStr);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al parsear fecha '" + fechaStr + "': " + e.getMessage());
                        fechaDate = new Date(System.currentTimeMillis());
                    }
                } else {
                    fechaDate = new Date(System.currentTimeMillis());
                }
                pstmt.setDate(5, fechaDate);
                
                // Manejar hora (formato esperado: HH:mm o HH:mm:ss)
                String horaStr = orden.getHoraRegistro();
                if (horaStr != null && !horaStr.isEmpty()) {
                    // Si no tiene segundos, agregarlos
                    if (horaStr.split(":").length == 2) {
                        horaStr += ":00";
                    }
                    pstmt.setTime(6, Time.valueOf(horaStr));
                } else {
                    pstmt.setTime(6, new Time(System.currentTimeMillis()));
                }
                pstmt.setInt(7, pacienteId);
                pstmt.setInt(8, usuarioId);
                pstmt.setString(9, orden.getCodigoPaciente());
                pstmt.setString(10, orden.getCedula());
                pstmt.setString(11, orden.getNombres());
                pstmt.setString(12, orden.getApellidos());
                pstmt.setString(13, orden.getDireccion());
                pstmt.setString(14, orden.getTelefono());
                pstmt.setString(15, orden.getCorreo());
                pstmt.setString(16, orden.getCodigoEmpresa());
                pstmt.setString(17, orden.getEmpresa());
                if (tieneSexo) {
                    pstmt.setString(18, convertirSexo(orden.getSexo()));
                    pstmt.setDouble(19, orden.getTotal());
                    pstmt.setString(20, orden.getEstatus());
                } else {
                    // Sin campo sexo
                pstmt.setDouble(18, orden.getTotal());
                pstmt.setString(19, orden.getEstatus());
                }
                
                System.out.println("💾 Ejecutando INSERT de orden...");
                int filasAfectadas = pstmt.executeUpdate();
                System.out.println("✅ Orden insertada. Filas afectadas: " + filasAfectadas);
            }
            
            // Insertar los exámenes de la orden
            System.out.println("🔍 Obteniendo ID de orden recién insertada...");
            int ordenId = obtenerIdPorNumeroOrden(conn, orden.getNumeroOrden());
            System.out.println("📋 ID de orden obtenido: " + ordenId);
            if (ordenId > 0 && orden.getExamenes() != null && !orden.getExamenes().isEmpty()) {
                System.out.println("📝 Insertando " + orden.getExamenes().size() + " exámenes...");
                insertarExamenesOrden(conn, ordenId, orden.getExamenes());
                System.out.println("✅ Exámenes insertados");
            } else {
                System.out.println("⚠️ No hay exámenes para insertar o no se pudo obtener el ID de la orden");
            }
            
            System.out.println("💾 Haciendo commit de la transacción...");
            conn.commit();
            System.out.println("✅ Transacción completada exitosamente");
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al insertar orden: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Inserta los exámenes asociados a una orden
     */
    public static void insertarExamenesOrden(int ordenId, List<String> examenes) {
        String sql = "INSERT INTO orden_examenes (orden_id, examen_codigo, precio) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String codigoExamen : examenes) {
                Double precio = com.mycompany.laboratorioapp.dao.ExamenDAO.obtenerPrecio(codigoExamen);
                if (precio != null) {
                    pstmt.setInt(1, ordenId);
                    pstmt.setString(2, codigoExamen);
                    pstmt.setDouble(3, precio);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error al insertar exámenes de orden: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Inserta los exámenes asociados a una orden (versión con conexión)
     */
    private static void insertarExamenesOrden(Connection conn, int ordenId, List<String> examenes) throws SQLException {
        String sql = "INSERT INTO orden_examenes (orden_id, examen_codigo, precio) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String codigoExamen : examenes) {
                Double precio = ExamenDAO.obtenerPrecio(codigoExamen);
                if (precio != null) {
                    pstmt.setInt(1, ordenId);
                    pstmt.setString(2, codigoExamen);
                    pstmt.setDouble(3, precio);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }
    
    /**
     * Obtiene el ID de una orden por su número
     */
    public static int obtenerIdPorNumeroOrden(String numeroOrden) {
        String sql = "SELECT id FROM ordenes WHERE numero_orden = ?";
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numeroOrden);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de orden: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Obtiene el ID de una orden por su número (versión con conexión)
     */
    private static int obtenerIdPorNumeroOrden(Connection conn, String numeroOrden) throws SQLException {
        String sql = "SELECT id FROM ordenes WHERE numero_orden = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numeroOrden);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
    
    /**
     * Elimina todos los exámenes asociados a una orden
     */
    public static void eliminarExamenesOrden(int ordenId) {
        String sql = "DELETE FROM orden_examenes WHERE orden_id = ?";
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ordenId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar exámenes de orden: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Obtiene el ID de un paciente por su cédula
     */
    private static int obtenerPacienteIdPorCedula(Connection conn, String cedula) throws SQLException {
        String sql = "SELECT id FROM pacientes WHERE cedula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
    
    /**
     * Crea un paciente si no existe, basándose en los datos de la orden
     */
    private static int crearPacienteSiNoExiste(Connection conn, Orden orden) throws SQLException {
        // Verificar si la columna sexo existe en la tabla pacientes
        boolean tieneSexo = false;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "pacientes", "sexo");
            tieneSexo = columns.next();
            columns.close();
        } catch (SQLException e) {
            // Si hay error, asumir que no existe
            tieneSexo = false;
        }
        
        // Intentar crear un paciente básico con los datos disponibles
        String sql;
        if (tieneSexo) {
            sql = "INSERT INTO pacientes (cedula, nombre, apellido, edad, direccion, telefono, correo, sexo) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO pacientes (cedula, nombre, apellido, edad, direccion, telefono, correo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, orden.getCedula());
            pstmt.setString(2, orden.getNombres());
            pstmt.setString(3, orden.getApellidos());
            pstmt.setInt(4, 0); // Edad desconocida
            pstmt.setString(5, orden.getDireccion());
            pstmt.setString(6, orden.getTelefono());
            pstmt.setString(7, orden.getCorreo());
            if (tieneSexo) {
                pstmt.setString(8, convertirSexo(orden.getSexo()));
            }
            
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // Si falla, intentar obtener el ID si ya existe
            return obtenerPacienteIdPorCedula(conn, orden.getCedula());
        }
        return -1;
    }
    
    /**
     * Actualiza una orden existente
     * Nota: No actualiza paciente_id ni usuario_id por seguridad de integridad
     */
    public static boolean actualizar(Orden orden) {
        // Verificar si la columna sexo existe en la tabla
        boolean tieneSexo = verificarColumnaSexo();
        
        String sql;
        if (tieneSexo) {
            sql = "UPDATE ordenes SET numero_factura = ?, numero_control = ?, numero_lote = ?, " +
                  "fecha_registro = ?, hora_registro = ?, codigo_paciente = ?, cedula = ?, " +
                  "nombres = ?, apellidos = ?, direccion = ?, telefono = ?, correo = ?, " +
                  "codigo_empresa = ?, empresa = ?, sexo = ?, total = ?, estatus = ? " +
                  "WHERE numero_orden = ?";
        } else {
            // SQL sin el campo sexo (para bases de datos antiguas)
            sql = "UPDATE ordenes SET numero_factura = ?, numero_control = ?, numero_lote = ?, " +
                     "fecha_registro = ?, hora_registro = ?, codigo_paciente = ?, cedula = ?, " +
                     "nombres = ?, apellidos = ?, direccion = ?, telefono = ?, correo = ?, " +
                     "codigo_empresa = ?, empresa = ?, total = ?, estatus = ? " +
                     "WHERE numero_orden = ?";
        }
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, orden.getNumeroFactura());
            pstmt.setString(2, orden.getNumeroControl());
            pstmt.setString(3, orden.getNumeroLote());
            // Manejar fecha (convertir de DD/MM/YYYY a YYYY-MM-DD si es necesario)
            String fechaStr = orden.getFechaRegistro();
            Date fechaDate;
            if (fechaStr != null && !fechaStr.isEmpty()) {
                try {
                    // Intentar parsear formato DD/MM/YYYY
                    if (fechaStr.contains("/")) {
                        java.text.SimpleDateFormat sdfInput = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        java.util.Date fechaUtil = sdfInput.parse(fechaStr);
                        fechaDate = new Date(fechaUtil.getTime());
                    } else {
                        // Asumir formato YYYY-MM-DD
                        fechaDate = Date.valueOf(fechaStr);
                    }
                } catch (Exception e) {
                    System.err.println("Error al parsear fecha en actualizar: '" + fechaStr + "': " + e.getMessage());
                    fechaDate = new Date(System.currentTimeMillis());
                }
            } else {
                fechaDate = new Date(System.currentTimeMillis());
            }
            pstmt.setDate(4, fechaDate);
            
            // Manejar hora (formato esperado: HH:mm o HH:mm:ss)
            String horaStr = orden.getHoraRegistro();
            if (horaStr != null && !horaStr.isEmpty()) {
                // Si no tiene segundos, agregarlos
                if (horaStr.split(":").length == 2) {
                    horaStr += ":00";
                }
                pstmt.setTime(5, Time.valueOf(horaStr));
            } else {
                pstmt.setTime(5, new Time(System.currentTimeMillis()));
            }
            pstmt.setString(6, orden.getCodigoPaciente());
            pstmt.setString(7, orden.getCedula());
            pstmt.setString(8, orden.getNombres());
            pstmt.setString(9, orden.getApellidos());
            pstmt.setString(10, orden.getDireccion());
            pstmt.setString(11, orden.getTelefono());
            pstmt.setString(12, orden.getCorreo());
            pstmt.setString(13, orden.getCodigoEmpresa());
            pstmt.setString(14, orden.getEmpresa());
            if (tieneSexo) {
                pstmt.setString(15, convertirSexo(orden.getSexo()));
                pstmt.setDouble(16, orden.getTotal());
                pstmt.setString(17, orden.getEstatus());
                pstmt.setString(18, orden.getNumeroOrden());
            } else {
                // Sin campo sexo
            pstmt.setDouble(15, orden.getTotal());
            pstmt.setString(16, orden.getEstatus());
            pstmt.setString(17, orden.getNumeroOrden());
            }
            
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar orden: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina una orden por su número
     */
    public static boolean eliminar(String numeroOrden) {
        String sql = "DELETE FROM ordenes WHERE numero_orden = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroOrden);
            int filas = pstmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar orden: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca una orden por su número
     */
    public static Orden buscarPorNumero(String numeroOrden) {
        String sql = "SELECT * FROM ordenes WHERE numero_orden = ?";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroOrden);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Orden orden = mapearOrden(rs);
                // Cargar exámenes asociados
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                return orden;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar orden: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene los códigos de exámenes asociados a una orden
     */
    private static List<String> obtenerExamenesOrden(Connection conn, int ordenId) throws SQLException {
        List<String> examenes = new ArrayList<>();
        String sql = "SELECT examen_codigo FROM orden_examenes WHERE orden_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ordenId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                examenes.add(rs.getString("examen_codigo"));
            }
        }
        return examenes;
    }
    
    /**
     * Obtiene todas las órdenes
     */
    public static List<Orden> obtenerTodas() {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes ORDER BY fecha_registro DESC, hora_registro DESC";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                ordenes.add(orden);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener órdenes: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    /**
     * Busca órdenes por cédula del paciente
     */
    public static List<Orden> buscarPorCedula(String cedula) {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes WHERE cedula = ? ORDER BY fecha_registro DESC";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                ordenes.add(orden);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar órdenes: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    /**
     * Obtiene órdenes con información del paciente y usuario (usando JOINs)
     */
    public static List<Orden> obtenerTodasConRelaciones() {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT o.*, p.nombre as paciente_nombre, p.apellido as paciente_apellido, " +
                     "u.usuario as usuario_creador, u.nombres as usuario_nombres " +
                     "FROM ordenes o " +
                     "INNER JOIN pacientes p ON o.paciente_id = p.id " +
                     "INNER JOIN usuarios u ON o.usuario_id = u.id " +
                     "ORDER BY o.fecha_registro DESC, o.hora_registro DESC";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                ordenes.add(orden);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener órdenes con relaciones: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    /**
     * Obtiene órdenes de un paciente específico usando la relación
     */
    public static List<Orden> obtenerPorPacienteId(int pacienteId) {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes WHERE paciente_id = ? ORDER BY fecha_registro DESC";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                ordenes.add(orden);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener órdenes por paciente: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    /**
     * Obtiene órdenes creadas por un usuario específico
     */
    public static List<Orden> obtenerPorUsuarioId(int usuarioId) {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes WHERE usuario_id = ? ORDER BY fecha_registro DESC";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Orden orden = mapearOrden(rs);
                orden.setExamenes(obtenerExamenesOrden(conn, rs.getInt("id")));
                ordenes.add(orden);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener órdenes por usuario: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    /**
     * Obtiene el siguiente número de orden disponible
     * Busca el máximo número de orden en la BD y retorna el siguiente
     */
    public static String obtenerSiguienteNumeroOrden() {
        // Intentar obtener el máximo número de orden numérico
        String sql = "SELECT numero_orden FROM ordenes WHERE numero_orden REGEXP '^[0-9]+$' ORDER BY CAST(numero_orden AS UNSIGNED) DESC LIMIT 1";
        
        try (Connection conn = ConexionMySQL.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int maxNum = 0;
            if (rs.next()) {
                String maxOrden = rs.getString("numero_orden");
                if (maxOrden != null && !maxOrden.isEmpty()) {
                    try {
                        maxNum = Integer.parseInt(maxOrden);
                    } catch (NumberFormatException e) {
                        // Si no se puede parsear, usar 0
                        maxNum = 0;
                    }
                }
            }
            
            // Retornar el siguiente número con formato de 6 dígitos
            return String.format("%06d", maxNum + 1);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente número de orden: " + e.getMessage());
            e.printStackTrace();
            // Fallback: usar timestamp o contador simple
            long timestamp = System.currentTimeMillis();
            return String.format("%06d", (int)(timestamp % 1000000));
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Orden
     */
    private static Orden mapearOrden(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha_registro");
        Time hora = rs.getTime("hora_registro");
        
        // Formatear fecha a DD/MM/YYYY
        String fechaStr = "";
        if (fecha != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            fechaStr = sdf.format(fecha);
        }
        
        // Formatear hora a HH:mm
        String horaStr = "";
        if (hora != null) {
            horaStr = hora.toString().substring(0, 5); // Formato HH:mm
        }
        
        String sexo = "";
        try {
            sexo = rs.getString("sexo");
            if (sexo == null) sexo = "";
        } catch (SQLException e) {
            // Columna sexo puede no existir en BD antigua
            sexo = "";
        }
        
        Orden orden = new Orden(
            rs.getString("numero_orden"),
            rs.getString("numero_factura"),
            rs.getString("numero_control"),
            rs.getString("numero_lote"),
            fechaStr,
            horaStr,
            rs.getString("codigo_paciente"),
            rs.getString("cedula"),
            rs.getString("nombres"),
            rs.getString("apellidos"),
            rs.getString("direccion"),
            rs.getString("telefono"),
            rs.getString("correo"),
            rs.getString("codigo_empresa"),
            rs.getString("empresa"),
            sexo,
            new ArrayList<>(), // Se llenará después
            rs.getDouble("total")
        );
        
        // Establecer el estatus desde la BD
        String estatus = rs.getString("estatus");
        if (estatus != null && !estatus.isEmpty()) {
            orden.setEstatus(estatus);
        }
        
        return orden;
    }

    /**
     * Verifica si la columna sexo existe en la tabla ordenes
     */
    private static boolean verificarColumnaSexo() {
        try (Connection conn = ConexionMySQL.obtenerConexion()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "ordenes", "sexo");
            boolean existe = columns.next();
            columns.close();
            return existe;
        } catch (SQLException e) {
            // Si hay error, asumir que no existe para evitar fallos
            System.err.println("⚠️ No se pudo verificar columna sexo, asumiendo que no existe: " + e.getMessage());
            return false;
        }
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

