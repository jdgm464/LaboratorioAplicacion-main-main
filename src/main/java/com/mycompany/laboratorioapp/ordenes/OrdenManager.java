package com.mycompany.laboratorioapp.ordenes;

import com.mycompany.laboratorioapp.usuarios.SesionUsuario;
import com.mycompany.laboratorioapp.usuarios.Usuario;
import com.mycompany.laboratorioapp.dao.OrdenDAO;
import java.util.List;

public class OrdenManager {
    
    public static void agregarOrden(Orden orden) {
        int usuarioId = SesionUsuario.obtenerUsuarioId();
        
        // Si no se puede obtener el ID, intentar obtenerlo de nuevo o usar un usuario por defecto
        if (usuarioId == -1) {
            String nombreUsuario = SesionUsuario.obtenerUsuario();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                // Intentar obtener el ID nuevamente
                usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario(nombreUsuario);
                
                // Si aún no se encuentra, intentar crear un usuario básico
                if (usuarioId == -1) {
                    try {
                        // Crear usuario básico en la BD si no existe
                        Usuario nuevoUsuario = new Usuario(
                            "",           // cedula
                            nombreUsuario, // nombres
                            "",           // apellidos
                            nombreUsuario, // usuario
                            "",           // password
                            "Usuario",    // rol
                            false,        // moduloVentasLaboratorio
                            false,        // moduloCompras
                            false,        // moduloAdministrativos
                            false,        // puedeDescuento
                            false,        // puedeRecargo
                            false,        // puedeCierre
                            false,        // puedeCortesias
                            false,        // puedeControlPrecios
                            false,        // puedeControlResultados
                            false,        // puedeAnular
                            false,        // puedeModificarOrdenes
                            false         // permisosAdministrativos
                        );
                        if (com.mycompany.laboratorioapp.dao.UsuarioDAO.insertar(nuevoUsuario)) {
                            usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario(nombreUsuario);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al crear usuario: " + e.getMessage());
                    }
                }
                
                // Si aún no se puede obtener, usar usuario admin por defecto
                if (usuarioId == -1) {
                    usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario("admin");
                    if (usuarioId == -1) {
                        System.err.println("Error: No se pudo obtener el ID del usuario. La orden no se guardará en la base de datos.");
                        javax.swing.JOptionPane.showMessageDialog(null, 
                            "Error: No se pudo obtener el ID del usuario.\n" +
                            "Asegúrate de que el usuario existe en la base de datos.\n" +
                            "Ejecuta el script database/schema.sql para crear el usuario admin.",
                            "Error de Usuario", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } else {
                System.err.println("Error: No hay usuario en sesión. La orden no se guardará en la base de datos.");
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error: No hay usuario en sesión.\n" +
                    "Por favor, inicia sesión nuevamente.",
                    "Error de Sesión", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Insertar la orden
        boolean insertado = OrdenDAO.insertar(orden, usuarioId);
        if (insertado) {
            System.out.println("✓ Orden guardada exitosamente: " + orden.getNumeroOrden());
        } else {
            System.err.println("❌ Error al guardar la orden: " + orden.getNumeroOrden());
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error al guardar la orden en la base de datos.\n" +
                "Verifica la conexión a MySQL y que las tablas existan.",
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Agrega una orden y retorna si fue exitoso
     * @param orden La orden a agregar
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean agregarOrdenConResultado(Orden orden) {
        int usuarioId = SesionUsuario.obtenerUsuarioId();
        
        // Si no se puede obtener el ID, intentar obtenerlo de nuevo o usar un usuario por defecto
        if (usuarioId == -1) {
            String nombreUsuario = SesionUsuario.obtenerUsuario();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                // Intentar obtener el ID nuevamente
                usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario(nombreUsuario);
                
                // Si aún no se encuentra, intentar crear un usuario básico
                if (usuarioId == -1) {
                    try {
                        // Crear usuario básico en la BD si no existe
                        Usuario nuevoUsuario = new Usuario(
                            "",           // cedula
                            nombreUsuario, // nombres
                            "",           // apellidos
                            nombreUsuario, // usuario
                            "",           // password
                            "Usuario",    // rol
                            false,        // moduloVentasLaboratorio
                            false,        // moduloCompras
                            false,        // moduloAdministrativos
                            false,        // puedeDescuento
                            false,        // puedeRecargo
                            false,        // puedeCierre
                            false,        // puedeCortesias
                            false,        // puedeControlPrecios
                            false,        // puedeControlResultados
                            false,        // puedeAnular
                            false,        // puedeModificarOrdenes
                            false         // permisosAdministrativos
                        );
                        if (com.mycompany.laboratorioapp.dao.UsuarioDAO.insertar(nuevoUsuario)) {
                            usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario(nombreUsuario);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al crear usuario: " + e.getMessage());
                    }
                }
                
                // Si aún no se puede obtener, usar usuario admin por defecto
                if (usuarioId == -1) {
                    usuarioId = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerIdPorUsuario("admin");
                }
            }
        }
        
        if (usuarioId == -1) {
            System.err.println("❌ Error: No se pudo obtener el ID del usuario. La orden no se guardará.");
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error: No se pudo obtener el ID del usuario.\n" +
                "Asegúrate de que el usuario existe en la base de datos.\n" +
                "Ejecuta el script database/schema.sql para crear el usuario admin.",
                "Error de Usuario", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        System.out.println("📝 Intentando guardar orden: " + orden.getNumeroOrden() + " con usuario_id: " + usuarioId);
        // Insertar la orden
        boolean resultado = OrdenDAO.insertar(orden, usuarioId);
        if (resultado) {
            System.out.println("✅ Orden guardada exitosamente: " + orden.getNumeroOrden());
        } else {
            System.err.println("❌ Falló al guardar la orden: " + orden.getNumeroOrden());
        }
        return resultado;
    }

    public static void eliminarOrden(String numeroOrden) {
        if (numeroOrden == null) return;
        OrdenDAO.eliminar(numeroOrden);
    }

    public static List<Orden> getOrdenes() {
        return OrdenDAO.obtenerTodas();
    }

    public static Orden buscarPorNumero(String numeroOrden) {
        if (numeroOrden == null) return null;
        return OrdenDAO.buscarPorNumero(numeroOrden);
    }

    // ====== Actualizar estatus y persistir ======
    public static void actualizarEstatus(String numeroOrden, String nuevoEstatus) {
        if (numeroOrden == null) return;
        Orden orden = OrdenDAO.buscarPorNumero(numeroOrden);
        if (orden != null) {
            orden.setEstatus(nuevoEstatus);
            OrdenDAO.actualizar(orden);
        }
    }
}

