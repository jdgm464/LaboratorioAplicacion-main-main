package com.mycompany.laboratorioapp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Registro {
    private final JFrame frame;
    private final JTextField nombreField;
    private final JTextField usuarioField;
    private final JPasswordField passwordField;
    private final JComboBox<String> rolCombo;
    private final login loginRef;

    public Registro(login loginRef) {
        this.loginRef = loginRef;

        frame = new JFrame("Registro de Usuario");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(420, 300);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        nombreField = new JTextField(15);
        frame.add(nombreField, gbc);

        // Usuario
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usuarioField = new JTextField(15);
        frame.add(usuarioField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        frame.add(passwordField, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Bioanalista"});
        frame.add(rolCombo, gbc);

        // Botón registrar
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton registrarButton = new JButton("Registrar");
        registrarButton.addActionListener(e -> registrarUsuario());
        frame.add(registrarButton, gbc);

        frame.setVisible(true);
    }

    private void registrarUsuario() {
        String nombre = nombreField.getText();
        String usuario = usuarioField.getText();
        String password = new String(passwordField.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamamos al método estático de Login
        login.registrarUsuario(nombre, usuario, password, rol);

        JOptionPane.showMessageDialog(frame, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose(); // Cerrar ventana de registro
    }
}