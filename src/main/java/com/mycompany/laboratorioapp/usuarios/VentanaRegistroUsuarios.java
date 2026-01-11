package com.mycompany.laboratorioapp.usuarios;

public class VentanaRegistroUsuarios {
    private javax.swing.JFrame frame;
    private javax.swing.table.DefaultTableModel model;
    private javax.swing.JTable table;

    public void mostrar() {
        frame = new javax.swing.JFrame("Registros de Usuarios");
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(720, 420);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new java.awt.BorderLayout());

        // Tabla con columnas similares a la imagen: Cédula, Nombres, Apellidos, Perfil
        String[] cols = {"Cédula", "Nombres", "Apellidos", "Perfil"};
        model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Cargar datos desde la base de datos
        refrescarTabla();

        table = new javax.swing.JTable(model);
        table.setRowHeight(22);
        frame.add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.CENTER);

        // Barra de acciones
        javax.swing.JPanel acciones = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 10));
        javax.swing.JButton btnNuevo = new javax.swing.JButton("Nuevo");
        javax.swing.JButton btnModificar = new javax.swing.JButton("Modificar");
        javax.swing.JButton btnEliminar = new javax.swing.JButton("Eliminar");
        javax.swing.JButton btnSalir = new javax.swing.JButton("Salir");
        acciones.add(btnNuevo);
        acciones.add(btnModificar);
        acciones.add(btnEliminar);
        acciones.add(btnSalir);
        frame.add(acciones, java.awt.BorderLayout.SOUTH);

        // Acciones
        btnNuevo.addActionListener(e -> {
            Registro registro = new Registro();
            registro.setVentanaPadre(this);
            // recargar cuando vuelva el foco
            frame.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                @Override public void windowGainedFocus(java.awt.event.WindowEvent e1) { 
                    refrescarTabla(); 
                    frame.removeWindowFocusListener(this); 
                }
            });
        });

        btnModificar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { javax.swing.JOptionPane.showMessageDialog(frame, "Seleccione un usuario"); return; }
            // Obtener el usuario de la fila seleccionada usando la cédula o nombres
            String cedula = (String) table.getValueAt(row, 0);
            String nombres = (String) table.getValueAt(row, 1);
            String apellidos = (String) table.getValueAt(row, 2);
            
            // Buscar el usuario en la base de datos por cédula o nombres
            Usuario u = null;
            if (cedula != null && !cedula.trim().isEmpty()) {
                // Buscar por cédula en la BD
                java.util.List<Usuario> usuarios = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerTodos();
                for (Usuario usuario : usuarios) {
                    if (cedula.equals(usuario.getCedula())) {
                        u = usuario;
                        break;
                    }
                }
            }
            
            // Si no se encuentra por cédula, buscar por nombres y apellidos
            if (u == null && nombres != null && apellidos != null) {
                java.util.List<Usuario> usuarios = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerTodos();
                for (Usuario usuario : usuarios) {
                    if (nombres.equals(usuario.getNombres()) && apellidos.equals(usuario.getApellidos())) {
                        u = usuario;
                        break;
                    }
                }
            }
            
            // Si aún no se encuentra, intentar con login.buscarUsuarioPorNombre
            if (u == null) {
                String username = javax.swing.JOptionPane.showInputDialog(frame, "Usuario (username) a modificar:");
                if (username == null || username.isBlank()) return;
                u = login.buscarUsuarioPorNombre(username.trim());
            }
            
            if (u == null) { 
                javax.swing.JOptionPane.showMessageDialog(frame, "Usuario no encontrado", "Error", javax.swing.JOptionPane.ERROR_MESSAGE); 
                return; 
            }
            Registro registro = new Registro(u);
            registro.setVentanaPadre(this);
            frame.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                @Override public void windowGainedFocus(java.awt.event.WindowEvent e1) { 
                    refrescarTabla(); 
                    frame.removeWindowFocusListener(this); 
                }
            });
        });

        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { javax.swing.JOptionPane.showMessageDialog(frame, "Seleccione un usuario"); return; }
            // Eliminamos por "usuario" si lo conocemos; aquí pedimos confirmación manual
            String username = javax.swing.JOptionPane.showInputDialog(frame, "Usuario a eliminar (username):");
            if (username != null && !username.isBlank()) {
                // Eliminar de la base de datos
                boolean ok = com.mycompany.laboratorioapp.dao.UsuarioDAO.eliminar(username.trim());
                if (ok) {
                    // También eliminar del sistema de archivos para compatibilidad
                    login.eliminarUsuario(username.trim());
                    javax.swing.JOptionPane.showMessageDialog(frame, "Usuario eliminado", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    refrescarTabla();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(frame, "Usuario no encontrado", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSalir.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }
    
    /**
     * Refresca la tabla cargando usuarios desde la base de datos
     */
    public void refrescarTabla() {
        if (model == null) return;
        
        model.setRowCount(0);
        
        // Cargar desde la base de datos
        java.util.List<Usuario> usuarios = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerTodos();
        
        // Si no hay usuarios en la BD, cargar desde el sistema de archivos para compatibilidad
        if (usuarios.isEmpty()) {
            usuarios = login.listarUsuarios();
        }
        
        for (Usuario u : usuarios) {
            model.addRow(new Object[]{
                u.getCedula() != null ? u.getCedula() : "", 
                u.getNombres() != null ? u.getNombres() : "", 
                u.getApellidos() != null ? u.getApellidos() : "", 
                u.getRol() != null ? u.getRol() : ""
            });
        }
    }
}

