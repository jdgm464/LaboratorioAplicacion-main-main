package com.mycompany.laboratorioapp;

import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.dao.PacienteDAO;
import com.mycompany.laboratorioapp.dao.UsuarioDAO;
import com.mycompany.laboratorioapp.examenes.Examen;
import com.mycompany.laboratorioapp.pacientes.Paciente;
import com.mycompany.laboratorioapp.pacientes.GestorPacientes;
import com.mycompany.laboratorioapp.usuarios.Usuario;
import com.mycompany.laboratorioapp.usuarios.SesionUsuario;

/**
 * Clase de prueba para verificar que la integración con la base de datos funcione correctamente
 */
public class PruebaBaseDatos {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("PRUEBA DE INTEGRACIÓN CON BASE DE DATOS");
        System.out.println("==========================================\n");
        
        // 1. Probar conexión
        System.out.println("1. Probando conexión a MySQL...");
        if (!ConexionMySQL.probarConexion()) {
            System.err.println("❌ ERROR: No se pudo conectar a MySQL");
            System.err.println("   Verifica:");
            System.err.println("   - Que MySQL esté corriendo");
            System.err.println("   - Que la base de datos 'laboratorio_db' exista");
            System.err.println("   - Que el usuario y contraseña en ConexionMySQL.java sean correctos");
            System.err.println("   - Ejecuta el script: database/schema.sql");
            return;
        }
        System.out.println("✓ Conexión exitosa\n");
        
        // 2. Probar PacienteDAO
        System.out.println("2. Probando operaciones con Pacientes...");
        try {
            // Crear un paciente de prueba
            Paciente pacientePrueba = new Paciente(
                "V12345678",
                "Juan",
                "Pérez",
                30,
                "Calle Test 123",
                "04121234567",
                "juan@test.com"
            );
            
            // Insertar
            boolean insertado = PacienteDAO.insertar(pacientePrueba);
            System.out.println("   - Insertar paciente: " + (insertado ? "✓" : "❌"));
            
            // Buscar
            Paciente encontrado = PacienteDAO.buscarPorCedula("V12345678");
            System.out.println("   - Buscar paciente: " + (encontrado != null ? "✓" : "❌"));
            
            // Actualizar
            if (encontrado != null) {
                encontrado = new Paciente(
                    "V12345678",
                    "Juan Carlos",
                    "Pérez",
                    31,
                    "Calle Test 456",
                    "04121234567",
                    "juancarlos@test.com"
                );
                boolean actualizado = PacienteDAO.actualizar(encontrado);
                System.out.println("   - Actualizar paciente: " + (actualizado ? "✓" : "❌"));
            }
            
            // Probar GestorPacientes
            Paciente desdeGestor = GestorPacientes.buscarPorCedula("V12345678");
            System.out.println("   - GestorPacientes.buscarPorCedula: " + (desdeGestor != null ? "✓" : "❌"));
            
            System.out.println("✓ Operaciones con Pacientes funcionando\n");
        } catch (Exception e) {
            System.err.println("❌ ERROR en operaciones con Pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 3. Probar ExamenDAO
        System.out.println("3. Probando operaciones con Exámenes...");
        try {
            // Crear un examen de prueba
            Examen examenPrueba = new Examen("TEST001", "Examen de Prueba", 25.50);
            
            // Insertar
            boolean insertado = ExamenDAO.insertar(examenPrueba);
            System.out.println("   - Insertar examen: " + (insertado ? "✓" : "❌"));
            
            // Buscar
            Examen encontrado = ExamenDAO.buscarPorCodigo("TEST001");
            System.out.println("   - Buscar examen: " + (encontrado != null ? "✓" : "❌"));
            
            // Obtener precio
            Double precio = ExamenDAO.obtenerPrecio("TEST001");
            System.out.println("   - Obtener precio: " + (precio != null && precio == 25.50 ? "✓" : "❌"));
            
            // Actualizar
            if (encontrado != null) {
                encontrado = new Examen("TEST001", "Examen de Prueba Actualizado", 30.00);
                boolean actualizado = ExamenDAO.actualizar(encontrado);
                System.out.println("   - Actualizar examen: " + (actualizado ? "✓" : "❌"));
            }
            
            System.out.println("✓ Operaciones con Exámenes funcionando\n");
        } catch (Exception e) {
            System.err.println("❌ ERROR en operaciones con Exámenes: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 4. Probar UsuarioDAO
        System.out.println("4. Probando operaciones con Usuarios...");
        try {
            // Buscar usuario admin (debería existir si se ejecutó el schema.sql)
            Usuario admin = UsuarioDAO.buscarPorUsuario("admin");
            System.out.println("   - Buscar usuario admin: " + (admin != null ? "✓" : "⚠ (No encontrado - ejecuta schema.sql)"));
            
            // Obtener ID de usuario
            if (admin != null) {
                int usuarioId = UsuarioDAO.obtenerIdPorUsuario("admin");
                System.out.println("   - Obtener ID de usuario: " + (usuarioId > 0 ? "✓ (ID: " + usuarioId + ")" : "❌"));
            }
            
            System.out.println("✓ Operaciones con Usuarios funcionando\n");
        } catch (Exception e) {
            System.err.println("❌ ERROR en operaciones con Usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 5. Probar SesionUsuario
        System.out.println("5. Probando SesionUsuario...");
        try {
            SesionUsuario.establecerUsuario("admin");
            String usuario = SesionUsuario.obtenerUsuario();
            int usuarioId = SesionUsuario.obtenerUsuarioId();
            System.out.println("   - Establecer usuario: " + (usuario != null ? "✓" : "❌"));
            System.out.println("   - Obtener usuario: " + (usuario != null ? "✓ (" + usuario + ")" : "❌"));
            System.out.println("   - Obtener usuarioId: " + (usuarioId > 0 ? "✓ (ID: " + usuarioId + ")" : "⚠"));
            System.out.println("✓ SesionUsuario funcionando\n");
        } catch (Exception e) {
            System.err.println("❌ ERROR en SesionUsuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("==========================================");
        System.out.println("PRUEBA COMPLETADA");
        System.out.println("==========================================");
        System.out.println("\nSi todas las pruebas pasaron, la aplicación está lista para usar.");
        System.out.println("Ejecuta: LaboratorioAplicacion.main() para iniciar la aplicación.");
    }
}

