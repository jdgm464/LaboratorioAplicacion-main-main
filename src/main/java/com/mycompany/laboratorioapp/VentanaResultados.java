package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

public class VentanaResultados {
    private final JFrame frame;
    private JTable tablaResultados;
    private static final java.util.Set<VentanaResultados> INSTANCIAS = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());
    // Filtros de búsqueda
    private JSpinner desdeSpinner;
    private JSpinner hastaSpinner;
    // (Sin panel de datos duplicado; la información del paciente se mostrará en la tabla)

    public VentanaResultados() {
        frame = new JFrame("Resultados de Laboratorio");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // --- Panel de búsqueda arriba ---
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Opciones de Búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;

        // Desde (selector de fecha)
        gbc.gridx = 0; gbc.gridy = fila;
        panelBusqueda.add(new JLabel("Desde"), gbc);
        gbc.gridx = 1;
        desdeSpinner = new JSpinner(new SpinnerDateModel());
        desdeSpinner.setEditor(new JSpinner.DateEditor(desdeSpinner, "M/d/yyyy"));
        panelBusqueda.add(desdeSpinner, gbc);

        // Hasta (selector de fecha)
        gbc.gridx = 2;
        panelBusqueda.add(new JLabel("Hasta"), gbc);
        gbc.gridx = 3;
        hastaSpinner = new JSpinner(new SpinnerDateModel());
        hastaSpinner.setEditor(new JSpinner.DateEditor(hastaSpinner, "M/d/yyyy"));
        panelBusqueda.add(hastaSpinner, gbc);

        // Orden
        gbc.gridx = 4;
        panelBusqueda.add(new JLabel("Orden"), gbc);
        gbc.gridx = 5;
        panelBusqueda.add(new JTextField(10), gbc);

        // Cedula
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelBusqueda.add(new JLabel("Cédula"), gbc);
        gbc.gridx = 1;
        panelBusqueda.add(new JTextField(12), gbc);

        // Nombre
        gbc.gridx = 2;
        panelBusqueda.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 3;
        panelBusqueda.add(new JTextField(12), gbc);

        // Apellido
        gbc.gridx = 4;
        panelBusqueda.add(new JLabel("Apellido"), gbc);
        gbc.gridx = 5;
        panelBusqueda.add(new JTextField(12), gbc);

        // Prueba
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelBusqueda.add(new JLabel("Prueba"), gbc);
        gbc.gridx = 1;
        panelBusqueda.add(new JTextField(12), gbc);

        // Grupo
        gbc.gridx = 2;
        panelBusqueda.add(new JLabel("Grupo"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        panelBusqueda.add(new JComboBox<>(new String[]{"-- Seleccionar --"}), gbc);

        frame.add(panelBusqueda, BorderLayout.NORTH);

        // --- Panel central (solo tabla) ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

        // Tabla central: ahora muestra datos del paciente/orden
        String[] columnas = {"Orden", "Cédula", "Nombres", "Apellidos", "Sexo", "Fecha", "Entidad", "Edad"};
        tablaResultados = new JTable(new Object[0][columnas.length], columnas);
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        frame.add(panelCentral, BorderLayout.CENTER);

        // --- Panel inferior ---
        JPanel panelInferior = new JPanel(new BorderLayout());

        // Parte izquierda: Bioanalista + Observación
        JPanel panelIzquierda = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 10, 5, 10);
        gbc2.anchor = GridBagConstraints.WEST;

        gbc2.gridx = 0; gbc2.gridy = 0;
        panelIzquierda.add(new JLabel("Bioanalista"), gbc2);
        gbc2.gridx = 1;
        panelIzquierda.add(new JComboBox<>(new String[]{"-- Seleccionar --"}), gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        panelIzquierda.add(new JLabel("Observación"), gbc2);
        gbc2.gridx = 1; gbc2.gridwidth = 3;
        panelIzquierda.add(new JTextField(30), gbc2);

        panelInferior.add(panelIzquierda, BorderLayout.WEST);

        // Parte derecha: botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnSobre = new JButton("Sobre");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> frame.dispose());

        panelBotones.add(btnSobre);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnSalir);

        panelInferior.add(panelBotones, BorderLayout.EAST);

        frame.add(panelInferior, BorderLayout.SOUTH);

        // Registrar instancia y cargar datos iniciales
        INSTANCIAS.add(this);
        refrescarTabla();

        // Abrir detalles con doble clic
        tablaResultados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tablaResultados.getSelectedRow();
                    if (row >= 0) {
                        String nroOrden = String.valueOf(tablaResultados.getValueAt(row, 0));
                        Orden o = OrdenManager.buscarPorNumero(nroOrden);
                        if (o != null) {
                            new VentanaDetallesResultados(o).mostrar();
                        }
                    }
                }
            }
        });
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    public void setTablaPacientesData(Object[][] filas) {
        String[] headers = {"Orden", "Cédula", "Nombres", "Apellidos", "Sexo", "Fecha", "Entidad", "Edad"};
        DefaultTableModel model = new DefaultTableModel(filas, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaResultados.setModel(model);
    }

    // Cargar datos desde OrdenManager y GestorPacientes
    public final void refrescarTabla() {
        java.util.List<Orden> ordenes = OrdenManager.getOrdenes();
        String[] headers = {"Orden", "Cédula", "Nombres", "Apellidos", "Sexo", "Fecha", "Entidad", "Edad"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Orden o : ordenes) {
            Paciente p = GestorPacientes.buscarPorCedula(o.getCedula());
            String sexo = ""; // No disponible en Paciente
            String edad = p != null ? String.valueOf(p.getEdad()) : "";
            model.addRow(new Object[]{
                    o.getNumeroOrden(),
                    o.getCedula(),
                    o.getNombres(),
                    o.getApellidos(),
                    sexo,
                    o.getFechaRegistro(),
                    o.getEmpresa(),
                    edad
            });
        }
        tablaResultados.setModel(model);
    }

    // Permite refrescar todas las ventanas abiertas
    public static void refrescarTodas() {
        for (VentanaResultados vr : INSTANCIAS) {
            if (vr != null) vr.refrescarTabla();
        }
    }
}