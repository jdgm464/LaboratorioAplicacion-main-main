package com.mycompany.laboratorioapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase utilitaria para gestionar la conexión a PostgreSQL
 * Para conectar desde Java necesitas el driver JDBC de PostgreSQL en pom.xml
 */
public class ConexionPostgreSQL {

    private static final String HOST = "localhost";
    private static final String PUERTO = "5432";
    private static final String BASE_DATOS = "postgres";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "1234";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS;

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión exitosa a PostgreSQL");
            return conexion;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no encontrado. Verifica la dependencia en pom.xml", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar con PostgreSQL: " + e.getMessage());
            throw e;
        }
    }

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

    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            return false;
        }
    }

    public static boolean ejecutarScriptSQL(String rutaArchivo) {
        try (Connection conn = obtenerConexion();
             BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            StringBuilder script = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                script.append(linea).append("\n");
            }
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

    public static void main(String[] args) {
        System.out.println("Probando conexión a PostgreSQL...");
        if (probarConexion()) {
            System.out.println("✓ Conexión exitosa!");
        } else {
            System.out.println("✗ Error en la conexión. Verifica:");
            System.out.println("  1. Que PostgreSQL esté corriendo");
            System.out.println("  2. Que la base de datos '" + BASE_DATOS + "' exista");
            System.out.println("  3. Usuario y contraseña en ConexionPostgreSQL.java");
            System.out.println("  4. mvn clean install");
        }
    }
}
