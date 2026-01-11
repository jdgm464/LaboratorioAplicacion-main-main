package com.mycompany.laboratorioapp.resultados;

import com.mycompany.laboratorioapp.examenes.VentanaDetallesExamen;
import com.mycompany.laboratorioapp.ordenes.Orden;
import com.mycompany.laboratorioapp.ordenes.OrdenManager;
import com.mycompany.laboratorioapp.pacientes.GestorPacientes;
import com.mycompany.laboratorioapp.pacientes.Paciente;

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
    private javax.swing.JTextField filtroOrdenField;
    private javax.swing.JTextField filtroCedulaField;
    private javax.swing.JTextField filtroNombreField;
    private javax.swing.JTextField filtroApellidoField;
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
        filtroOrdenField = new JTextField(10);
        panelBusqueda.add(filtroOrdenField, gbc);

        // Cedula
        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        panelBusqueda.add(new JLabel("Cédula"), gbc);
        gbc.gridx = 1;
        filtroCedulaField = new JTextField(12);
        panelBusqueda.add(filtroCedulaField, gbc);

        // Nombre
        gbc.gridx = 2;
        panelBusqueda.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 3;
        filtroNombreField = new JTextField(12);
        panelBusqueda.add(filtroNombreField, gbc);

        // Apellido
        gbc.gridx = 4;
        panelBusqueda.add(new JLabel("Apellido"), gbc);
        gbc.gridx = 5;
        filtroApellidoField = new JTextField(12);
        panelBusqueda.add(filtroApellidoField, gbc);

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
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        panelBotones.add(btnSobre);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnSalir);

        panelInferior.add(panelBotones, BorderLayout.EAST);

        frame.add(panelInferior, BorderLayout.SOUTH);

        // Acciones de Enter para filtrar y vacío para mostrar todos
        java.awt.event.ActionListener aplicar = e -> aplicarFiltros();
        filtroOrdenField.addActionListener(aplicar);
        filtroCedulaField.addActionListener(aplicar);
        filtroNombreField.addActionListener(aplicar);
        filtroApellidoField.addActionListener(aplicar);

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
                            if (contieneHematologiaCompleta(o)) {
                                new VentanaDetallesExamen(o).mostrar();
                            } else {
                                new VentanaDetallesResultados(o).mostrar();
                            }
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
        setModelFromOrdenes(ordenes);
    }

    // Permite refrescar todas las ventanas abiertas
    public static void refrescarTodas() {
        for (VentanaResultados vr : INSTANCIAS) {
            if (vr != null) vr.refrescarTabla();
        }
    }

    private void aplicarFiltros() {
        String qOrden = filtroOrdenField.getText() != null ? filtroOrdenField.getText().trim().toLowerCase() : "";
        String qCed   = filtroCedulaField.getText() != null ? filtroCedulaField.getText().trim().toLowerCase() : "";
        String qNom   = filtroNombreField.getText() != null ? filtroNombreField.getText().trim().toLowerCase() : "";
        String qApe   = filtroApellidoField.getText() != null ? filtroApellidoField.getText().trim().toLowerCase() : "";

        boolean sinFiltros = qOrden.isEmpty() && qCed.isEmpty() && qNom.isEmpty() && qApe.isEmpty();
        if (sinFiltros) { refrescarTabla(); return; }

        java.util.List<Orden> ordenes = new java.util.ArrayList<>();
        for (Orden o : OrdenManager.getOrdenes()) {
            if (!qOrden.isEmpty() && (o.getNumeroOrden() == null || !o.getNumeroOrden().toLowerCase().contains(qOrden))) continue;
            if (!qCed.isEmpty() && (o.getCedula() == null || !o.getCedula().toLowerCase().contains(qCed))) continue;
            if (!qNom.isEmpty() && (o.getNombres() == null || !o.getNombres().toLowerCase().contains(qNom))) continue;
            if (!qApe.isEmpty() && (o.getApellidos() == null || !o.getApellidos().toLowerCase().contains(qApe))) continue;
            ordenes.add(o);
        }
        setModelFromOrdenes(ordenes);
    }

    private void setModelFromOrdenes(java.util.List<Orden> ordenes) {
        String[] headers = {"Orden", "Cédula", "Nombres", "Apellidos", "Sexo", "Fecha", "Entidad", "Edad"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Orden o : ordenes) {
            Paciente p = GestorPacientes.buscarPorCedula(o.getCedula());
            String sexo = "";
            // Primero intentar obtener el sexo de la orden
            if (o.getSexo() != null && !o.getSexo().isEmpty()) {
                sexo = o.getSexo();
            } else if (p != null && p.getSexo() != null && !p.getSexo().isEmpty()) {
                // Si no está en la orden, obtener del paciente
                sexo = p.getSexo();
            }
            // Convertir M/F a formato legible si es necesario
            if (sexo.equals("M")) sexo = "M";
            else if (sexo.equals("F")) sexo = "F";
            
            String edad;
            if (p != null && p.getEdad() > 0) {
                edad = String.valueOf(p.getEdad());
            } else {
                // Intento de extracción desde nombre si viene como "NOMBRE APELLIDO (26)"
                edad = extraerEdadDeNombre(o.getNombres(), o.getApellidos());
            }
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

    // Intenta parsear una edad si fue concatenada en nombres/apellidos (fallback)
    private String extraerEdadDeNombre(String nombres, String apellidos) {
        try {
            String t = (nombres + " " + apellidos).replaceAll("[^0-9]", " ").trim();
            for (String part : t.split("\\s+")) {
                if (part.length() > 0) {
                    int v = Integer.parseInt(part);
                    if (v > 0 && v < 120) return String.valueOf(v);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private void eliminarSeleccionado() {
        int row = tablaResultados.getSelectedRow();
        if (row < 0) {
            javax.swing.JOptionPane.showMessageDialog(frame, "Seleccione un resultado para eliminar.");
            return;
        }
        String nroOrden = String.valueOf(tablaResultados.getValueAt(row, 0));
        int conf = javax.swing.JOptionPane.showConfirmDialog(frame,
                "¿Eliminar el resultado de la orden " + nroOrden + "?",
                "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (conf != javax.swing.JOptionPane.YES_OPTION) return;
        OrdenManager.eliminarOrden(nroOrden);
        refrescarTabla();
    }

    private static boolean contieneHematologiaCompleta(Orden o) {
        try {
            if (o.getExamenes() == null) return false;
            for (String ex : o.getExamenes()) {
                String n = normalizar(ex);
                if (n.contains("hematologia completa") || n.contains("hematologia")) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String t = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return t.toLowerCase().trim();
    }
}

