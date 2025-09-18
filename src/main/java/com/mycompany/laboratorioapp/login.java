package com.mycompany.laboratorioapp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class login {
    private final JFrame frame;
    private final JTextField usuarioField;
    private final JPasswordField passwordField;
    private final JComboBox<String> rolCombo;

    // Simulamos una base de datos en memoria
    private static final Map<String, Usuario> usuariosRegistrados = new HashMap<>();

    public login() {
        // Usuario admin de ejemplo
        usuariosRegistrados.put("admin", new Usuario("Administrador", "admin", "1234"));
        usuariosRegistrados.put("bio1", new Usuario("Bioanalista", "bio1", "1234"));

        frame = new JFrame("Login - Maberoca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 260);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usuarioField = new JTextField(15);
        frame.add(usuarioField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        frame.add(passwordField, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Bioanalista"});
        frame.add(rolCombo, gbc);

        // Botón iniciar sesión
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(e -> autenticar());
        frame.add(loginButton, gbc);

        // Botón crear cuenta
        gbc.gridy = 4;
        JButton registroButton = new JButton("Crear Cuenta");
        registroButton.addActionListener(e -> {
            new Registro(this);
        });
        frame.add(registroButton, gbc);

        frame.setVisible(true);
    }

    private void autenticar() {
        String usuario = usuarioField.getText();
        String password = new String(passwordField.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        Usuario u = usuariosRegistrados.get(usuario);

        if (u != null && u.getPassword().equals(password) && u.getRol().equals(rol)) {
            abrirInterfaz(usuario, rol);
        } else {
            JOptionPane.showMessageDialog(frame, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirInterfaz(String usuario, String rol) {
        frame.dispose();
        SwingUtilities.invokeLater(() -> new InterfazPrincipal(usuario, rol).mostrar());
    }

    // Método para registrar un usuario desde la ventana Registro
    public static void registrarUsuario(String nombre, String usuario, String password, String rol) {
        usuariosRegistrados.put(usuario, new Usuario(rol, usuario, password));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}