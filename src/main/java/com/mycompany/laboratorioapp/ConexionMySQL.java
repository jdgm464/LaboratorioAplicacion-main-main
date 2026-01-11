package com.mycompany.laboratorioapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase utilitaria para gestionar la conexión a MySQL
 * 
 * Nota: phpMyAdmin es solo una herramienta web para administrar MySQL.
 * Para conectar desde Java, necesitas:
 * 1. El driver JDBC de MySQL (ya agregado en pom.xml)
 * 2. Esta clase de conexión
 * 3. Configurar los datos de conexión (host, puerto, base de datos, usuario, contraseña)
 */
public class ConexionMySQL {
    
    // Configuración de conexión - AJUSTA ESTOS VALORES SEGÚN TU SERVIDOR
    private static final String HOST = "localhost";  // o la IP de tu servidor MySQL
    private static final String PUERTO = "3306";      // Puerto por defecto de MySQL
    private static final String BASE_DATOS = "laboratorio_db";  // Nombre de tu base de datos
    private static final String USUARIO = "root";     // Tu usuario de MySQL
    private static final String PASSWORD = "";        // Tu contraseña de MySQL
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS 
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    /**
     * Obtiene una conexión a la base de datos MySQL
     * @return Connection objeto de conexión
     * @throws SQLException si hay un error al conectar
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Cargar el driver (desde Java 6 no es necesario, pero es buena práctica)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer la conexión
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión exitosa a MySQL");
            return conexion;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. Verifica que la dependencia esté en pom.xml", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cierra una conexión de forma segura
     * @param conexion La conexión a cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Prueba la conexión a la base de datos
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ejecuta un script SQL desde un archivo
     * @param rutaArchivo Ruta al archivo .sql
     * @return true si se ejecutó correctamente, false en caso contrario
     */
    public static boolean ejecutarScriptSQL(String rutaArchivo) {
        try (Connection conn = obtenerConexion();
             BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            
            StringBuilder script = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                script.append(linea).append("\n");
            }
            
            // Dividir el script en sentencias individuales
            String[] sentencias = script.toString().split(";");
            
            try (Statement stmt = conn.createStatement()) {
                for (String sentencia : sentencias) {
                    sentencia = sentencia.trim();
                    if (!sentencia.isEmpty() && !sentencia.startsWith("--")) {
                        stmt.execute(sentencia);
                    }
                }
            }
            
            System.out.println("Script SQL ejecutado correctamente");
            return true;
            
        } catch (SQLException | IOException e) {
            System.err.println("Error al ejecutar script SQL: " + e.getMessage());
            return false;
        }
    }
    
    // Método main para probar la conexión
    public static void main(String[] args) {
        System.out.println("Probando conexión a MySQL...");
        if (probarConexion()) {
            System.out.println("✓ Conexión exitosa!");
        } else {
            System.out.println("✗ Error en la conexión. Verifica:");
            System.out.println("  1. Que MySQL esté corriendo");
            System.out.println("  2. Que la base de datos '" + BASE_DATOS + "' exista");
            System.out.println("  3. Que el usuario y contraseña sean correctos");
            System.out.println("  4. Que hayas ejecutado: mvn clean install");
        }
    }
}

