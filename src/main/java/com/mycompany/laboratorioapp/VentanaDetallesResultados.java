package com.mycompany.laboratorioapp;

import javax.swing.*;
import java.awt.*;

public class VentanaDetallesResultados {
    private final JFrame frame;
    private final Orden orden;

    public VentanaDetallesResultados(Orden orden) {
        this.orden = orden;
        frame = new JFrame("Detalles de Resultados - Orden " + orden.getNumeroOrden());
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel info = new JPanel(new GridLayout(0, 2, 8, 8));
        info.setBorder(BorderFactory.createTitledBorder("Datos del Paciente / Orden"));

        info.add(new JLabel("Orden:"));
        info.add(new JLabel(orden.getNumeroOrden()));
        info.add(new JLabel("Cédula:"));
        info.add(new JLabel(orden.getCedula()));
        info.add(new JLabel("Nombre:"));
        info.add(new JLabel(orden.getNombres() + " " + orden.getApellidos()));
        info.add(new JLabel("Fecha:"));
        info.add(new JLabel(orden.getFechaRegistro()));
        info.add(new JLabel("Entidad:"));
        info.add(new JLabel(orden.getEmpresa()));

        // Lista de exámenes solicitados
        DefaultListModel<String> lm = new DefaultListModel<>();
        if (orden.getExamenes() != null) {
            for (String ex : orden.getExamenes()) lm.addElement(ex);
        }
        JList<String> listaExamenes = new JList<>(lm);
        JScrollPane scrollExamenes = new JScrollPane(listaExamenes);
        scrollExamenes.setBorder(BorderFactory.createTitledBorder("Exámenes"));

        frame.add(info, BorderLayout.NORTH);
        frame.add(scrollExamenes, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> frame.dispose());
        acciones.add(cerrar);
        frame.add(acciones, BorderLayout.SOUTH);
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


