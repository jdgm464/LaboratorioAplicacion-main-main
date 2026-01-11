package com.mycompany.laboratorioapp.examenes;

import com.mycompany.laboratorioapp.BaseDeDatosExcel;
import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.examenes.Examen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaControlPrecios {
    private final JFrame frame;

    // Controles sección 1
    private final JCheckBox chkPorNivel;
    private final JComboBox<String> comboNivelPrecio;

    // Sección 2
    private final JCheckBox chkPorArea;
    private final JComboBox<String> comboArea;
    private final JComboBox<String> comboAreaPrecio;

    // Sección 3
    private final JCheckBox chkPorGrupo;
    private final JComboBox<String> comboGrupo;
    private final JComboBox<String> comboGrupoPrecio;

    // Sección 4 (cambio de precios)
    private final JCheckBox chkCambioPrecios;
    private final JComboBox<String> comboPrecioCalcular;
    private final JComboBox<String> comboPrecioBase;
    private final JComboBox<String> comboOperador;
    private final JTextField campoValor;
    private final JButton btnCalcular;

    // Tabla inferior
    private final DefaultTableModel modeloTabla;
    private java.util.List<Examen> examenesCargados = new java.util.ArrayList<>();

    public VentanaControlPrecios() {
        frame = new JFrame("Control de Precios");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Título
        JLabel titulo = new JLabel("Control de Precios");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        contenido.add(titulo);

        contenido.add(Box.createVerticalStrut(10));

        // ====== Sección: Solo permite consultar precio por nivel ======
        JPanel s1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        chkPorNivel = new JCheckBox("Solo permite consultar precio por nivel");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        s1.add(chkPorNivel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        s1.add(new JLabel("Precio a Consultar"), gbc);
        gbc.gridx = 1;
        comboNivelPrecio = new JComboBox<>(generarListaNiveles());
        comboNivelPrecio.setEnabled(false);
        s1.add(comboNivelPrecio, gbc);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnExcel = new JButton("Excel");
        JButton btnSalir = new JButton("Salir");
        gbc.gridy = 0; gbc.gridx = 3; gbc.gridwidth = 1;
        s1.add(btnAceptar, gbc);
        gbc.gridx = 4; s1.add(btnExcel, gbc);
        gbc.gridx = 5; s1.add(btnSalir, gbc);

        btnSalir.addActionListener(e -> frame.dispose());

        contenido.add(s1);
        contenido.add(Box.createVerticalStrut(10));

        // ====== Sección: Consulta por área ======
        JPanel s2 = new JPanel(new GridBagLayout());
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new java.awt.Insets(5,5,5,5);
        g2.anchor = GridBagConstraints.WEST;
        chkPorArea = new JCheckBox("Consulta el precio por área de pruebas o servicios");
        g2.gridx = 0; g2.gridy = 0; g2.gridwidth = 3; s2.add(chkPorArea, g2);
        g2.gridwidth = 1; g2.gridy = 1; g2.gridx = 0; s2.add(new JLabel("Área"), g2);
        g2.gridx = 1; comboArea = new JComboBox<>(new String[]{"Laboratorio clínico"}); comboArea.setEnabled(false); s2.add(comboArea, g2);
        g2.gridx = 2; s2.add(new JLabel("Precio a Consultar"), g2);
        g2.gridx = 3; comboAreaPrecio = new JComboBox<>(generarListaNiveles()); comboAreaPrecio.setEnabled(false); s2.add(comboAreaPrecio, g2);
        contenido.add(s2);
        contenido.add(Box.createVerticalStrut(10));

        // ====== Sección: Consulta por grupo ======
        JPanel s3 = new JPanel(new GridBagLayout());
        GridBagConstraints g3 = new GridBagConstraints();
        g3.insets = new java.awt.Insets(5,5,5,5);
        g3.anchor = GridBagConstraints.WEST;
        chkPorGrupo = new JCheckBox("Consulta el precio por grupo de pruebas o servicios");
        g3.gridx = 0; g3.gridy = 0; g3.gridwidth = 3; s3.add(chkPorGrupo, g3);
        g3.gridwidth = 1; g3.gridy = 1; g3.gridx = 0; s3.add(new JLabel("Grupo"), g3);
        g3.gridx = 1; comboGrupo = new JComboBox<>(new String[]{"Bacteriología"}); comboGrupo.setEnabled(false); s3.add(comboGrupo, g3);
        g3.gridx = 2; s3.add(new JLabel("Precio a Consultar"), g3);
        g3.gridx = 3; comboGrupoPrecio = new JComboBox<>(generarListaNiveles()); comboGrupoPrecio.setEnabled(false); s3.add(comboGrupoPrecio, g3);
        contenido.add(s3);
        contenido.add(Box.createVerticalStrut(10));

        // ====== Sección: Cambio de precios ======
        JPanel s4 = new JPanel(new GridBagLayout());
        GridBagConstraints g4 = new GridBagConstraints();
        g4.insets = new java.awt.Insets(5,5,5,5);
        g4.anchor = GridBagConstraints.WEST;
        chkCambioPrecios = new JCheckBox("Este proceso cambia los precios actuales del sistema");
        g4.gridx = 0; g4.gridy = 0; g4.gridwidth = 5; s4.add(chkCambioPrecios, g4);
        g4.gridwidth = 1; g4.gridy = 1; g4.gridx = 0; s4.add(new JLabel("Precio a Calcular"), g4);
        g4.gridx = 1; comboPrecioCalcular = new JComboBox<>(generarListaNiveles()); comboPrecioCalcular.setEnabled(false); s4.add(comboPrecioCalcular, g4);
        g4.gridx = 2; s4.add(new JLabel("Precio Base"), g4);
        g4.gridx = 3; comboPrecioBase = new JComboBox<>(generarListaNiveles()); comboPrecioBase.setEnabled(false); s4.add(comboPrecioBase, g4);
        g4.gridx = 4; s4.add(new JLabel("Operador"), g4);
        g4.gridx = 5; comboOperador = new JComboBox<>(new String[]{"x","/","+","-"}); comboOperador.setEnabled(false); s4.add(comboOperador, g4);
        g4.gridx = 6; s4.add(new JLabel("Valor"), g4);
        g4.gridx = 7; campoValor = new JTextField(8); campoValor.setEnabled(false); s4.add(campoValor, g4);
        g4.gridx = 8; btnCalcular = new JButton("Calcular"); btnCalcular.setEnabled(false); s4.add(btnCalcular, g4);
        contenido.add(s4);
        contenido.add(Box.createVerticalStrut(10));

        // ====== Tabla inferior ======
        modeloTabla = new DefaultTableModel(new String[]{"Código", "Descripción", "Precio"}, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(900, 380));
        contenido.add(scroll);

        frame.add(new JScrollPane(contenido), BorderLayout.CENTER);

        // ====== Eventos de habilitación ======
        chkPorNivel.addActionListener(e -> {
            comboNivelPrecio.setEnabled(chkPorNivel.isSelected());
            if (chkPorNivel.isSelected()) {
                // Cargar directamente desde la BD (ya no se necesita Excel)
                try {
                    examenesCargados = ExamenDAO.obtenerTodos();
                    if (examenesCargados == null || examenesCargados.isEmpty()) {
                        javax.swing.JOptionPane.showMessageDialog(frame, 
                            "No hay exámenes en la base de datos.\nUse 'Seleccionar Excel...' para importar desde un archivo Excel si es necesario.", 
                            "Sin exámenes", 
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                    llenarTablaConPrecios();
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(frame, 
                        "Error al cargar exámenes desde la base de datos: " + ex.getMessage(), 
                        "Error", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        chkPorArea.addActionListener(e -> {
            boolean on = chkPorArea.isSelected();
            comboArea.setEnabled(on);
            comboAreaPrecio.setEnabled(on);
        });
        chkPorGrupo.addActionListener(e -> {
            boolean on = chkPorGrupo.isSelected();
            comboGrupo.setEnabled(on);
            comboGrupoPrecio.setEnabled(on);
        });
        chkCambioPrecios.addActionListener(e -> {
            boolean on = chkCambioPrecios.isSelected();
            comboPrecioCalcular.setEnabled(on);
            comboPrecioBase.setEnabled(on);
            comboOperador.setEnabled(on);
            campoValor.setEnabled(on);
            btnCalcular.setEnabled(on);
        });

        // ====== Lógica básica: llenar tabla según selección de "precio a consultar" ======
        java.awt.event.ActionListener refrescarTabla = e -> llenarTablaConPrecios();
        comboNivelPrecio.addActionListener(refrescarTabla);
        comboArea.addActionListener(refrescarTabla);
        comboAreaPrecio.addActionListener(refrescarTabla);
        comboGrupo.addActionListener(refrescarTabla);
        comboGrupoPrecio.addActionListener(refrescarTabla);

        // Habilitar edición y guardado de precios desde la tabla (columna Precio)
        JButton btnGuardarCambios = new JButton("Guardar cambios");
        btnGuardarCambios.addActionListener(e -> guardarCambiosEnExcel());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnSeleccionarExcel = new JButton("Seleccionar Excel...");
        btnSeleccionarExcel.addActionListener(e -> seleccionarArchivoLista());
        acciones.add(btnSeleccionarExcel);
        acciones.add(btnGuardarCambios);
        frame.add(acciones, BorderLayout.SOUTH);
    }

    private String[] generarListaNiveles() {
        String[] niveles = new String[30];
        int idx = 0;
        // Contado 1..10
        for (int i = 1; i <= 10; i++) niveles[idx++] = "Contado" + i;
        // Crédito 1..10
        for (int i = 1; i <= 10; i++) niveles[idx++] = "Crédito" + i;
        // Dólar Contado 1..10
        for (int i = 1; i <= 10; i++) niveles[idx++] = "DólarContado" + i;
        return niveles;
    }

    private void llenarTablaConPrecios() {
        modeloTabla.setRowCount(0);
        String criterio = null;
        if (chkPorNivel.isSelected()) criterio = (String) comboNivelPrecio.getSelectedItem();
        else if (chkPorArea.isSelected()) criterio = (String) comboAreaPrecio.getSelectedItem();
        else if (chkPorGrupo.isSelected()) criterio = (String) comboGrupoPrecio.getSelectedItem();
        if (criterio == null) return;

        // Para DólarContado1, mostrar todos los exámenes con su precio actual desde la BD
        if (criterio != null && criterio.equalsIgnoreCase("DólarContado1")) {
            try {
                // Cargar desde la BD directamente
                examenesCargados = ExamenDAO.obtenerTodos();
                
                for (Examen ex : examenesCargados) {
                    String codigo = ex.getCodigo() == null ? "" : ex.getCodigo();
                    double precio = ex.getPrecio();
                    modeloTabla.addRow(new Object[]{codigo, ex.getNombre(), String.format("%.2f", precio)});
                }
            } catch (Exception e) {
                System.err.println("Error al cargar exámenes desde BD: " + e.getMessage());
            }
        }
    }

    private int obtenerIndiceNivel(String nivel) {
        try {
            String dig = nivel.replaceAll("[^0-9]", "");
            return Integer.parseInt(dig);
        } catch (Exception e) { return 1; }
    }

    public void mostrar() { frame.setVisible(true); }

    private void guardarCambiosEnExcel() {
        try {
            int cambiosGuardados = 0;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codigo = String.valueOf(modeloTabla.getValueAt(i, 0));
                String precioStr = String.valueOf(modeloTabla.getValueAt(i, 2));
                try {
                    double nuevoPrecio = Double.parseDouble(precioStr.replace(",", "."));
                    // Actualizar directamente en la base de datos
                    BaseDeDatosExcel.actualizarPrecio(codigo, nuevoPrecio);
                    cambiosGuardados++;
                } catch (Exception ignored) {}
            }
            // Ya no se guarda en Excel, solo en la BD
            javax.swing.JOptionPane.showMessageDialog(frame, 
                "Cambios guardados en la base de datos (" + cambiosGuardados + " precios actualizados).");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(frame, 
                "Error al guardar: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarArchivoLista() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo Excel para importar exámenes");
        int r = chooser.showOpenDialog(frame);
        if (r == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File f = chooser.getSelectedFile();
            try {
                // Importar desde Excel y guardar en la BD
                BaseDeDatosExcel.cargarDesdeExcel(f.getAbsolutePath());
                BaseDeDatosExcel.cargarListaPrecios(f.getAbsolutePath());
                // Cargar los exámenes actualizados desde la BD
                examenesCargados = ExamenDAO.obtenerTodos();
                llenarTablaConPrecios();
                javax.swing.JOptionPane.showMessageDialog(frame, 
                    "Exámenes importados exitosamente desde el archivo Excel y guardados en la base de datos.");
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(frame, 
                    "No se pudo importar el archivo: " + ex.getMessage(), 
                    "Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

