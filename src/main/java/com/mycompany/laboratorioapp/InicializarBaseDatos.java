package com.mycompany.laboratorioapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Clase para inicializar la base de datos si no existe
 */
public class InicializarBaseDatos {
    
    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "laboratorio_db";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";
    
    /**
     * Crea la base de datos si no existe
     */
    public static boolean crearBaseDatosSiNoExiste() {
        String urlSinBD = "jdbc:mysql://" + HOST + ":" + PUERTO + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(urlSinBD, USUARIO, PASSWORD);
            
            try (Statement stmt = conn.createStatement()) {
                // Crear base de datos si no existe
                String sql = "CREATE DATABASE IF NOT EXISTS " + BASE_DATOS + 
                            " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
                stmt.executeUpdate(sql);
                System.out.println("✓ Base de datos '" + BASE_DATOS + "' verificada/creada");
            }
            
            conn.close();
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL no encontrado");
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error al crear base de datos: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicializa la base de datos completa (crea BD y ejecuta schema)
     */
    public static boolean inicializar() {
        System.out.println("Inicializando base de datos...");
        
        // 1. Crear base de datos si no existe
        if (!crearBaseDatosSiNoExiste()) {
            return false;
        }
        
        // 2. Ejecutar script SQL
        System.out.println("Ejecutando script SQL...");
        String rutaScript = "database/schema.sql";
        if (ConexionMySQL.ejecutarScriptSQL(rutaScript)) {
            System.out.println("✓ Base de datos inicializada correctamente");
            return true;
        } else {
            System.err.println("⚠ Error al ejecutar script SQL. Verifica que el archivo exista.");
            return false;
        }
    }
    
    public static void main(String[] args) {
        if (inicializar()) {
            System.out.println("\n✅ Base de datos lista para usar!");
            System.out.println("Ahora puedes ejecutar la aplicación.");
        } else {
            System.err.println("\n❌ Error al inicializar la base de datos");
            System.err.println("Verifica:");
            System.err.println("1. Que MySQL esté corriendo");
            System.err.println("2. Que el usuario y contraseña en InicializarBaseDatos.java sean correctos");
        }
    }
}

