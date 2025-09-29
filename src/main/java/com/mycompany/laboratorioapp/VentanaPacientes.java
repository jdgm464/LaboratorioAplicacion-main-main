package com.mycompany.laboratorioapp;


import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;




public class VentanaPacientes {
    // Persistir qué filas (por clave) se han ocultado voluntariamente
    private static final String OCULTOS_FILE = "pacientes_ocultos.properties";
    private static final Map<String, Integer> ocultosPorClave = new HashMap<>();

    static {
        cargarOcultosDesdeArchivo();
    }
    // Permite recargar la tabla de pacientes desde Excel desde otras clases
    public void cargarPacientes() {
        cargarPacientesDesdeExcel();
    }
    private final JFrame frame;
    private final JTable tablaPacientes;
    private final DefaultTableModel modeloTabla;
    // Campos de búsqueda
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtCodigoWeb;
    private JSpinner desdeSpinner;
    private JSpinner hastaSpinner;
    private JLabel lblVisitas;

    public VentanaPacientes() {
        frame = new JFrame("Registro de Pacientes");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ------------------ PANEL SUPERIOR (BÚSQUEDA) ------------------
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelBusqueda.add(new JLabel("Cédula"));
        txtCedula = new JTextField(10);
        panelBusqueda.add(txtCedula);

        panelBusqueda.add(new JLabel("Nombres"));
        txtNombres = new JTextField(10);
        panelBusqueda.add(txtNombres);

        panelBusqueda.add(new JLabel("Apellidos"));
        txtApellidos = new JTextField(10);
        panelBusqueda.add(txtApellidos);

        panelBusqueda.add(new JLabel("Código Web"));
        txtCodigoWeb = new JTextField(10);
        panelBusqueda.add(txtCodigoWeb);

        // Filtros de fecha (Desde / Hasta)
        panelBusqueda.add(new JLabel("Desde"));
        desdeSpinner = new JSpinner(new SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        desdeSpinner.setEditor(new JSpinner.DateEditor(desdeSpinner, "dd/MM/yyyy"));
        panelBusqueda.add(desdeSpinner);

        panelBusqueda.add(new JLabel("Hasta"));
        hastaSpinner = new JSpinner(new SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        hastaSpinner.setEditor(new JSpinner.DateEditor(hastaSpinner, "dd/MM/yyyy"));
        panelBusqueda.add(hastaSpinner);

        JButton btnContar = new JButton("Contar visitas");
        panelBusqueda.add(btnContar);

        lblVisitas = new JLabel("Visitas: -");
        panelBusqueda.add(lblVisitas);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        // ------------------ TABLA CENTRAL ------------------
    String[] columnas = {"Código", "Nombres", "Apellidos", "Cédula", "Fecha Nacimiento", "Edad", "Sexo", "Dirección", "Teléfono1", "Teléfono2", "Sede", "Categoría", "Dedicación", "Estatus", "Fecha Ingreso", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPacientes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaPacientes);

        panel.add(scrollPane, BorderLayout.CENTER);

        // ------------------ BOTONES INFERIORES ------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");
        JButton btnRecargar = new JButton("Recargar");

        btnSalir.addActionListener(e -> frame.dispose());
        btnEliminar.addActionListener(e -> eliminarFilaSeleccionada());
        btnRecargar.addActionListener(e -> { modeloTabla.setRowCount(0); cargarPacientesDesdeExcel(); });

        panelBotones.add(btnNuevo);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRecargar);
        panelBotones.add(btnSalir);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // Listeners para conteo de visitas en rango
        btnContar.addActionListener(e -> contarVisitasEnRango());
        desdeSpinner.addChangeListener(e -> contarVisitasEnRango());
        hastaSpinner.addChangeListener(e -> contarVisitasEnRango());
        txtCedula.addActionListener(e -> contarVisitasEnRango());
        txtCodigoWeb.addActionListener(e -> contarVisitasEnRango());

        // ------------------ CARGAR EXCEL ------------------
        cargarPacientesDesdeExcel();

        frame.add(panel);
    }

    private void contarVisitasEnRango() {
        try {
            String cedula = txtCedula.getText() != null ? txtCedula.getText().trim() : "";
            if (cedula.isEmpty()) {
                // Si hay fila seleccionada, usar su cédula
                int sel = tablaPacientes.getSelectedRow();
                if (sel >= 0) {
                    Object v = modeloTabla.getValueAt(sel, 3);
                    if (v != null) cedula = v.toString().trim();
                }
            }
            if (cedula.isEmpty()) {
                // Buscar por código web si está
                String codigo = txtCodigoWeb.getText() != null ? txtCodigoWeb.getText().trim() : "";
                if (!codigo.isEmpty()) {
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        Object codVal = modeloTabla.getValueAt(i, 0);
                        if (codVal != null && codigo.equalsIgnoreCase(codVal.toString().trim())) {
                            Object v = modeloTabla.getValueAt(i, 3);
                            if (v != null) { cedula = v.toString().trim(); break; }
                        }
                    }
                }
            }
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Indique la cédula, seleccione un paciente o ingrese Código Web.");
                return;
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            java.util.Date desde = (java.util.Date) desdeSpinner.getValue();
            java.util.Date hasta = (java.util.Date) hastaSpinner.getValue();
            if (hasta.before(desde)) {
                JOptionPane.showMessageDialog(frame, "La fecha 'Hasta' no puede ser anterior a 'Desde'.");
                return;
            }

            String cedNorm = normalizarCedula(cedula);
            int count = 0;
            for (Orden o : OrdenManager.getOrdenes()) {
                String oc = normalizarCedula(o.getCedula());
                if (!cedNorm.equalsIgnoreCase(oc)) continue;
                try {
                    java.util.Date f = sdf.parse(o.getFechaRegistro());
                    if ((f.equals(desde) || f.after(desde)) && (f.equals(hasta) || f.before(hasta))) {
                        count++;
                    }
                } catch (Exception ignored) {}
            }
            lblVisitas.setText("Visitas: " + count);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error al contar visitas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPacientesDesdeExcel() {
        try {
            Workbook workbook;
            java.io.File externo = new java.io.File("IPPUSNEG informacion..xlsx");
            if (externo.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(externo)) {
                    workbook = WorkbookFactory.create(fis);
                }
            } else {
                try (java.io.InputStream file = getClass().getClassLoader().getResourceAsStream("IPPUSNEG informacion..xlsx")) {
                    if (file == null) throw new Exception("No se encontró el archivo IPPUSNEG informacion..xlsx en resources");
                    workbook = WorkbookFactory.create(file);
                }
            }
            Sheet sheet = workbook.getSheetAt(0);

            // Leer cabeceras para crear un mapeo por nombre -> índice
            Row header = sheet.getRow(0);
            java.util.Map<String, Integer> map = new java.util.HashMap<>();
            for (int j = 0; j < 30; j++) {
                if (header != null && header.getCell(j) != null) {
                    String h = header.getCell(j).toString();
                    String key = normalizar(h);
                    map.put(key, j);
                }
            }

            // Helper para obtener por nombre lógico con alias y búsqueda parcial
            java.util.function.Function<String, Integer> idx = nombre -> getCol(map, nombre);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

            Map<String, Integer> restantesOcultos = new HashMap<>(ocultosPorClave);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Object[] fila = new Object[16];
                fila[0]  = leerCelda(row, idx.apply("codigo"), false, true);
                fila[1]  = leerCelda(row, idx.apply("nombres"), false, false);
                fila[2]  = leerCelda(row, idx.apply("apellidos"), false, false);
                fila[3]  = leerCelda(row, idx.apply("cedula"), false, false);
                fila[4]  = leerCeldaFecha(row, idx.apply("fecha nacimiento"), sdf);
                fila[5]  = leerCelda(row, idx.apply("edad"), false, true);
                fila[6]  = leerCelda(row, idx.apply("sexo"), false, false);
                fila[7]  = leerCelda(row, idx.apply("direccion"), false, false);
                fila[8]  = leerCelda(row, idx.apply("telefono1"), false, true);
                fila[9]  = leerCelda(row, idx.apply("telefono2"), false, true);
                fila[10] = leerCelda(row, idx.apply("sede"), false, false);
                fila[11] = leerCelda(row, idx.apply("categoria"), false, false);
                fila[12] = leerCelda(row, idx.apply("dedicacion"), false, false);
                fila[13] = leerCelda(row, idx.apply("estatus"), false, false);
                fila[14] = leerCeldaFecha(row, idx.apply("fecha ingreso"), sdf);
                fila[15] = leerCelda(row, idx.apply("email"), false, false);

                // Aplicar ocultamientos persistentes por clave
                String clave = construirClaveFila(fila);
                int rest = restantesOcultos.getOrDefault(clave, 0);
                if (rest > 0) {
                    restantesOcultos.put(clave, rest - 1);
                } else {
                    modeloTabla.addRow(fila);
                }
            }
            // Nota: no persistimos los consumos; el ocultamiento permanece
            // hasta que el usuario agregue nuevas filas iguales (que sí se mostrarán)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---- Utilidades de lectura ----
    private static String normalizar(String s) {
        if (s == null) return "";
        String t = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        t = t.toLowerCase().trim();
        t = t.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
        t = t.replace("n°", "numero");
        t = t.replaceAll("[^a-z0-9 ]", "");
        t = t.replace("  ", " ");
        return t;
    }

    // Devuelve índice por nombre, admitiendo alias y coincidencia parcial
    private static int getCol(java.util.Map<String,Integer> map, String nombre) {
        String key = normalizar(nombre);
        if (map.containsKey(key)) return map.get(key);
        // alias por campo
        String[] aliases = switch (key) {
            case "codigo" -> new String[]{"codigo web", "codigo paciente", "cod"};
            case "nombres" -> new String[]{"nombre"};
            case "apellidos" -> new String[]{"apellido"};
            case "cedula" -> new String[]{"cédula"};
            case "telefono1" -> new String[]{"telefono", "tlf1", "telefono 1"};
            case "telefono2" -> new String[]{"tlf2", "telefono 2"};
            case "fecha nacimiento" -> new String[]{"fecha nac", "nacimiento"};
            case "fecha ingreso" -> new String[]{"ingreso"};
            case "email" -> new String[]{"correo", "correo electronico"};
            default -> new String[]{};
        };
        for (String a : aliases) {
            String ak = normalizar(a);
            if (map.containsKey(ak)) return map.get(ak);
        }
        // búsqueda parcial
        for (var entry : map.entrySet()) {
            if (entry.getKey().contains(key)) return entry.getValue();
        }
        return -1;
    }

    private static String normalizarCedula(String s) {
        if (s == null) return "";
        String raw = s.trim().toUpperCase();
        String pref = (raw.startsWith("V") || raw.startsWith("E")) ? raw.substring(0,1) : "V";
        String resto = raw.substring(pref.length()).replaceAll("[^0-9]", "");
        return pref + resto;
    }

    private static String construirClaveFila(Object[] fila) {
        String ced = normalizarCedula(fila[3] != null ? fila[3].toString() : "");
        String cod = fila[0] != null ? fila[0].toString().trim() : "";
        String nom = fila[1] != null ? fila[1].toString().trim().toUpperCase() : "";
        String ape = fila[2] != null ? fila[2].toString().trim().toUpperCase() : "";
        String tel = fila[8] != null ? fila[8].toString().trim() : "";
        return ced + "|" + cod + "|" + nom + "|" + ape + "|" + tel;
    }

    private static Object leerCelda(Row row, int col, boolean esFecha, boolean enteroPreferido) {
        if (col < 0) return "";
        var c = row.getCell(col);
        if (c == null) return "";
        switch (c.getCellType()) {
            case STRING -> { return c.getStringCellValue(); }
            case NUMERIC -> {
                double v = c.getNumericCellValue();
                if (enteroPreferido || v == (long) v) return String.valueOf((long) v);
                return String.valueOf(v);
            }
            case FORMULA -> {
                var t = c.getCachedFormulaResultType();
                if (t == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                    double v = c.getNumericCellValue();
                    if (enteroPreferido || v == (long) v) return String.valueOf((long) v);
                    return String.valueOf(v);
                } else if (t == org.apache.poi.ss.usermodel.CellType.STRING) {
                    return c.getStringCellValue();
                } else return "";
            }
            default -> { return ""; }
        }
    }

    private static Object leerCeldaFecha(Row row, int col, java.text.SimpleDateFormat sdf) {
        if (col < 0) return "";
        var c = row.getCell(col);
        if (c == null) return "";
        try {
            if (c.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC ||
                c.getCachedFormulaResultType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                java.util.Date d = c.getDateCellValue();
                return d != null ? sdf.format(d) : "";
            }
        } catch (Exception ignored) {}
        return c.toString();
    }

    // ---- Eliminar sólo de la vista ----
    private void eliminarFilaSeleccionada() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila >= 0) {
            // Construir clave de la fila y marcarla como oculta (persistente)
            Object[] datos = new Object[modeloTabla.getColumnCount()];
            for (int c = 0; c < datos.length && c < 16; c++) {
                datos[c] = modeloTabla.getValueAt(fila, c);
            }
            String clave = construirClaveFila(datos);
            ocultosPorClave.put(clave, ocultosPorClave.getOrDefault(clave, 0) + 1);
            guardarOcultosEnArchivo();
            modeloTabla.removeRow(fila);
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un paciente para eliminar.");
        }
    }

    private static void cargarOcultosDesdeArchivo() {
        try {
            File f = new File(OCULTOS_FILE);
            if (!f.exists()) return;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
            }
            ocultosPorClave.clear();
            for (String k : p.stringPropertyNames()) {
                try {
                    int val = Integer.parseInt(p.getProperty(k, "0"));
                    if (val > 0) ocultosPorClave.put(k, val);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private static void guardarOcultosEnArchivo() {
        try {
            Properties p = new Properties();
            for (Map.Entry<String, Integer> e : ocultosPorClave.entrySet()) {
                if (e.getValue() != null && e.getValue() > 0) {
                    p.setProperty(e.getKey(), String.valueOf(e.getValue()));
                }
            }
            try (FileOutputStream fos = new FileOutputStream(OCULTOS_FILE)) {
                p.store(fos, "Pacientes ocultos en la vista");
            }
        } catch (Exception ignored) {}
    }

    public void mostrar() {
        frame.setVisible(true);
    }
}