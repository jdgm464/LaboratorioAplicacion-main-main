
package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class InterfazPrincipal {

    private JFrame mainFrame;
    private static final double TASA_BCV = 131.12; // Valor fijo del BCV
    private JTextField tasaManual;
    private final String usuarioLogueado;
    private final String rolUsuario;

    public InterfazPrincipal(String usuario, String rol) {
        this.usuarioLogueado = usuario;
        this.rolUsuario = rol;
        configurarVentanaPrincipal();
    }

    private void configurarVentanaPrincipal() {
        mainFrame = new JFrame("Sistema de Laboratorio - Maberoca");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 750);
        mainFrame.setMinimumSize(new Dimension(1000, 650));
        mainFrame.setLayout(new BorderLayout());

        agregarMenuSuperior();
        agregarPanelIzquierdo();
        agregarPanelCentral();
    }

    private void agregarMenuSuperior() {
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        menuPanel.setBackground(Color.WHITE);

        String[] menuItems = {"Órdenes", "Resultados", "Pacientes", "Presupuesto", "Archivos"};

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setBackground(new Color(0, 191, 255));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(120, 40));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1, true));

            // Acción al hacer clic
            button.addActionListener(e -> {
                switch (item) {
                    case "Órdenes" -> {
                        VentanaOrdenes ventana = new VentanaOrdenes();
                        ventana.mostrar();
                    }
                    case "Resultados" -> {
                        VentanaResultados ventana = new VentanaResultados();
                        ventana.mostrar();
                    }
                    case "Pacientes" -> {
                        VentanaPacientes ventana = new VentanaPacientes();
                        ventana.mostrar();
                    }
                    case "Presupuesto" -> {
                        VentanaPresupuestos ventana = new VentanaPresupuestos();
                        ventana.mostrar();
                    }
                    case "Archivos" -> {
                        VentanaArchivos ventana = new VentanaArchivos();
                        ventana.mostrar();
                    }
                }
            });

            // Limitar funciones si es usuario
            if (rolUsuario.equals("Usuario")
                    && (item.equals("Resultados") || item.equals("Pacientes") || item.equals("Presupuesto") || item.equals("Archivos"))) {
                button.setEnabled(false);
            }

            menuPanel.add(button);
        }

        mainFrame.add(menuPanel, BorderLayout.NORTH);
    }

    private void agregarPanelIzquierdo() {
    JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.setBackground(Color.WHITE);
    leftPanel.setPreferredSize(new Dimension(220, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new java.awt.Insets(15, 5, 15, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;

    // ---------- Nombre de la aplicación ----------
    JLabel tituloApp = new JLabel("Medisoft");
    tituloApp.setFont(new Font("Arial", Font.BOLD, 22));
    tituloApp.setForeground(new Color(0, 102, 204)); // Azul elegante
    leftPanel.add(tituloApp, gbc);

    // ---------- Icono BCV ----------
    gbc.gridy++;
    JLabel iconBCV = new JLabel("<html>💲");
    iconBCV.setFont(new Font("Arial", Font.PLAIN, 36));
    leftPanel.add(iconBCV, gbc);

    // ---------- Combo selección automática/manual ----------
    gbc.gridy++;
    JComboBox<String> tasaCombo = new JComboBox<>(new String[]{"Automática", "Manual"});
    tasaCombo.setPreferredSize(new Dimension(160, 28));
    tasaCombo.setFont(new Font("Arial", Font.PLAIN, 14));
    leftPanel.add(tasaCombo, gbc);

    // ---------- Campo para la tasa ----------
    gbc.gridy++;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 5, 10, 5); // margencitos arriba/abajo

    tasaManual = new JTextField(String.format("%.2f", TASA_BCV));
    tasaManual.setPreferredSize(new Dimension(140, 36)); // ancho más reducido
    tasaManual.setFont(new Font("Arial", Font.BOLD, 18));
    tasaManual.setHorizontalAlignment(JTextField.CENTER);
    tasaManual.setBackground(Color.WHITE);
    tasaManual.setForeground(Color.BLACK);
    tasaManual.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    tasaManual.setEnabled(false);
    leftPanel.add(tasaManual, gbc);

    tasaCombo.addActionListener(e -> {
        if ("Automática".equals(tasaCombo.getSelectedItem())) {
            tasaManual.setText(String.format("%.2f", TASA_BCV));
            tasaManual.setEnabled(false);
        } else {
            tasaManual.setText("0.00");
            tasaManual.setEnabled(true);
        }
    });

    // ---------- Fecha y hora ----------
    gbc.gridy++;
    String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    String hora = new SimpleDateFormat("HH:mm").format(new Date());
    JLabel fechaHoraLabel = new JLabel("<html>📅 " + fecha + "<br>⏰ " + hora + "</html>");
    fechaHoraLabel.setFont(new Font("Arial", Font.BOLD, 14));
    leftPanel.add(fechaHoraLabel, gbc);

    // ---------- Usuario logueado ----------
    gbc.gridy++;
    JLabel usuarioLabel = new JLabel("<html>👤 " + usuarioLogueado + " (" + rolUsuario + ")</html>");
    usuarioLabel.setFont(new Font("Arial", Font.BOLD, 16));
    leftPanel.add(usuarioLabel, gbc);

    mainFrame.add(leftPanel, BorderLayout.WEST);
}

    private void agregarPanelCentral() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

    // ...panel central vacío (sin imagen)...

        mainFrame.add(centerPanel, BorderLayout.CENTER);
    }

    public void mostrar() {
        mainFrame.setVisible(true);
    }
}