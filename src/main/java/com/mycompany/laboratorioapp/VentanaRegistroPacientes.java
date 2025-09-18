package com.mycompany.laboratorioapp;

// import org.apache.poi.ss.usermodel.*; // No se usa
// import org.apache.poi.xssf.usermodel.XSSFWorkbook; // No se usa

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

        // ----- Fila 1: Cedula + Nacionalidad + Codigo -----
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
        BaseDeDatosExcel.guardarPaciente(datos);

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
}