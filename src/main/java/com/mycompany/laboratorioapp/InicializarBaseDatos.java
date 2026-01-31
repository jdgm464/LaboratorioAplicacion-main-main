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
    private static final String PUERTO = "5432";
    private static final String BASE_DATOS = "laboratorio_db";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Crea la base de datos si no existe (PostgreSQL)
     */
    public static boolean crearBaseDatosSiNoExiste() {
        String urlPostgres = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/postgres";
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(urlPostgres, USUARIO, PASSWORD);
            try (Statement stmt = conn.createStatement()) {
                String sql = "SELECT 1 FROM pg_database WHERE datname = '" + BASE_DATOS + "'";
                var rs = stmt.executeQuery(sql);
                if (!rs.next()) {
                    stmt.executeUpdate("CREATE DATABASE " + BASE_DATOS);
                    System.out.println("✓ Base de datos '" + BASE_DATOS + "' creada");
                } else {
                    System.out.println("✓ Base de datos '" + BASE_DATOS + "' ya existe");
                }
            }
            conn.close();
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver PostgreSQL no encontrado");
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
        if (ConexionPostgreSQL.ejecutarScriptSQL(rutaScript)) {
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
            System.err.println("1. Que PostgreSQL esté corriendo");
            System.err.println("2. Que el usuario y contraseña en InicializarBaseDatos.java sean correctos");
        }
    }
}

