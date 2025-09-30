package com.mycompany.laboratorioapp;

// import org.apache.poi.ss.usermodel.*; // No se usa
// import org.apache.poi.xssf.usermodel.XSSFWorkbook; // No se usa
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.Normalizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class VentanaRegistroPacientes {
    private final JFrame frame;
    private final JTextField cedulaField, codigoField, nombresField, apellidosField,
            direccionField, fechaNacField, telefonoField,
            correoField, edadField;
    private final JComboBox<String> nacionalidadCombo, sexoCombo, entidadCombo;
    private final JCheckBox rnCheck;
    private final JCheckBox familiarCheck;

    private final VentanaDetallesOrdenes ventanaDetalles;

    public VentanaRegistroPacientes(JFrame parent, VentanaDetallesOrdenes ventanaDetalles) {
        this.ventanaDetalles = ventanaDetalles;

        frame = new JFrame("Registro de Pacientes");
        frame.setSize(750, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(parent);

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // ----- Fila 1: Cedula + Nacionalidad + Codigo + Familiar -----
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(new JLabel("Cédula:"), gbc);

        gbc.gridx = 1;
        cedulaField = new JTextField(12);
        panelCampos.add(cedulaField, gbc);

        gbc.gridx = 2;
        panelCampos.add(new JLabel("Nacionalidad:"), gbc);

        gbc.gridx = 3;
        nacionalidadCombo = new JComboBox<>(new String[]{"V", "E"});
        panelCampos.add(nacionalidadCombo, gbc);

        gbc.gridx = 4;
        panelCampos.add(new JLabel("Código:"), gbc);

        gbc.gridx = 5;
        codigoField = new JTextField(10);
        panelCampos.add(codigoField, gbc);

        gbc.gridx = 6;
        panelCampos.add(new JLabel("Familiar:"), gbc);
        gbc.gridx = 7;
        familiarCheck = new JCheckBox();
        panelCampos.add(familiarCheck, gbc);

        // ----- Fila 2: Nombres + R/N -----
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(new JLabel("Nombres:"), gbc);

        gbc.gridx = 1;
        nombresField = new JTextField(15);
        panelCampos.add(nombresField, gbc);

        gbc.gridx = 2;
        panelCampos.add(new JLabel("R/N:"), gbc);

        gbc.gridx = 3;
        rnCheck = new JCheckBox();
        panelCampos.add(rnCheck, gbc);

        // ----- Apellidos -----
        apellidosField = crearCampo(panelCampos, gbc, 2, "Apellidos:");

        // ----- Sexo (combo) -----
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCampos.add(new JLabel("Sexo:"), gbc);

        gbc.gridx = 1;
        sexoCombo = new JComboBox<>(new String[]{"Masculino", "Femenino"});
        panelCampos.add(sexoCombo, gbc);

        // ----- Fecha Nacimiento + Edad -----
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelCampos.add(new JLabel("Fecha de Nacimiento:"), gbc);

        gbc.gridx = 1;
        fechaNacField = new JTextField(12);
        panelCampos.add(fechaNacField, gbc);

        gbc.gridx = 2;
        panelCampos.add(new JLabel("Edad:"), gbc);

        gbc.gridx = 3;
        edadField = new JTextField(5);
        edadField.setEditable(false); // 🚀 edad solo calculada
        panelCampos.add(edadField, gbc);

        // 🚀 Evento para calcular edad automáticamente
        fechaNacField.addActionListener(e -> calcularEdad());

        // ----- Dirección -----
        direccionField = crearCampo(panelCampos, gbc, 5, "Dirección:");

        // ----- Teléfono -----
        telefonoField = crearCampo(panelCampos, gbc, 6, "Teléfono:");

        // ----- Correo -----
        correoField = crearCampo(panelCampos, gbc, 7, "Correo:");

        // ----- Entidad (combo de hospitales/entidades) -----
        gbc.gridx = 0;
        gbc.gridy = 8;
        panelCampos.add(new JLabel("Entidad:"), gbc);

        gbc.gridx = 1;
        entidadCombo = new JComboBox<>(new String[]{
                "Hospital Central", "Clínica Popular", "Centro Médico Privado"
        });
        panelCampos.add(entidadCombo, gbc);

        frame.add(new JScrollPane(panelCampos), BorderLayout.CENTER);

        // ----- Botones -----
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnSalir = new JButton("Salir"); // 🚀 cambiado de Borrar → Salir
        JButton btnDeshacer = new JButton("Deshacer");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnSalir);
        panelBotones.add(btnDeshacer);

        frame.add(panelBotones, BorderLayout.SOUTH);

        // ----- Acciones -----
        btnGuardar.addActionListener(e -> guardarPaciente());
        btnActualizar.addActionListener(e -> actualizarPaciente());
        btnSalir.addActionListener(e -> frame.dispose()); // 🚀 salir cierra ventana
        btnDeshacer.addActionListener(e -> limpiarOCerrar());

        // Generar código secuencial automáticamente al abrir (no incrementa el contador global)
        String codigoAuto = proponerSiguienteCodigo();
        if (codigoAuto != null && !codigoAuto.isBlank()) {
            codigoField.setText(codigoAuto);
            codigoField.setEditable(false);
        }

        // Si es familiar, no asignar código
        familiarCheck.addActionListener(e -> {
            if (familiarCheck.isSelected()) {
                codigoField.setText("");
                codigoField.setEditable(false);
            } else {
                String codigoAuto2 = proponerSiguienteCodigo();
                if (codigoAuto2 != null && !codigoAuto2.isBlank()) {
                    codigoField.setText(codigoAuto2);
                    codigoField.setEditable(false);
                }
            }
        });
    }

    private JTextField crearCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        JTextField campo = new JTextField(25);
        panel.add(campo, gbc);

        return campo;
    }

    // 🚀 Método para calcular edad
    private void calcularEdad() {
        String fechaTexto = fechaNacField.getText().trim();
        if (!fechaTexto.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate nacimiento = LocalDate.parse(fechaTexto, formatter);
                int edad = Period.between(nacimiento, LocalDate.now()).getYears();
                edadField.setText(String.valueOf(edad));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Formato inválido. Use dd/MM/yyyy",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarPaciente() {
        String cedula = cedulaField.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Debe ingresar la cédula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> datos = obtenerDatosFormulario();
        // Persistir con gestor real para que VentanaDetallesOrdenes pueda leerlo inmediatamente
        try {
            Paciente p = new Paciente(
                    cedula,
                    datos.getOrDefault("nombres", ""),
                    datos.getOrDefault("apellidos", ""),
                    safeParseInt(datos.getOrDefault("edad", "0")),
                    datos.getOrDefault("direccion", ""),
                    datos.getOrDefault("telefono", ""),
                    datos.getOrDefault("correo", "")
            );
            GestorPacientes.guardarPaciente(p);
        } catch (Exception ignored) {}

        // También llamar a la API placeholder si existiera una implementación
        try { BaseDeDatosExcel.guardarPaciente(datos); } catch (Exception ignored) {}

        // Actualizar contador de código solo cuando NO es familiar y se guardó
        if (!familiarCheck.isSelected()) {
            confirmarCodigoAsignado(codigoField.getText().trim());
        }

        JOptionPane.showMessageDialog(frame, "Paciente registrado exitosamente.");
        ventanaDetalles.setCedulaPaciente(cedula);
        frame.dispose();
    }

    private void actualizarPaciente() {
        String cedula = cedulaField.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Debe ingresar la cédula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> datos = obtenerDatosFormulario();
        BaseDeDatosExcel.actualizarPaciente(cedula, datos);

        JOptionPane.showMessageDialog(frame, "Paciente actualizado correctamente.");
        ventanaDetalles.setCedulaPaciente(cedula);
        frame.dispose();
    }

    private void limpiarOCerrar() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "¿Desea limpiar los campos en lugar de cerrar?",
                "Deshacer",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cedulaField.setText("");
            codigoField.setText("");
            nombresField.setText("");
            apellidosField.setText("");
            direccionField.setText("");
            fechaNacField.setText("");
            telefonoField.setText("");
            correoField.setText("");
            edadField.setText("");
            sexoCombo.setSelectedIndex(0);
            nacionalidadCombo.setSelectedIndex(0);
            entidadCombo.setSelectedIndex(0);
            rnCheck.setSelected(false);
        } else {
            frame.dispose();
        }
    }

    // Constructor para precargar datos de un paciente
    public VentanaRegistroPacientes(Paciente paciente, VentanaDetallesOrdenes ventanaDetalles) {
        this((JFrame) null, ventanaDetalles);
        if (paciente != null) {
            cedulaField.setText(paciente.getCedula());
            nombresField.setText(paciente.getNombre());
            apellidosField.setText(paciente.getApellido());
            direccionField.setText(paciente.getDireccion());
            telefonoField.setText(paciente.getTelefono());
            edadField.setText(String.valueOf(paciente.getEdad()));
        }
    }

    private Map<String, String> obtenerDatosFormulario() {
        Map<String, String> datos = new HashMap<>();
        datos.put("cedula", cedulaField.getText().trim());
        datos.put("codigo", codigoField.getText().trim());
        datos.put("nacionalidad", (String) nacionalidadCombo.getSelectedItem());
        datos.put("nombres", nombresField.getText().trim());
        datos.put("apellidos", apellidosField.getText().trim());
        datos.put("direccion", direccionField.getText().trim());
        datos.put("fechaNac", fechaNacField.getText().trim());
        datos.put("telefono", telefonoField.getText().trim());
        datos.put("sexo", (String) sexoCombo.getSelectedItem());
        datos.put("rn", rnCheck.isSelected() ? "Sí" : "No");
        datos.put("correo", correoField.getText().trim());
        datos.put("entidad", (String) entidadCombo.getSelectedItem());
        datos.put("edad", edadField.getText().trim());
        return datos;
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    // ====== Código secuencial ======
    private static final String PAC_PROPS = System.getProperty("user.home")
            + File.separator + ".laboratorioapp" + File.separator + "pacientes.properties";

    // Proponer siguiente código sin modificar el contador persistente
    private String proponerSiguienteCodigo() {
        int width = Math.max(4, leerAnchoDesdeExcel());
        int ultimo = Math.max(leerUltimoCodigoPersistido(), leerMaxCodigoExcel());
        int next = ultimo + 1;
        return String.format("%0" + width + "d", next);
    }

    // Confirmar que el código actual fue asignado; persistir como último usado
    private void confirmarCodigoAsignado(String codigo) {
        if (codigo == null || codigo.isBlank()) return;
        int val = extraerNumero(codigo);
        int width = codigo.replaceAll("[^0-9]", "").length();
        if (width <= 0) width = 4;
        guardarUltimoCodigoPersistido(val, width);
    }

    private int leerUltimoCodigoPersistido() {
        try {
            File f = new File(PAC_PROPS);
            if (!f.exists()) return 0;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
            return Integer.parseInt(p.getProperty("lastCode", "0"));
        } catch (Exception e) { return 0; }
    }

    private void guardarUltimoCodigoPersistido(int last, int width) {
        try {
            File f = new File(PAC_PROPS);
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            Properties p = new Properties();
            if (f.exists()) try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); } catch (Exception ignored) {}
            p.setProperty("lastCode", String.valueOf(last));
            p.setProperty("width", String.valueOf(width));
            try (FileOutputStream fos = new FileOutputStream(f)) { p.store(fos, "Pacientes - correlativo de codigo"); }
        } catch (Exception ignored) {}
    }

    private int leerAnchoDesdeExcel() {
        try {
            Workbook workbook;
            File externo = new File("IPPUSNEG informacion..xlsx");
            if (externo.exists()) {
                try (FileInputStream fis = new FileInputStream(externo)) { workbook = WorkbookFactory.create(fis); }
            } else {
                try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("IPPUSNEG informacion..xlsx")) {
                    if (is == null) return leerAnchoPersistido();
                    workbook = WorkbookFactory.create(is);
                }
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            int colCodigo = -1;
            if (header != null) {
                for (int j = 0; j < 40; j++) {
                    if (header.getCell(j) == null) continue;
                    String h = normalizarHeader(header.getCell(j).toString());
                    if (h.contains("codigo")) { colCodigo = j; break; }
                }
            }
            if (colCodigo < 0) return leerAnchoPersistido();
            int width = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(colCodigo) == null) continue;
                String raw = row.getCell(colCodigo).toString();
                String digits = raw.replaceAll("[^0-9]", "");
                if (digits.length() > width) width = digits.length();
            }
            try { workbook.close(); } catch (Exception ignored) {}
            return Math.max(width, leerAnchoPersistido());
        } catch (Exception e) { return leerAnchoPersistido(); }
    }

    private int leerAnchoPersistido() {
        try {
            File f = new File(PAC_PROPS);
            if (!f.exists()) return 4;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
            return Integer.parseInt(p.getProperty("width", "4"));
        } catch (Exception e) { return 4; }
    }

    private int leerMaxCodigoExcel() {
        try {
            Workbook workbook;
            File externo = new File("IPPUSNEG informacion..xlsx");
            if (externo.exists()) {
                try (FileInputStream fis = new FileInputStream(externo)) { workbook = WorkbookFactory.create(fis); }
            } else {
                try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("IPPUSNEG informacion..xlsx")) {
                    if (is == null) return 0;
                    workbook = WorkbookFactory.create(is);
                }
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            int colCodigo = -1;
            if (header != null) {
                for (int j = 0; j < 40; j++) {
                    if (header.getCell(j) == null) continue;
                    String h = normalizarHeader(header.getCell(j).toString());
                    if (h.contains("codigo")) { colCodigo = j; break; }
                }
            }
            if (colCodigo < 0) { try { workbook.close(); } catch (Exception ignored) {} return 0; }
            int maxNum = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(colCodigo) == null) continue;
                String raw = row.getCell(colCodigo).toString();
                int val = extraerNumero(raw);
                if (val > maxNum) maxNum = val;
            }
            try { workbook.close(); } catch (Exception ignored) {}
            return maxNum;
        } catch (Exception e) { return 0; }
    }

    private static int extraerNumero(String s) {
        try {
            String digits = s == null ? "" : s.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return 0;
            return Integer.parseInt(digits);
        } catch (Exception e) { return 0; }
    }

    private static String normalizarHeader(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        t = t.toLowerCase().trim();
        t = t.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
        t = t.replace("n°", "numero");
        t = t.replaceAll("[^a-z0-9 ]", "");
        t = t.replace("  ", " ");
        return t;
    }

    private static int safeParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
}