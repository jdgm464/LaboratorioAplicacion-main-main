package com.mycompany.laboratorioapp.usuarios;

import com.mycompany.laboratorioapp.dao.UsuarioDAO;
import com.mycompany.laboratorioapp.InterfazPrincipal;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class RegistroSimple {
    private final JFrame frame;
    private final JTextField cedulaField;
    private final JTextField nombreField;
    private final JTextField apellidoField;
    private final JTextField usuarioField;
    private final JPasswordField passwordField;
    private final JComboBox<String> rolCombo;
    private VentanaRegistroUsuarios ventanaPadre;

    public RegistroSimple() {
        this(null);
    }
    
    public RegistroSimple(VentanaRegistroUsuarios ventanaPadre) {
        this.ventanaPadre = ventanaPadre;
        frame = new JFrame("Registro de Usuario");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(420, 350);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cédula (arriba del nombre, como en la base de datos)
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        cedulaField = new JTextField(15);
        frame.add(cedulaField, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        nombreField = new JTextField(15);
        frame.add(nombreField, gbc);

        // Apellido
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        apellidoField = new JTextField(15);
        frame.add(apellidoField, gbc);

        // Usuario
        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usuarioField = new JTextField(15);
        frame.add(usuarioField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 4;
        frame.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        frame.add(passwordField, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 5;
        frame.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Bioanalista"});
        frame.add(rolCombo, gbc);

        // Botón registrar
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton registrarButton = new JButton("Registrar");
        registrarButton.addActionListener(e -> registrarUsuario());
        frame.add(registrarButton, gbc);

        frame.setVisible(true);
    }

    private void registrarUsuario() {
        String cedula = cedulaField.getText().trim();
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = new String(passwordField.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios (excepto cédula)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear objeto Usuario con cédula
        Usuario u = new Usuario(
                cedula.isEmpty() ? "" : cedula,
                nombre,
                apellido,
                usuario,
                password,
                rol,
                false, false, false, // módulos
                false, false, false, false, // autorizaciones básicas
                false, false, // control precios/resultados
                false, false, // anular, modificar órdenes
                false // permisos administrativos
        );

        // Guardar en la base de datos
        boolean guardado = UsuarioDAO.insertar(u);
        
        if (guardado) {
            // También guardar en el sistema de archivos para compatibilidad
            login.registrarUsuarioExtendido(u);
            
            JOptionPane.showMessageDialog(frame, 
                "Usuario registrado exitosamente:\n" + 
                nombre + " " + apellido + " - " + usuario + " (" + rol + ")\n\n" +
                "Será redirigido a la interfaz principal...", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refrescar la ventana de registro de usuarios si está abierta
            if (ventanaPadre != null) {
                ventanaPadre.refrescarTabla();
            }
            
            // Cerrar la ventana de registro
            frame.dispose();
            
            // Abrir la interfaz principal con el usuario recién registrado
            SwingUtilities.invokeLater(() -> {
                // Establecer el usuario en la sesión
                SesionUsuario.establecerUsuario(usuario);
                // Abrir la interfaz principal
                new InterfazPrincipal(usuario, rol).mostrar();
            });
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Error al registrar el usuario en la base de datos.\n" +
                "Verifique que el usuario no exista ya.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setVentanaPadre(VentanaRegistroUsuarios ventanaPadre) {
        this.ventanaPadre = ventanaPadre;
    }
}

