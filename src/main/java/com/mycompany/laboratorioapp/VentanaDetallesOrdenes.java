package com.mycompany.laboratorioapp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 




public class VentanaDetallesOrdenes {
    private VentanaOrdenes ventanaPadre;
    private final JFrame frame;

    // --- Campos accesibles ---
    private JTextField cedulaField, edadField, nombreField, apellidoField, direccionField, telefonoField, correoField, rifField;
    private JComboBox<String> entidadCombo;
    private JTextArea diagnosticoArea;

    // --- Tablas ---
    private JTable tablaExamenes;
    private JTable tablaFactura;
    private DefaultTableModel modeloFactura;

    // --- Campos de totales ---
    private JTextField subtotalField, descuentoField, recargoField, totalField, saldoField;

    // Constructor que recibe VentanaOrdenes
    public VentanaDetallesOrdenes(VentanaOrdenes ventanaPadre) {
        this();
        this.ventanaPadre = ventanaPadre;
    }

    public VentanaDetallesOrdenes() {
        frame = new JFrame("Detalles de Orden");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        agregarAreaSuperior(mainPanel);
        agregarTablasYValores(mainPanel);
        agregarBotones(mainPanel);

        // 🚀 cargar exámenes desde BD Excel al iniciar
        cargarExamenesDesdeBD();
    }

    private void agregarAreaSuperior(JPanel mainPanel) {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // ---------- Cédula ----------
        gbc.gridx = 0; gbc.gridy = 0;
        dataPanel.add(new JLabel("Cédula:"), gbc);

        gbc.gridx = 1;
        cedulaField = new JTextField(10);
        dataPanel.add(cedulaField, gbc);

        // ✅ acción al presionar Enter en la cédula
        cedulaField.addActionListener(e -> {
            String cedula = cedulaField.getText().trim();
            if (!cedula.isEmpty()) {
                var paciente = GestorPacientes.buscarPorCedula(cedula);
                if (paciente == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Paciente no encontrado. Se abrirá el registro.",
                            "Paciente no existe",
                            JOptionPane.INFORMATION_MESSAGE);
                    VentanaRegistroPacientes ventana = new VentanaRegistroPacientes((javax.swing.JFrame) null, this);
                    ventana.mostrar();
                } else {
                    nombreField.setText(paciente.getNombre());
                    apellidoField.setText(paciente.getApellido());
                    direccionField.setText(paciente.getDireccion());
                    telefonoField.setText(paciente.getTelefono());
                    correoField.setText(paciente.getCorreo());
                    rifField.setText(paciente.getCedula());
                    edadField.setText(String.valueOf(paciente.getEdad()));
                }
            }
        });

        // ✅ Botón emoji
        gbc.gridx = 2;
        JButton botonEmoji = new JButton("😊");
        botonEmoji.setPreferredSize(new Dimension(45, 25));
        botonEmoji.addActionListener(e -> {
            VentanaRegistroPacientes ventana = new VentanaRegistroPacientes((javax.swing.JFrame) null, this);
            ventana.mostrar();
        });
        dataPanel.add(botonEmoji, gbc);

        // ---------- Edad ----------
        gbc.gridx = 3;
        dataPanel.add(new JLabel("Edad:"), gbc);

        gbc.gridx = 4;
        edadField = new JTextField(5);
        dataPanel.add(edadField, gbc);

        // ---------- Entidad ----------
        gbc.gridx = 5;
        dataPanel.add(new JLabel("Entidad:"), gbc);

        gbc.gridx = 6;
        entidadCombo = new JComboBox<>();
        entidadCombo.setPreferredSize(new Dimension(180, 25));
        dataPanel.add(entidadCombo, gbc);

        // Botón engranaje
        gbc.gridx = 7;
        JButton botonEngranaje = new JButton("⚙");
        botonEngranaje.setPreferredSize(new Dimension(45, 25));
        botonEngranaje.addActionListener(e -> abrirVentanaConfiguracion());
        dataPanel.add(botonEngranaje, gbc);

        // ---------- Detalle Facturación (día/fecha/hora) ----------
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 8; gbc.fill = GridBagConstraints.HORIZONTAL;

        String dia = new SimpleDateFormat("EEEE").format(new Date());
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String hora = new SimpleDateFormat("HH:mm").format(new Date());

