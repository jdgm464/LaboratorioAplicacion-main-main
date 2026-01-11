package com.mycompany.laboratorioapp.pacientes;

import com.mycompany.laboratorioapp.ordenes.Orden;
import com.mycompany.laboratorioapp.ordenes.OrdenManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


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
    
    // Método para cargar pacientes desde la base de datos
    public void cargarPacientesDesdeBaseDatos() {
        try {
            modeloTabla.setRowCount(0);
            List<Paciente> pacientes = GestorPacientes.cargarPacientes();
            
            for (Paciente p : pacientes) {
                Object[] fila = new Object[16];
                fila[0] = ""; // Código (no disponible en BD)
                fila[1] = p.getNombre() != null ? p.getNombre() : "";
                fila[2] = p.getApellido() != null ? p.getApellido() : "";
                fila[3] = p.getCedula() != null ? p.getCedula() : "";
                fila[4] = p.getFechaNacimiento() != null ? 
                    p.getFechaNacimiento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                fila[5] = p.getEdad() > 0 ? String.valueOf(p.getEdad()) : "";
                fila[6] = p.getSexo() != null && !p.getSexo().trim().isEmpty() ? p.getSexo() : "";
                fila[7] = p.getDireccion() != null ? p.getDireccion() : "";
                fila[8] = p.getTelefono() != null ? p.getTelefono() : "";
                fila[9] = ""; // Teléfono2 (no disponible en BD)
                fila[10] = ""; // Sede (no disponible en BD)
                fila[11] = ""; // Categoría (no disponible en BD)
                fila[12] = ""; // Dedicación (no disponible en BD)
                fila[13] = ""; // Estatus (no disponible en BD)
                fila[14] = ""; // Fecha Ingreso (no disponible en BD)
                fila[15] = p.getCorreo() != null ? p.getCorreo() : "";
                
                // Aplicar filtros de búsqueda actuales
                if (!filaCumpleFiltros(fila)) continue;
                
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar pacientes desde la base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método para recargar pacientes (desde BD y Excel)
    public void recargarPacientes() {
        // Limpiar la tabla primero
        modeloTabla.setRowCount(0);
        // Primero intentar cargar desde Excel (si existe)
        try {
            cargarPacientesDesdeExcel(null, false);
        } catch (Exception e) {
            // Si falla la carga desde Excel, continuar para cargar desde BD
            System.err.println("No se pudo cargar desde Excel: " + e.getMessage());
        }
        // Luego agregar pacientes de la base de datos que no estén ya en la tabla
        agregarPacientesDesdeBaseDatos();
    }
    
    // Agregar pacientes de la BD que no estén ya en la tabla, o actualizar los que tienen datos faltantes
    private void agregarPacientesDesdeBaseDatos() {
        try {
            List<Paciente> pacientesBD = GestorPacientes.cargarPacientes();
            
            for (Paciente p : pacientesBD) {
                // Verificar si el paciente ya está en la tabla (por cédula)
                String cedulaBD = normalizarCedula(p.getCedula());
                int filaExistente = -1;
                boolean necesitaActualizacion = false;
                
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    Object cedulaTabla = modeloTabla.getValueAt(i, 3);
                    if (cedulaTabla != null) {
                        String cedulaTablaNorm = normalizarCedula(cedulaTabla.toString());
                        if (cedulaBD.equals(cedulaTablaNorm)) {
                            filaExistente = i;
                            // Verificar si necesita actualización (nombres o apellidos vacíos)
                            Object nombreActual = modeloTabla.getValueAt(i, 1);
                            Object apellidoActual = modeloTabla.getValueAt(i, 2);
                            String nombreStr = nombreActual != null ? nombreActual.toString().trim() : "";
                            String apellidoStr = apellidoActual != null ? apellidoActual.toString().trim() : "";
                            
                            if (nombreStr.isEmpty() || apellidoStr.isEmpty()) {
                                necesitaActualizacion = true;
                            }
                            break;
                        }
                    }
                }
                
                // Si existe y necesita actualización, actualizar la fila
                if (filaExistente >= 0 && necesitaActualizacion) {
                    // Actualizar solo los campos que están vacíos o faltantes
                    Object nombreActual = modeloTabla.getValueAt(filaExistente, 1);
                    Object apellidoActual = modeloTabla.getValueAt(filaExistente, 2);
                    Object direccionActual = modeloTabla.getValueAt(filaExistente, 7);
                    Object telefonoActual = modeloTabla.getValueAt(filaExistente, 8);
                    Object correoActual = modeloTabla.getValueAt(filaExistente, 15);
                    Object edadActual = modeloTabla.getValueAt(filaExistente, 5);
                    Object sexoActual = modeloTabla.getValueAt(filaExistente, 6);
                    
                    // Actualizar nombre si está vacío
                    if (nombreActual == null || nombreActual.toString().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getNombre() != null ? p.getNombre() : "", filaExistente, 1);
                    }
                    
                    // Actualizar apellido si está vacío
                    if (apellidoActual == null || apellidoActual.toString().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getApellido() != null ? p.getApellido() : "", filaExistente, 2);
                    }
                    
                    // Actualizar otros campos si están vacíos y hay datos en BD
                    if ((direccionActual == null || direccionActual.toString().trim().isEmpty()) && 
                        p.getDireccion() != null && !p.getDireccion().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getDireccion(), filaExistente, 7);
                    }
                    
                    if ((telefonoActual == null || telefonoActual.toString().trim().isEmpty()) && 
                        p.getTelefono() != null && !p.getTelefono().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getTelefono(), filaExistente, 8);
                    }
                    
                    if ((correoActual == null || correoActual.toString().trim().isEmpty()) && 
                        p.getCorreo() != null && !p.getCorreo().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getCorreo(), filaExistente, 15);
                    }
                    
                    if ((edadActual == null || edadActual.toString().trim().isEmpty()) && 
                        p.getEdad() > 0) {
                        modeloTabla.setValueAt(String.valueOf(p.getEdad()), filaExistente, 5);
                    }
                    
                    if ((sexoActual == null || sexoActual.toString().trim().isEmpty()) && 
                        p.getSexo() != null && !p.getSexo().trim().isEmpty()) {
                        modeloTabla.setValueAt(p.getSexo(), filaExistente, 6);
                    }
                }
                // Si no existe, agregarlo
                else if (filaExistente < 0) {
                    Object[] fila = new Object[16];
                    fila[0] = ""; // Código
                    fila[1] = p.getNombre() != null ? p.getNombre() : "";
                    fila[2] = p.getApellido() != null ? p.getApellido() : "";
                    fila[3] = p.getCedula() != null ? p.getCedula() : "";
                    fila[4] = p.getFechaNacimiento() != null ? 
                        p.getFechaNacimiento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                    fila[5] = p.getEdad() > 0 ? String.valueOf(p.getEdad()) : "";
                    fila[6] = p.getSexo() != null && !p.getSexo().trim().isEmpty() ? p.getSexo() : "";
                    fila[7] = p.getDireccion() != null ? p.getDireccion() : "";
                    fila[8] = p.getTelefono() != null ? p.getTelefono() : "";
                    fila[9] = ""; // Teléfono2
                    fila[10] = ""; // Sede
                    fila[11] = ""; // Categoría
                    fila[12] = ""; // Dedicación
                    fila[13] = ""; // Estatus
                    fila[14] = ""; // Fecha Ingreso
                    fila[15] = p.getCorreo() != null ? p.getCorreo() : "";
                    
                    // Aplicar filtros de búsqueda actuales
                    if (!filaCumpleFiltros(fila)) continue;
                    
                    modeloTabla.addRow(fila);
                }
            }
        } catch (Exception e) {
            // Silenciar errores para no interrumpir la carga desde Excel
            System.err.println("Error al agregar pacientes desde BD: " + e.getMessage());
        }
    }
    private final JFrame frame;
    private final JTable tablaPacientes;
    private final DefaultTableModel modeloTabla;
    private java.io.File archivoExcelSeleccionado;
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
        JButton btnCargarExcel = new JButton("Cargar Excel");

        btnSalir.addActionListener(e -> frame.dispose());
        btnEliminar.addActionListener(e -> eliminarFilaSeleccionada());
        btnRecargar.addActionListener(e -> { recargarPacientes(); });
        btnCargarExcel.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Seleccione archivo Excel de pacientes");
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                archivoExcelSeleccionado = chooser.getSelectedFile();
                modeloTabla.setRowCount(0);
                cargarPacientesDesdeExcel(archivoExcelSeleccionado, false);
            }
        });
        btnNuevo.addActionListener(e -> {
            VentanaRegistroPacientes ventanaRegistro = new VentanaRegistroPacientes((javax.swing.JFrame) null, null);
            ventanaRegistro.setVentanaPacientes(this);
            ventanaRegistro.mostrar();
        });
        btnModificar.addActionListener(e -> {
            int sel = tablaPacientes.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(frame, "Seleccione un paciente."); return; }
            String ced = String.valueOf(modeloTabla.getValueAt(sel, 3));
            Paciente p = GestorPacientes.buscarPorCedula(ced);
            if (p == null) { 
                JOptionPane.showMessageDialog(frame, "Paciente no encontrado en la base de datos."); 
                return; 
            }
            VentanaRegistroPacientes ventanaRegistro = new VentanaRegistroPacientes(p, null);
            ventanaRegistro.setVentanaPacientes(this);
            ventanaRegistro.mostrar();
        });
        

        panelBotones.add(btnNuevo);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCargarExcel);
        panelBotones.add(btnRecargar);
        panelBotones.add(btnSalir);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // Listeners para conteo de visitas en rango
        btnContar.addActionListener(e -> contarVisitasEnRango());
        desdeSpinner.addChangeListener(e -> contarVisitasEnRango());
        hastaSpinner.addChangeListener(e -> contarVisitasEnRango());
        txtCedula.addActionListener(e -> { recargarConFiltros(); });
        txtCodigoWeb.addActionListener(e -> { recargarConFiltros(); });
        txtNombres.addActionListener(e -> { recargarConFiltros(); });
        txtApellidos.addActionListener(e -> { recargarConFiltros(); });

        // ------------------ CARGAR EXCEL Y BASE DE DATOS ------------------
        recargarPacientes();

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

    private void recargarConFiltros() {
        try {
            modeloTabla.setRowCount(0);
            cargarPacientesDesdeExcel(archivoExcelSeleccionado, false);
        } catch (Exception ignored) {}
    }

    private void cargarPacientesDesdeExcel() {
        cargarPacientesDesdeExcel(null, false);
    }

    private void cargarPacientesDesdeExcel(java.io.File archivo, boolean incluirOcultos) {
        try {
            Workbook workbook;
            java.io.File externo = archivo != null ? archivo : new java.io.File("IPPUSNEG informacion..xlsx");
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

            Map<String, Integer> restantesOcultos = incluirOcultos ? new HashMap<>() : new HashMap<>(ocultosPorClave);
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

                // Aplicar filtros de búsqueda actuales
                if (!filaCumpleFiltros(fila)) continue;
                // Aplicar ocultamientos persistentes por clave (si corresponde)
                if (incluirOcultos) {
                    modeloTabla.addRow(fila);
                } else {
                    String clave = construirClaveFila(fila);
                    int rest = restantesOcultos.getOrDefault(clave, 0);
                    if (rest > 0) {
                        restantesOcultos.put(clave, rest - 1);
                    } else {
                        modeloTabla.addRow(fila);
                    }
                }
            }
            // Nota: no persistimos los consumos; el ocultamiento permanece
            // hasta que el usuario agregue nuevas filas iguales (que sí se mostrarán)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error al cargar pacientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Verifica si la fila coincide con los filtros del cuadro (cedula, nombres, apellidos, codigo web)
    private boolean filaCumpleFiltros(Object[] fila) {
        String fCedula = normalizarCedula(valor(fila, 3));
        String fNombres = valor(fila, 1).toLowerCase();
        String fApellidos = valor(fila, 2).toLowerCase();
        String fCodigo = valor(fila, 0).toLowerCase();

        String qCedula = normalizarCedula(txtCedula != null && txtCedula.getText() != null ? txtCedula.getText().trim() : "");
        String qNombres = txtNombres != null && txtNombres.getText() != null ? txtNombres.getText().trim().toLowerCase() : "";
        String qApellidos = txtApellidos != null && txtApellidos.getText() != null ? txtApellidos.getText().trim().toLowerCase() : "";
        String qCodigo = txtCodigoWeb != null && txtCodigoWeb.getText() != null ? txtCodigoWeb.getText().trim().toLowerCase() : "";

        if (!qCedula.isEmpty() && !fCedula.contains(qCedula)) return false;
        if (!qNombres.isEmpty() && !fNombres.contains(qNombres)) return false;
        if (!qApellidos.isEmpty() && !fApellidos.contains(qApellidos)) return false;
        if (!qCodigo.isEmpty() && !fCodigo.contains(qCodigo)) return false;
        return true;
    }

    private static String valor(Object[] fila, int idx) {
        Object v = (idx >= 0 && idx < fila.length) ? fila[idx] : null;
        return v == null ? "" : v.toString();
    }

    // Permite que otra ventana precargue la búsqueda por cédula y compute visitas
    public void presetBusquedaPorCedula(String cedula) {
        if (txtCedula != null) {
            txtCedula.setText(cedula != null ? cedula : "");
        }
        contarVisitasEnRango();
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
        if (raw.isEmpty()) return "";
        String pref = (raw.startsWith("V") || raw.startsWith("E") || raw.startsWith("A")) ? raw.substring(0,1) : "V";
        String resto = raw.length() > pref.length() ? raw.substring(pref.length()) : "";
        resto = resto.replaceAll("[^0-9]", "");
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

