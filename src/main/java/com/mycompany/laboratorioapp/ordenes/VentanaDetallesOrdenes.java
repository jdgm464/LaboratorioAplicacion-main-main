package com.mycompany.laboratorioapp.ordenes;

import com.mycompany.laboratorioapp.BaseDeDatosExcel;
import com.mycompany.laboratorioapp.VentanaOBioanalista;
import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.examenes.Examen;
import com.mycompany.laboratorioapp.pacientes.GestorPacientes;
import com.mycompany.laboratorioapp.pacientes.Paciente;
import com.mycompany.laboratorioapp.pacientes.PacienteExcelHelper;
import com.mycompany.laboratorioapp.pacientes.VentanaPacientes;
import com.mycompany.laboratorioapp.pacientes.VentanaRegistroPacientes;
import com.mycompany.laboratorioapp.resultados.VentanaResultados;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.swing.table.TableRowSorter;
 




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
    private JTextField filtroExamenField;
    private TableRowSorter<DefaultTableModel> sorterExamenes;
    private boolean seleccionProgramaticaExamen;

    // --- Campos de totales ---
    private JTextField subtotalField, descuentoField, recargoField, totalField, saldoField;
    
    // --- Orden actual (para saber si estamos editando) ---
    private Orden ordenActual = null; // Si no es null, estamos editando una orden existente

    // Constructor que recibe VentanaOrdenes
    public VentanaDetallesOrdenes(VentanaOrdenes ventanaPadre) {
        this();
        this.ventanaPadre = ventanaPadre;
    }
    
    // Método para establecer la orden actual (cuando se carga o modifica)
    public void setOrdenActual(Orden orden) {
        this.ordenActual = orden;
    }
    
    // Método para cargar una orden en el formulario
    public void cargarOrden(Orden orden) {
        if (orden == null) return;
        
        this.ordenActual = orden;
        
        // Rellenar datos del paciente
        cedulaField.setText(orden.getCedula());
        nombreField.setText(orden.getNombres());
        apellidoField.setText(orden.getApellidos());
        direccionField.setText(orden.getDireccion());
        telefonoField.setText(orden.getTelefono());
        correoField.setText(orden.getCorreo());
        rifField.setText(orden.getCedula());
        
        // Edad desde pacientes si existe
        Paciente p = GestorPacientes.buscarPorCedula(orden.getCedula());
        if (p != null) {
            edadField.setText(obtenerEdadPaciente(p, orden.getCedula()));
        }
        
        // Empresa/Entidad
        if (orden.getEmpresa() != null && !orden.getEmpresa().isBlank()) {
            if (((javax.swing.DefaultComboBoxModel<String>) entidadCombo.getModel()).getIndexOf(orden.getEmpresa()) < 0) {
                entidadCombo.addItem(orden.getEmpresa());
            }
            entidadCombo.setSelectedItem(orden.getEmpresa());
        } else if (entidadCombo.getItemCount() > 0) {
            entidadCombo.setSelectedIndex(0);
        }
        
        // Cargar exámenes en la tabla de factura
        modeloFactura.setRowCount(0);
        if (orden.getExamenes() != null) {
            // Ya no es necesario cargar desde Excel, los precios están en la BD
            for (String ex : orden.getExamenes()) {
                // El valor puede ser un código o un nombre (para compatibilidad con órdenes antiguas)
                String nombreExamen = ex;
                double precio = 0.0;
                
                // Intentar obtener el nombre si es un código
                String nombrePorCodigo = ExamenDAO.obtenerNombrePorCodigo(ex);
                if (nombrePorCodigo != null && !nombrePorCodigo.isEmpty()) {
                    // Es un código, usar el nombre obtenido
                    nombreExamen = nombrePorCodigo;
                    Double precioPorCodigo = ExamenDAO.obtenerPrecio(ex);
                    if (precioPorCodigo != null) {
                        precio = precioPorCodigo;
                    } else {
                        precio = obtenerPrecioPorNombre(nombreExamen);
                    }
                } else {
                    // Probablemente es un nombre (orden antigua), buscar precio por nombre
                    precio = obtenerPrecioPorNombre(ex);
                }
                
                modeloFactura.addRow(new Object[]{nombreExamen, String.format("%.2f", precio)});
            }
        }
        
        // Total
        totalField.setText(String.format("%.2f", orden.getTotal()));
        saldoField.setText(String.format("%.2f", orden.getTotal()));
        subtotalField.setText("");
        actualizarTotales();
    }
    
    private double obtenerPrecioPorNombre(String nombre) {
        if (nombre == null) return 0.0;
        // Buscar directamente en la BD
        java.util.List<Examen> examenes = ExamenDAO.buscarPorNombre(nombre);
        if (examenes != null && !examenes.isEmpty()) {
            return examenes.get(0).getPrecio();
        }
        return 0.0;
    }
    
    /**
     * Obtiene el código de un examen por su nombre
     * Busca directamente en la base de datos
     */
    private String obtenerCodigoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        // Buscar directamente en la BD
        java.util.List<Examen> examenesBD = ExamenDAO.buscarPorNombre(nombre);
        if (examenesBD != null && !examenesBD.isEmpty()) {
            return examenesBD.get(0).getCodigo();
        }
        return null;
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

        // 🚀 cargar exámenes desde BD Excel en segundo plano (no bloquea la apertura de la ventana)
        cargarExamenesEnSegundoPlano();
    }
    
    /**
     * Carga los exámenes en un hilo separado para no bloquear la apertura de la ventana
     */
    private void cargarExamenesEnSegundoPlano() {
        // Cargar exámenes en un hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                // Cargar directamente desde la base de datos
                java.util.List<Examen> examenesBD = com.mycompany.laboratorioapp.dao.ExamenDAO.obtenerTodos();
                
                if (examenesBD != null && !examenesBD.isEmpty()) {
                    // Actualizar la tabla en el hilo de Swing
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        DefaultTableModel modelo = (DefaultTableModel) tablaExamenes.getModel();
                        modelo.setRowCount(0); // Limpiar primero
                        for (Examen examen : examenesBD) {
                            String cod = examen.getCodigo() != null ? examen.getCodigo() : "";
                            modelo.addRow(new Object[]{cod, examen.getNombre()});
                        }
                    });
                } else {
                    System.err.println("⚠️ No hay exámenes en la base de datos. Use 'Control de Precios' para cargar desde Excel si es necesario.");
                }
            } catch (Exception e) {
                System.err.println("❌ Error al cargar exámenes desde BD: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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
                    cargarPacienteEnCampos(paciente);
                    verificarEdadPaciente(paciente);
                }
            }
        });

        // ✅ Botón emoji
        gbc.gridx = 2;
        JButton botonEmoji = new JButton("<html>👤</html>");
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
        entidadCombo.addItem("-- Seleccionar --");
        for (String entidad : BaseDeDatosExcel.getEntidades()) {
            entidadCombo.addItem(entidad);
        }
        entidadCombo.setSelectedIndex(0);
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
        sorterExamenes = new TableRowSorter<>(modeloExamenes);
        tablaExamenes.setRowSorter(sorterExamenes);
        // Ocultar columna de código
        tablaExamenes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaExamenes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaExamenes.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollExamenes = new JScrollPane(tablaExamenes);
        JPanel panelExamenes = new JPanel(new BorderLayout(5, 5));
        panelExamenes.setBorder(BorderFactory.createTitledBorder("Exámenes"));
        JPanel panelBusquedaExamen = new JPanel(new BorderLayout(5, 5));
        panelBusquedaExamen.add(new JLabel("Buscar:"), BorderLayout.WEST);
        filtroExamenField = new JTextField();
        filtroExamenField.setToolTipText("Escriba el nombre o código del examen para ubicarlo rápido.");
        panelBusquedaExamen.add(filtroExamenField, BorderLayout.CENTER);
        panelExamenes.add(panelBusquedaExamen, BorderLayout.NORTH);
        panelExamenes.add(scrollExamenes, BorderLayout.CENTER);
        tablasPanel.add(panelExamenes);

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

        // Doble clic en un examen de la factura: quitar de la factura y actualizar totales
        tablaFactura.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaFactura.getSelectedRow();
                    if (fila >= 0 && modeloFactura.getRowCount() > 0) {
                        modeloFactura.removeRow(fila);
                        actualizarTotales();
                    }
                }
            }
        });
        tablaFactura.setToolTipText("Doble clic en un examen para quitarlo de la factura.");

        // Actualizar totales cuando se edita el costo en la tabla
        modeloFactura.addTableModelListener(ev -> actualizarTotales());

        filtroExamenField.getDocument().addDocumentListener(SimpleDocListener.onChange(this::filtrarExamenes));
        filtroExamenField.addActionListener(e -> agregarExamenSeleccionado());
        filtroExamenField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && tablaExamenes.getRowCount() > 0) {
                    tablaExamenes.requestFocusInWindow();
                    tablaExamenes.setRowSelectionInterval(0, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    agregarExamenSeleccionado();
                    e.consume();
                }
            }
        });

        tablaExamenes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tablaExamenes.getSelectedRow() >= 0 && !seleccionProgramaticaExamen) {
                    agregarExamenSeleccionado();
                }
            }
        });
        tablaExamenes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    agregarExamenSeleccionado();
                    e.consume();
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

    private void actualizarTotales() {
        if (modeloFactura == null || subtotalField == null) return;
        double subtotalBruto = 0.0;
        for (int i = 0; i < modeloFactura.getRowCount(); i++) {
            Object val = modeloFactura.getValueAt(i, 1);
            if (val == null) continue;
            String s = val.toString().trim().replace(',', '.');
            if (s.isEmpty()) continue;
            try {
                subtotalBruto += Double.parseDouble(s);
            } catch (NumberFormatException ignored) {}
        }

        double desc = 0.0, rec = 0.0;
        if (descuentoField != null && descuentoField.getText() != null) {
            try { desc = Double.parseDouble(descuentoField.getText().trim().replace(',', '.')) / 100.0; } catch (Exception ignored) {}
        }
        if (recargoField != null && recargoField.getText() != null) {
            try { rec  = Double.parseDouble(recargoField.getText().trim().replace(',', '.'))   / 100.0; } catch (Exception ignored) {}
        }

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
            // Limpiar formulario para crear nueva orden
            limpiarFormulario();
            ordenActual = null; // Asegurar que no hay orden cargada
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
            
            // Obtener o crear el paciente con todos sus datos
            Paciente paciente = GestorPacientes.buscarPorCedula(cedula);
            if (paciente == null) {
                // Si no existe, crear uno nuevo con los datos del formulario
                paciente = new Paciente(
                    cedula,
                    nombreField.getText().trim(),
                    apellidoField.getText().trim(),
                    parseEnteroSeguro(edadField.getText().trim()),
                    direccionField.getText().trim(),
                    telefonoField.getText().trim(),
                        correoField.getText().trim(),
                        "" // Sexo vacío si es nuevo, se puede agregar después
            );
            GestorPacientes.guardarPaciente(paciente);
            } else {
                // Si existe, actualizar con los datos del formulario pero mantener el sexo
                paciente.setNombre(nombreField.getText().trim());
                paciente.setApellido(apellidoField.getText().trim());
                paciente.setEdad(parseEnteroSeguro(edadField.getText().trim()));
                paciente.setDireccion(direccionField.getText().trim());
                paciente.setTelefono(telefonoField.getText().trim());
                paciente.setCorreo(correoField.getText().trim());
                // Mantener el sexo existente si ya estaba guardado
                GestorPacientes.actualizarPaciente(paciente);
            }
            
            JOptionPane.showMessageDialog(frame, "Paciente guardado correctamente.");

            // Mostrar/actualizar la lista de pacientes
            VentanaPacientes vp = new VentanaPacientes();
            vp.mostrar();

            // ==== Agregar/Reflejar en VentanaOrdenes ====
            // Construir lista de exámenes y total desde la tabla de factura
            // IMPORTANTE: Guardar códigos de exámenes, no nombres
            java.util.List<String> examenes = new java.util.ArrayList<>();
            double total = 0.0;
            for (int i = 0; i < modeloFactura.getRowCount(); i++) {
                Object ex = modeloFactura.getValueAt(i, 0);
                Object precio = modeloFactura.getValueAt(i, 1);
                if (ex != null) {
                    // Convertir nombre del examen a código
                    String nombreExamen = ex.toString();
                    String codigoExamen = obtenerCodigoPorNombre(nombreExamen);
                    if (codigoExamen != null && !codigoExamen.isEmpty()) {
                        examenes.add(codigoExamen);
                    } else {
                        // Si no se encuentra el código, usar el nombre como fallback
                        // pero esto debería ser raro
                        System.err.println("⚠️ No se encontró código para examen: " + nombreExamen);
                        examenes.add(nombreExamen);
                    }
                }
                try { total += Double.parseDouble(String.valueOf(precio)); } catch (Exception ignored) {}
            }

            String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
            String hora = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
            java.util.Map<String, String> datosPacienteExcel = PacienteExcelHelper.buscarPorCedula(cedula);
            String codigoPaciente = obtenerCodigoPaciente(cedula, datosPacienteExcel);
            String empresaSeleccionada = obtenerEmpresaSeleccionada();
            String codigoEmpresa = obtenerCodigoEmpresa(cedula, empresaSeleccionada, ordenActual);

            // Verificar si estamos editando una orden existente o creando una nueva
            boolean esEdicion = ordenActual != null && ordenActual.getNumeroOrden() != null;
            
            if (esEdicion) {
                // Actualizar orden existente
                System.out.println("🔄 Actualizando orden existente: " + ordenActual.getNumeroOrden());
                
                Orden orden = new Orden(
                        ordenActual.getNumeroOrden(), // Mantener el número de orden original
                        valorSeguro(ordenActual.getNumeroFactura()),
                        valorSeguro(ordenActual.getNumeroControl()),
                        valorSeguro(ordenActual.getNumeroLote()),
                        fecha,
                        hora,
                        codigoPaciente,
                        cedula,
                        paciente.getNombre(),
                        paciente.getApellido(),
                        paciente.getDireccion(),
                        paciente.getTelefono(),
                        paciente.getCorreo(),
                        codigoEmpresa,
                        empresaSeleccionada,
                        paciente.getSexo() != null ? paciente.getSexo() : "",
                        examenes,
                        total
                );
                orden.setEstatus(ordenActual.getEstatus() != null ? ordenActual.getEstatus() : "Activo"); // Mantener estatus
                
                // Actualizar la orden en la base de datos
                boolean actualizado = com.mycompany.laboratorioapp.dao.OrdenDAO.actualizar(orden);
                
                if (actualizado) {
                    System.out.println("✅ Orden actualizada correctamente en BD");
                    
                    // Actualizar también los exámenes de la orden
                    try {
                        int ordenId = com.mycompany.laboratorioapp.dao.OrdenDAO.obtenerIdPorNumeroOrden(orden.getNumeroOrden());
                        if (ordenId > 0) {
                            // Eliminar exámenes antiguos e insertar los nuevos
                            com.mycompany.laboratorioapp.dao.OrdenDAO.eliminarExamenesOrden(ordenId);
                            com.mycompany.laboratorioapp.dao.OrdenDAO.insertarExamenesOrden(ordenId, examenes);
                        }
                    } catch (Exception ex) {
                        System.err.println("⚠️ Error al actualizar exámenes: " + ex.getMessage());
                    }
                    
                    JOptionPane.showMessageDialog(frame, 
                        "Orden actualizada exitosamente.\nNúmero de orden: " + orden.getNumeroOrden(),
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Esperar un momento para asegurar que la BD se actualizó
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // Refrescar la tabla de órdenes desde la base de datos
                    if (ventanaPadre != null) {
                        System.out.println("🔄 Refrescando tabla de órdenes...");
                        ventanaPadre.refrescarTabla();
                    }
                    
                    // Refrescar resultados para que se refleje de inmediato
                    System.out.println("🔄 Refrescando ventana de resultados...");
                    VentanaResultados.refrescarTodas();
                    
                    // Cerrar la ventana de detalles
                    frame.dispose();
                } else {
                    System.err.println("❌ No se pudo actualizar la orden en la BD");
                    JOptionPane.showMessageDialog(frame, 
                        "Error al actualizar la orden en la base de datos.\n\n" +
                        "Revisa la consola para más detalles.",
                        "Error al Actualizar", 
                        JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
            
            // Crear nueva orden
            // Generar número de orden de forma segura desde la BD
            String numeroOrdenNuevo = com.mycompany.laboratorioapp.dao.OrdenDAO.obtenerSiguienteNumeroOrden();
            
            Orden orden = new Orden(
                    numeroOrdenNuevo,
                    "", // Factura N°
                    "", // Control N°
                    "", // Lote N°
                    fecha,
                    hora,
                    codigoPaciente,
                    cedula,
                    paciente.getNombre(),
                    paciente.getApellido(),
                    paciente.getDireccion(),
                    paciente.getTelefono(),
                    paciente.getCorreo(),
                    codigoEmpresa,
                    empresaSeleccionada,
                    paciente.getSexo() != null ? paciente.getSexo() : "",
                    examenes,
                    total
            );
            
            // Guardar la orden en la base de datos
            System.out.println("🔄 Guardando nueva orden número: " + numeroOrdenNuevo);
            System.out.println("   Cédula: " + cedula);
            System.out.println("   Total: " + total);
            System.out.println("   Exámenes: " + examenes.size());
            
            boolean guardado = OrdenManager.agregarOrdenConResultado(orden);
            
            if (guardado) {
                System.out.println("✅ Orden guardada correctamente en BD");
                
                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(frame, 
                    "Orden guardada exitosamente en la base de datos.\nNúmero de orden: " + numeroOrdenNuevo,
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Esperar un momento para asegurar que la BD se actualizó
                try {
                    Thread.sleep(200); // Aumentado a 200ms para mayor seguridad
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                
                // Refrescar la tabla de órdenes desde la base de datos
                if (ventanaPadre != null) {
                    System.out.println("🔄 Refrescando tabla de órdenes...");
                    ventanaPadre.refrescarTabla();
                }
                
                // Refrescar resultados para que se refleje de inmediato
                System.out.println("🔄 Refrescando ventana de resultados...");
                VentanaResultados.refrescarTodas();
                
                // Cerrar la ventana de detalles
                frame.dispose();
            } else {
                // Mostrar error si no se pudo guardar
                System.err.println("❌ No se pudo guardar la orden en la BD");
                JOptionPane.showMessageDialog(frame, 
                    "Error al guardar la orden en la base de datos.\n\n" +
                    "Posibles causas:\n" +
                    "• Problema de conexión a MySQL\n" +
                    "• Usuario no configurado correctamente\n" +
                    "• Error en el formato de datos\n\n" +
                    "Revisa la consola para más detalles.",
                    "Error al Guardar", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cargar orden por número
        btnCargar.addActionListener(e -> {
            String numero = JOptionPane.showInputDialog(frame, "Ingrese el número de orden:", "Cargar Orden", JOptionPane.QUESTION_MESSAGE);
            if (numero == null || numero.trim().isEmpty()) return;
            numero = numero.trim();
            Orden o = OrdenManager.buscarPorNumero(numero);
            if (o == null) {
                JOptionPane.showMessageDialog(frame, "No se encontró la orden " + numero, "Cargar", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Cargar la orden en el formulario
            cargarOrden(o);
            JOptionPane.showMessageDialog(frame, "Orden " + numero + " cargada correctamente.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
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
        btnSiguiente.addActionListener(e -> navegarSiguienteOMostrarNuevo());

        btnOBio.addActionListener(e -> new VentanaOBioanalista().mostrar());
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String obtenerEmpresaSeleccionada() {
        Object seleccion = entidadCombo != null ? entidadCombo.getSelectedItem() : null;
        String empresa = seleccion != null ? seleccion.toString().trim() : "";
        return "-- Seleccionar --".equalsIgnoreCase(empresa) ? "" : empresa;
    }

    private String obtenerCodigoPaciente(String cedula, java.util.Map<String, String> datosPacienteExcel) {
        Paciente paciente = GestorPacientes.buscarPorCedula(cedula);
        if (paciente != null && paciente.getCodigo() != null && !paciente.getCodigo().isBlank()) {
            return paciente.getCodigo();
        }
        if (ordenActual != null && ordenActual.getCodigoPaciente() != null && !ordenActual.getCodigoPaciente().isBlank()) {
            return ordenActual.getCodigoPaciente();
        }
        return datosPacienteExcel.getOrDefault("codigo", "").trim();
    }

    private String obtenerCodigoEmpresa(String cedula, String empresaSeleccionada, Orden ordenExistente) {
        if (ordenExistente != null && ordenExistente.getCodigoEmpresa() != null && !ordenExistente.getCodigoEmpresa().isBlank()) {
            return ordenExistente.getCodigoEmpresa();
        }
        if (empresaSeleccionada == null || empresaSeleccionada.isBlank()) {
            return "";
        }

        String rif = rifField != null && rifField.getText() != null ? rifField.getText().trim() : "";
        if (rif.isBlank()) {
            return "";
        }
        return normalizarDocumento(rif).equals(normalizarDocumento(cedula)) ? "" : rif;
    }

    private String normalizarDocumento(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    // ✅ Método nuevo para recibir cédula desde VentanaRegistroPacientes
    public void setCedulaPaciente(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return;
        
        // Establecer la cédula en el campo inmediatamente
        cedulaField.setText(cedula);
        
        // Cargar los datos del paciente con varios intentos para asegurar que la BD se actualizó
        new Thread(() -> {
            Paciente paciente = null;
            int intentos = 0;
            int maxIntentos = 5;
            
            // Intentar buscar el paciente varias veces con delays crecientes
            while (paciente == null && intentos < maxIntentos) {
                try {
                    // Delay progresivo: 200ms, 300ms, 400ms, etc.
                    Thread.sleep(200 + (intentos * 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                
                try {
                    paciente = GestorPacientes.buscarPorCedula(cedula);
                    if (paciente != null) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar paciente (intento " + (intentos + 1) + "): " + e.getMessage());
                }
                intentos++;
            }
            
            // Ejecutar la actualización en el hilo de Swing
            final Paciente pacienteFinal = paciente;
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (pacienteFinal != null) {
                    cargarPacienteEnCampos(pacienteFinal);
                } else {
                    // Si después de varios intentos no se encuentra, mostrar mensaje
                    System.err.println("⚠️ No se pudo cargar el paciente con cédula: " + cedula);
                    // Aún así, intentar cargar una vez más directamente
                    cargarDatosPaciente(cedula);
                }
            });
        }).start();
    }
    
    // Método auxiliar para cargar los datos del paciente
    private void cargarDatosPaciente(String cedula) {
        try {
        var paciente = GestorPacientes.buscarPorCedula(cedula);
        if (paciente != null) {
                cargarPacienteEnCampos(paciente);
            } else {
                System.err.println("⚠️ Paciente no encontrado con cédula: " + cedula);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos del paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método público para cargar un paciente completo (llamado desde VentanaRegistroPacientes)
    public void cargarPacienteCompleto(Paciente paciente) {
        if (paciente == null) return;
        cargarPacienteEnCampos(paciente);
    }
    
    // Método privado para cargar los datos del paciente en los campos
    private void cargarPacienteEnCampos(Paciente paciente) {
        try {
            // Cargar todos los campos del paciente
            cedulaField.setText(paciente.getCedula() != null ? paciente.getCedula() : "");
            nombreField.setText(paciente.getNombre() != null ? paciente.getNombre() : "");
            apellidoField.setText(paciente.getApellido() != null ? paciente.getApellido() : "");
            direccionField.setText(paciente.getDireccion() != null ? paciente.getDireccion() : "");
            telefonoField.setText(paciente.getTelefono() != null ? paciente.getTelefono() : "");
            correoField.setText(paciente.getCorreo() != null ? paciente.getCorreo() : "");
            rifField.setText(paciente.getCedula() != null ? paciente.getCedula() : "");
            edadField.setText(obtenerEdadPaciente(paciente, paciente.getCedula()));
            // El sexo se guardará automáticamente cuando se guarde la orden desde el paciente
            System.out.println("✅ Datos del paciente cargados: " + paciente.getNombre() + " " + paciente.getApellido() + " (Sexo: " + paciente.getSexo() + ")");
        } catch (Exception e) {
            System.err.println("❌ Error al cargar campos del paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarEdadPaciente(Paciente paciente) {
        String edadActual = edadField != null && edadField.getText() != null
                ? edadField.getText().trim()
                : "";
        if (!edadActual.isEmpty()) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                frame,
                "Este paciente no tiene edad o fecha de nacimiento registrada.\n¿Desea completarla ahora?",
                "Edad no registrada",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            VentanaRegistroPacientes ventanaRegistro = new VentanaRegistroPacientes(paciente, this);
            ventanaRegistro.mostrar();
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

        if (indicePaciente < 0) {
            // Si no hay actual y se presiona "Anterior", ir al último
            if (delta < 0) {
                indicePaciente = cachePacientes.size();
            } else {
                // Si no hay actual y se presiona "Siguiente", mostrar nuevo
                limpiarFormulario();
                indicePaciente = -1;
                return;
            }
        }
        int nuevo = indicePaciente + delta;
        if (nuevo < 0) return; // fuera por debajo
        if (nuevo >= cachePacientes.size()) { // superó el último → nuevo paciente
            limpiarFormulario();
            indicePaciente = -1;
            return;
        }
        indicePaciente = nuevo;
        mostrarPaciente(cachePacientes.get(indicePaciente));
    }

    private void navegarSiguienteOMostrarNuevo() {
        if (cachePacientes == null) cachePacientes = GestorPacientes.cargarPacientes();
        if (cachePacientes.isEmpty()) { limpiarFormulario(); indicePaciente = -1; return; }
        if (indicePaciente < 0) {
            // no hay actual → ir al primero
            indicePaciente = 0;
            mostrarPaciente(cachePacientes.get(indicePaciente));
            return;
        }
        int nuevo = indicePaciente + 1;
        if (nuevo >= cachePacientes.size()) {
            // después del último → formulario nuevo
            limpiarFormulario();
            indicePaciente = -1;
        } else {
            indicePaciente = nuevo;
            mostrarPaciente(cachePacientes.get(indicePaciente));
        }
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
        edadField.setText(obtenerEdadPaciente(p, p.getCedula()));
    }

    private void filtrarExamenes() {
        if (sorterExamenes == null || filtroExamenField == null) {
            return;
        }

        String texto = filtroExamenField.getText() != null ? filtroExamenField.getText().trim() : "";
        if (texto.isEmpty()) {
            sorterExamenes.setRowFilter(null);
        } else {
            String termino = java.util.regex.Pattern.quote(texto);
            sorterExamenes.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + termino, 0, 1));
        }
        resaltarPrimerExamenVisible();
    }

    private void resaltarPrimerExamenVisible() {
        if (tablaExamenes == null) {
            return;
        }
        seleccionProgramaticaExamen = true;
        try {
            if (tablaExamenes.getRowCount() > 0) {
                tablaExamenes.setRowSelectionInterval(0, 0);
                tablaExamenes.scrollRectToVisible(tablaExamenes.getCellRect(0, 1, true));
            } else {
                tablaExamenes.clearSelection();
            }
        } finally {
            seleccionProgramaticaExamen = false;
        }
    }

    private void agregarExamenSeleccionado() {
        if (tablaExamenes == null) {
            return;
        }
        int filaVista = tablaExamenes.getSelectedRow();
        if (filaVista < 0) {
            return;
        }

        int filaModelo = tablaExamenes.convertRowIndexToModel(filaVista);
        String codigo = String.valueOf(tablaExamenes.getModel().getValueAt(filaModelo, 0));
        String descripcion = String.valueOf(tablaExamenes.getModel().getValueAt(filaModelo, 1));
        Double precio = ExamenDAO.obtenerPrecio(codigo);
        double costo = precio != null ? precio : 0.0;
        modeloFactura.addRow(new Object[]{descripcion, String.format("%.2f", costo)});
        actualizarTotales();
    }

    private String obtenerEdadPaciente(Paciente paciente, String cedula) {
        if (paciente != null) {
            if (paciente.getEdad() > 0) {
                return String.valueOf(paciente.getEdad());
            }
            if (paciente.getFechaNacimiento() != null) {
                int edadCalculada = java.time.Period.between(
                        paciente.getFechaNacimiento(),
                        java.time.LocalDate.now()
                ).getYears();
                if (edadCalculada > 0) {
                    return String.valueOf(edadCalculada);
                }
            }
        }

        java.util.Map<String, String> datosExcel = PacienteExcelHelper.buscarPorCedula(cedula);
        String edadExcel = datosExcel.getOrDefault("edad", "").trim();
        if (!edadExcel.isEmpty() && !"0".equals(edadExcel)) {
            return edadExcel;
        }

        String fechaNacimiento = datosExcel.getOrDefault("fechaNacimiento", "").trim();
        if (!fechaNacimiento.isEmpty()) {
            try {
                java.time.LocalDate fecha = java.time.LocalDate.parse(
                        fechaNacimiento,
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                );
                int edadCalculada = java.time.Period.between(fecha, java.time.LocalDate.now()).getYears();
                if (edadCalculada > 0) {
                    return String.valueOf(edadCalculada);
                }
            } catch (Exception ignored) {
            }
        }

        return "";
    }

    private int parseEnteroSeguro(String texto) {
        try { return Integer.parseInt(texto); } catch (Exception ex) { return 0; }
    }

    private void limpiarFormulario() {
        ordenActual = null; // Resetear orden actual
        cedulaField.setText("");
        nombreField.setText("");
        apellidoField.setText("");
        direccionField.setText("");
        telefonoField.setText("");
        correoField.setText("");
        rifField.setText("");
        edadField.setText("");
        if (entidadCombo != null && entidadCombo.getItemCount() > 0) {
            entidadCombo.setSelectedIndex(0);
        }
        // Limpiar factura y totales mínimos para nueva orden
        if (modeloFactura != null) {
            modeloFactura.setRowCount(0);
        }
        subtotalField.setText("");
        descuentoField.setText("");
        recargoField.setText("");
        totalField.setText("");
        saldoField.setText("");
        if (diagnosticoArea != null) diagnosticoArea.setText("");
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