        JLabel detalle = new JLabel("Detalle para Facturación    " + dia + ", " + fecha + " - " + hora);
        detalle.setOpaque(true);
        detalle.setBackground(Color.GRAY);
        detalle.setForeground(Color.WHITE);
        detalle.setPreferredSize(new Dimension(1100, 35));
        detalle.setFont(new Font("Arial", Font.PLAIN, 16));
        dataPanel.add(detalle, gbc);

        // ---------- Nombre ----------
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 2; gbc.gridx = 0;
        dataPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        nombreField = new JTextField(12);
        dataPanel.add(nombreField, gbc);

        // ---------- Apellido ----------
        gbc.gridx = 2;
        dataPanel.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 3;
        apellidoField = new JTextField(12);
        dataPanel.add(apellidoField, gbc);

        // ---------- Cédula/RIF ----------
        gbc.gridx = 4;
        dataPanel.add(new JLabel("Cédula/RIF:"), gbc);
        gbc.gridx = 5;
        rifField = new JTextField(12);
        dataPanel.add(rifField, gbc);

        // ---------- Correo ----------
        gbc.gridy = 3; gbc.gridx = 0;
        dataPanel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        correoField = new JTextField(20);
        dataPanel.add(correoField, gbc);
        gbc.gridwidth = 1;

        // ---------- Dirección ----------
        gbc.gridy = 4; gbc.gridx = 0;
        dataPanel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        direccionField = new JTextField(40);
        dataPanel.add(direccionField, gbc);

        // ---------- Teléfono ----------
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 1;
        dataPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        telefonoField = new JTextField(15);
        dataPanel.add(telefonoField, gbc);

