package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VentanaPresupuestos {

    private JFrame frame;

    public VentanaPresupuestos() {
        frame = new JFrame("Gestión de Presupuestos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Título superior
        JLabel titulo = new JLabel("Gestión de Presupuestos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titulo, BorderLayout.NORTH);

        // Panel central (aquí luego puedes añadir tablas, formularios, etc.)
        JPanel centro = new JPanel();
        centro.setBackground(Color.WHITE);
        frame.add(centro, BorderLayout.CENTER);

        // Panel inferior con un botón de cerrar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> frame.dispose());
        panelBotones.add(btnCerrar);

        frame.add(panelBotones, BorderLayout.SOUTH);
    }

    public void mostrar() {
        frame.setVisible(true);
    }
}
