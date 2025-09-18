package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VentanaResultados {
    private final JFrame frame;

    public VentanaResultados() {
        frame = new JFrame("Resultados de Laboratorio");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Aquí se mostrarán los resultados del laboratorio", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(label, BorderLayout.CENTER);
        frame.add(panel);
    }

    public void mostrar() {
        frame.setVisible(true);
    }
}