        centerPanel.add(dataPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.NORTH);
    }

    private void agregarTablasYValores(JPanel mainPanel) {
        JPanel tablasPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Izquierda: Exámenes (Código oculto + Descripción)
        String[] columnasExamenes = {"Código", "Descripción"};
        DefaultTableModel modeloExamenes = new DefaultTableModel(columnasExamenes, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaExamenes = new JTable(modeloExamenes);
        // Ocultar columna de código
        tablaExamenes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaExamenes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaExamenes.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollExamenes = new JScrollPane(tablaExamenes);
        scrollExamenes.setBorder(BorderFactory.createTitledBorder("Exámenes"));
        tablasPanel.add(scrollExamenes);

        // Derecha: Factura
        String[] columnasFactura = {"Examen", "Costo ($)"};
        modeloFactura = new DefaultTableModel(columnasFactura, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        tablaFactura = new JTable(modeloFactura);
        JScrollPane scrollFactura = new JScrollPane(tablaFactura);
        scrollFactura.setBorder(BorderFactory.createTitledBorder("Detalle de Factura"));
        tablasPanel.add(scrollFactura);

        tablaExamenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaExamenes.getSelectedRow();
                if (fila >= 0) {
                    String codigo = String.valueOf(tablaExamenes.getValueAt(fila, 0));
                    String descripcion = String.valueOf(tablaExamenes.getValueAt(fila, 1));
                    Double p = BaseDeDatosExcel.getPrecioPorCodigo(codigo);
                    double costo = p != null ? p : 0.0;
                    modeloFactura.addRow(new Object[]{descripcion, String.format("%.2f", costo)});
                    actualizarTotales();
                }
            }
        });

        mainPanel.add(tablasPanel, BorderLayout.CENTER);

        // Totales + diagnóstico
        JPanel panelInferior = new JPanel(new BorderLayout());
        JPanel totalesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        totalesPanel.add(new JLabel("Subtotal ($):"));
        subtotalField = new JTextField(8); subtotalField.setEditable(false);
        totalesPanel.add(subtotalField);

        totalesPanel.add(new JLabel("% Desc:"));
        descuentoField = new JTextField(5);
        descuentoField.getDocument().addDocumentListener(SimpleDocListener.onChange(this::actualizarTotales));
        totalesPanel.add(descuentoField);

        totalesPanel.add(new JLabel("% Rec:"));
        recargoField = new JTextField(5);
        recargoField.getDocument().addDocumentListener(SimpleDocListener.onChange(this::actualizarTotales));
        totalesPanel.add(recargoField);

        totalesPanel.add(new JLabel("Total:"));
        totalField = new JTextField(8); totalField.setEditable(false);
        totalesPanel.add(totalField);

        totalesPanel.add(new JLabel("Saldo:"));
        saldoField = new JTextField(8); saldoField.setEditable(false);
        totalesPanel.add(saldoField);

        panelInferior.add(totalesPanel, BorderLayout.NORTH);

        JPanel diagnosticoPanel = new JPanel(new BorderLayout());
        diagnosticoPanel.setBorder(BorderFactory.createTitledBorder("Diagnóstico / Observaciones"));
        diagnosticoArea = new JTextArea(4, 80);
        diagnosticoArea.setLineWrap(true);
        diagnosticoArea.setWrapStyleWord(true);
        JScrollPane scrollDiagnostico = new JScrollPane(diagnosticoArea);
        diagnosticoPanel.add(scrollDiagnostico, BorderLayout.CENTER);

        panelInferior.add(diagnosticoPanel, BorderLayout.CENTER);
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarExamenesDesdeBD() {
        try {
            // 1) Intentar con ruta recordada en config
            BaseDeDatosExcel.cargarRutaListaPreciosDesdeConfig();
            String path = BaseDeDatosExcel.getListaPreciosPath();
            boolean ok = false;
            if (path != null && !path.isBlank()) {
                try {
                    BaseDeDatosExcel.cargarDesdeExcel(path);
                    BaseDeDatosExcel.cargarListaPrecios(path);
                    ok = true;
                } catch (Exception ignored) {}
            }
            // 2) Fallback a archivos por defecto en raíz o resources
            if (!ok) {
                String def = "lista precios examenes.xlsx";
                try {
                    BaseDeDatosExcel.cargarDesdeExcel(def);
                    BaseDeDatosExcel.cargarListaPrecios(def);
                    ok = true;
                } catch (Exception ex) {
                    try {
                        BaseDeDatosExcel.cargarDesdeExcel("resources/" + def);
                        BaseDeDatosExcel.cargarListaPrecios("resources/" + def);
                        ok = true;
                    } catch (Exception ignored) {}
                }
            }

            List<Examen> examenes = BaseDeDatosExcel.getExamenes();
            DefaultTableModel modelo = (DefaultTableModel) tablaExamenes.getModel();
            for (Examen examen : examenes) {
                String cod = examen.getCodigo() == null ? "" : examen.getCodigo();
                modelo.addRow(new Object[]{cod, examen.getNombre()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error al cargar exámenes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotales() {
        double subtotalBruto = 0.0;
        for (int i = 0; i < modeloFactura.getRowCount(); i++) {
            try {
                subtotalBruto += Double.parseDouble(modeloFactura.getValueAt(i, 1).toString());
            } catch (Exception ignored) {}
        }

        double desc = 0.0, rec = 0.0;
        try { desc = Double.parseDouble(descuentoField.getText()) / 100.0; } catch (Exception ignored) {}
        try { rec  = Double.parseDouble(recargoField.getText())   / 100.0; } catch (Exception ignored) {}

        // Subtotal muestra el monto con descuento aplicado
        double subtotalConDesc = subtotalBruto * (1.0 - Math.max(0.0, desc));
        subtotalField.setText(String.format("%.2f", subtotalConDesc));

        // Total aplica recargo sobre el subtotal ya descontado
        double total = subtotalConDesc * (1.0 + Math.max(0.0, rec));
        totalField.setText(String.format("%.2f", total));
        saldoField.setText(String.format("%.2f", total));
    }

    private void abrirVentanaConfiguracion() {
        JFrame ventana = new JFrame("Configuración");
        ventana.setSize(400, 250);
        ventana.setLocationRelativeTo(frame);
        ventana.setVisible(true);
    }

    private void agregarBotones(JPanel mainPanel) {
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnConstancia = new JButton("Constancia");
        JButton btnOBio       = new JButton("O/Bioanalista");
        JButton btnNuevo      = new JButton("Nuevo");
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnCargar     = new JButton("Cargar");
        JButton btnModificar  = new JButton("Modificar");
        JButton btnAnular     = new JButton("Anular");
        JButton btnBuscar     = new JButton("Buscar");
        JButton btnImprimir   = new JButton("Imprimir");
        JButton btnAnterior   = new JButton("<<");
        JButton btnSiguiente  = new JButton(">>");
        JButton btnSalir      = new JButton("Salir");
        btnSalir.addActionListener(e -> frame.dispose());

        botonesPanel.add(btnConstancia);
        botonesPanel.add(btnOBio);
        botonesPanel.add(btnNuevo);
        botonesPanel.add(btnGuardar);
        botonesPanel.add(btnCargar);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnAnular);
        botonesPanel.add(btnBuscar);
        botonesPanel.add(btnImprimir);
        botonesPanel.add(btnAnterior);
        botonesPanel.add(btnSiguiente);
        botonesPanel.add(btnSalir);

        frame.add(botonesPanel, BorderLayout.SOUTH);

        // ====== Acciones requeridas ======
        btnNuevo.addActionListener(e -> {
            VentanaRegistroPacientes reg = new VentanaRegistroPacientes((javax.swing.JFrame) null, this);
            reg.mostrar();
        });

        btnModificar.addActionListener(e -> {
            String cedula = cedulaField.getText().trim();
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ingrese la cédula para modificar.");
                return;
            }
            Paciente p = GestorPacientes.buscarPorCedula(cedula);
            if (p == null) {
                JOptionPane.showMessageDialog(frame, "Paciente no encontrado.");
                return;
            }
            VentanaRegistroPacientes reg = new VentanaRegistroPacientes(p, this);
            reg.mostrar();
        });

        // Guardar orden
        btnGuardar.addActionListener(e -> {
            String cedula = cedulaField.getText().trim();
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "La cédula es obligatoria para guardar.");
                return;
            }
            Paciente paciente = new Paciente(
                    cedula,
                    nombreField.getText().trim(),
                    apellidoField.getText().trim(),
                    parseEnteroSeguro(edadField.getText().trim()),
                    direccionField.getText().trim(),
                    telefonoField.getText().trim(),
                    correoField.getText().trim()
            );
            GestorPacientes.guardarPaciente(paciente);
            JOptionPane.showMessageDialog(frame, "Paciente guardado correctamente.");

            // Mostrar/actualizar la lista de pacientes
            VentanaPacientes vp = new VentanaPacientes();
            vp.mostrar();

            // ==== Agregar/Reflejar en VentanaOrdenes ====
            // Construir lista de exámenes y total desde la tabla de factura
            java.util.List<String> examenes = new java.util.ArrayList<>();
            double total = 0.0;
            for (int i = 0; i < modeloFactura.getRowCount(); i++) {
                Object ex = modeloFactura.getValueAt(i, 0);
                Object precio = modeloFactura.getValueAt(i, 1);
                if (ex != null) examenes.add(ex.toString());
                try { total += Double.parseDouble(String.valueOf(precio)); } catch (Exception ignored) {}
            }

            String numeroOrden = String.format("%06d", OrdenManager.getOrdenes().size() + 1);
            String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
            String hora = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());

            Orden orden = new Orden(
                    numeroOrden,
                    "", // Factura N°
                    "", // Control N°
                    "", // Lote N°
                    fecha,
                    hora,
                    "", // Cod Paciente (no disponible)
                    cedula,
                    paciente.getNombre(),
                    paciente.getApellido(),
                    paciente.getDireccion(),
                    paciente.getTelefono(),
                    paciente.getCorreo(),
                    "", // Cod Empresa
                    (String) entidadCombo.getSelectedItem(),
                    examenes,
                    total
            );
            OrdenManager.agregarOrden(orden);
            if (ventanaPadre != null) {
                ventanaPadre.refrescarTabla();
            }
            // Refrescar resultados para que se refleje de inmediato
            VentanaResultados.refrescarTodas();
        });

        // Cargar orden por número
        btnCargar.addActionListener(e -> {
            String numero = JOptionPane.showInputDialog(frame, "Ingrese el número de presupuesto/orden:", "Cargar", JOptionPane.QUESTION_MESSAGE);
            if (numero == null || numero.trim().isEmpty()) return;
            numero = numero.trim();
            Orden o = OrdenManager.buscarPorNumero(numero);
            if (o == null) {
                JOptionPane.showMessageDialog(frame, "No se encontró la orden " + numero, "Cargar", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Rellenar datos del paciente y empresa
            cedulaField.setText(o.getCedula());
            nombreField.setText(o.getNombres());
            apellidoField.setText(o.getApellidos());
            direccionField.setText(o.getDireccion());
            telefonoField.setText(o.getTelefono());
            correoField.setText(o.getCorreo());
            rifField.setText(o.getCedula());
            // entidad si existe
            if (o.getEmpresa() != null) {
                entidadCombo.addItem(o.getEmpresa());
                entidadCombo.setSelectedItem(o.getEmpresa());
            }
            // Cargar exámenes en la tabla de factura
            modeloFactura.setRowCount(0);
            if (o.getExamenes() != null) {
                for (String ex : o.getExamenes()) {
                    modeloFactura.addRow(new Object[]{ex, ""});
                }
            }
            // Total
            totalField.setText(String.format("%.2f", o.getTotal()));
            saldoField.setText(String.format("%.2f", o.getTotal()));
            subtotalField.setText("");
            actualizarTotales();
        });

        btnAnular.addActionListener(e -> {
            if (ventanaPadre != null) {
                ventanaPadre.anularSeleccionada();
                JOptionPane.showMessageDialog(frame, "Orden anulada.");
            } else {
                JOptionPane.showMessageDialog(frame, "Abra desde 'Registro de Facturación por Orden' para anular.");
            }
        });

        btnAnterior.addActionListener(e -> navegarPaciente(-1));
        btnSiguiente.addActionListener(e -> navegarPaciente(1));

        btnOBio.addActionListener(e -> new VentanaOBioanalista().mostrar());
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ✅ Método nuevo para recibir cédula desde VentanaRegistroPacientes
    public void setCedulaPaciente(String cedula) {
        cedulaField.setText(cedula);
        var paciente = GestorPacientes.buscarPorCedula(cedula);
        if (paciente != null) {
            nombreField.setText(paciente.getNombre());
            apellidoField.setText(paciente.getApellido());
            direccionField.setText(paciente.getDireccion());
            telefonoField.setText(paciente.getTelefono());
            correoField.setText(paciente.getCorreo());
            rifField.setText(paciente.getCedula());
            edadField.setText(String.valueOf(paciente.getEdad()));
        }
    }

    // ====== Navegación de pacientes ======
    private java.util.List<Paciente> cachePacientes;
    private int indicePaciente = -1;

    private void navegarPaciente(int delta) {
        if (cachePacientes == null) {
            cachePacientes = GestorPacientes.cargarPacientes();
            // posicionarnos en el actual si existe
            String actual = cedulaField.getText().trim();
            for (int i = 0; i < cachePacientes.size(); i++) {
                if (cachePacientes.get(i).getCedula().trim().equalsIgnoreCase(actual)) {
                    indicePaciente = i;
                    break;
                }
            }
        }
        if (cachePacientes.isEmpty()) return;

        if (indicePaciente < 0) indicePaciente = 0; // si no hay actual, empezar en primero
        int nuevo = indicePaciente + delta;
        if (nuevo < 0 || nuevo >= cachePacientes.size()) return; // fuera de rango
        indicePaciente = nuevo;
        mostrarPaciente(cachePacientes.get(indicePaciente));
    }

    private void mostrarPaciente(Paciente p) {
        if (p == null) return;
        cedulaField.setText(p.getCedula());
        nombreField.setText(p.getNombre());
        apellidoField.setText(p.getApellido());
        direccionField.setText(p.getDireccion());
        telefonoField.setText(p.getTelefono());
        correoField.setText(p.getCorreo());
        rifField.setText(p.getCedula());
        edadField.setText(String.valueOf(p.getEdad()));
    }

    private int parseEnteroSeguro(String texto) {
        try { return Integer.parseInt(texto); } catch (Exception ex) { return 0; }
    }

    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable onAnyChange;
        private SimpleDocListener(Runnable r) { this.onAnyChange = r; }
        public static SimpleDocListener onChange(Runnable r) { return new SimpleDocListener(r); }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { onAnyChange.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { onAnyChange.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { onAnyChange.run(); }
    }
}