package com.mycompany.laboratorioapp;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class VentanaOBioanalista {
    private final JFrame frame;

    public VentanaOBioanalista() {
        frame = new JFrame("O/Bioanalista");
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.add(new JLabel("Aquí irá la gestión de O/Bioanalista."), BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
    }

    public void mostrar() {
        frame.setVisible(true);
    }
}


