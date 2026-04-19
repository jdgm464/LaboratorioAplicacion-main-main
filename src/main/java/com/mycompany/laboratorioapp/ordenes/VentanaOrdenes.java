package com.mycompany.laboratorioapp.ordenes;

import com.mycompany.laboratorioapp.pacientes.PacienteExcelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;


public class VentanaOrdenes {
    // Permite agregar una nueva orden a la tabla
    public void agregarOrden(Object[] datos) {
        // Ahora esperamos 15 datos (sin estatus), o 16 (con estatus)
        Object[] datosConEstatus;
        if (datos.length == 15) {
            datosConEstatus = Arrays.copyOf(datos, 16);
            datosConEstatus[15] = "Activo";
        } else if (datos.length == 16) {
            datosConEstatus = datos;
        } else {
            // Si el array tiene menos columnas, lo rellenamos
            datosConEstatus = new Object[16];
            System.arraycopy(datos, 0, datosConEstatus, 0, Math.min(datos.length, 15));
            datosConEstatus[15] = "Activo";
        }
        modeloTabla.addRow(datosConEstatus);
    }

    // Cambia el estatus de una orden (por índice de fila)
    public void cambiarEstatusOrden(int fila, String nuevoEstatus) {
        if (fila >= 0 && fila < modeloTabla.getRowCount()) {
            modeloTabla.setValueAt(nuevoEstatus, fila, 15); // columna 15 es Estatus
        }
    }

    private JFrame ventana;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    public VentanaOrdenes() {
        configurarVentana();
    }

    private void configurarVentana() {
        ventana = new JFrame("Registro de Facturación por Orden");
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setSize(1000, 600);
        ventana.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Registro de Facturación por Orden", SwingConstants.LEFT);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ventana.add(titulo, BorderLayout.NORTH);

        // --- Tabla de órdenes ---
    String[] columnas = {"Orden N°", "Factura N°", "Control N°", "Lote N°",
        "Fecha Reg", "Hora Reg", "Cod Paciente", "Cédula",
        "Nombres", "Apellidos", "Dirección", "Teléfono", "Correo",
        "Cod Empresa", "Empresa", "Estatus"};
    modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(25);
        tablaPacientes.setFont(new Font("Arial", Font.PLAIN, 13));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaPacientes.setRowSorter(sorter);
        sorter.toggleSortOrder(0);

        JScrollPane scrollTabla = new JScrollPane(tablaPacientes);
        ventana.add(scrollTabla, BorderLayout.CENTER);

        // --- Botones ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        botonesPanel.setBackground(Color.WHITE);

        String[] botones = {"Constancia", "O/Bioanalista", "Nuevo", "Cargar",
                "Modificar", "Eliminar", "Anular", "Buscar", "Imprimir", "Salir"};

        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setBackground(new Color(0, 191, 255));
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 12));
            boton.setFocusPainted(false);
            boton.setPreferredSize(new Dimension(100, 35));

            boton.addActionListener(e -> manejarAccion(texto));
            botonesPanel.add(boton);
        }

        ventana.add(botonesPanel, BorderLayout.SOUTH);
    }

    private void manejarAccion(String accion) {
        switch (accion) {
            case "Nuevo" -> {
                VentanaDetallesOrdenes detalles = new VentanaDetallesOrdenes(this);
                detalles.mostrar();
            }
            case "Cargar" -> {
                // Recargar órdenes desde la base de datos
                refrescarTabla();
                JOptionPane.showMessageDialog(ventana, 
                    "Órdenes recargadas desde la base de datos.",
                    "Cargar", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            case "Modificar" -> modificarSeleccionada();
            case "Eliminar" -> eliminarSeleccionada();
            case "Anular" -> {
                anularSeleccionada();
            }
            case "Salir" -> ventana.dispose();
            default -> JOptionPane.showMessageDialog(ventana, "Acción: " + accion);
        }
    }

    public void mostrar() {
        // Asegurar que al abrir la ventana se muestren las órdenes acumuladas
        refrescarTabla();
        ventana.setVisible(true);
    }

    // 🔹 Método para refrescar tabla con las órdenes de OrdenManager (desde BD)
    public void refrescarTabla() {
        modeloTabla.setRowCount(0); // limpiar
        try {
            // Obtener todas las órdenes desde la base de datos
            List<Orden> ordenes = OrdenManager.getOrdenes();
            for (Orden o : ordenes) {
                java.util.Map<String, String> datosExcel = PacienteExcelHelper.buscarPorCedula(o.getCedula());
                Object[] row = new Object[] {
                    o.getNumeroOrden(),
                    o.getNumeroFactura(),
                    o.getNumeroControl(),
                    o.getNumeroLote(),
                    o.getFechaRegistro(),
                    o.getHoraRegistro(),
                    valorNoVacio(o.getCodigoPaciente(), datosExcel.get("codigo")),
                    o.getCedula(),
                    o.getNombres(),
                    o.getApellidos(),
                    o.getDireccion(),
                    o.getTelefono(),
                    o.getCorreo(),
                    o.getCodigoEmpresa(),
                    limpiarEmpresa(o.getEmpresa()),
                    o.getEstatus()
                };
                // Si la orden no tiene estatus, lo agregamos como "Activo"
                if (row.length == 15) {
                    Object[] rowConEstatus = Arrays.copyOf(row, 16);
                    rowConEstatus[15] = o.getEstatus() != null && !o.getEstatus().isEmpty() ? o.getEstatus() : "Activo";
                    modeloTabla.addRow(rowConEstatus);
                } else {
                    // Asegurar que el estatus esté presente
                    if (row.length < 16) {
                        Object[] rowConEstatus = Arrays.copyOf(row, 16);
                        rowConEstatus[15] = o.getEstatus() != null && !o.getEstatus().isEmpty() ? o.getEstatus() : "Activo";
                        modeloTabla.addRow(rowConEstatus);
                    } else {
                        modeloTabla.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al refrescar tabla de órdenes: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, 
                "Error al cargar las órdenes desde la base de datos: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Anular desde otras ventanas o desde el propio botón
    public void anularSeleccionada() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila >= 0) {
            // Cambiar en UI
            cambiarEstatusOrden(fila, "Anulado");
            // Persistir en el manager
            Object numeroOrden = modeloTabla.getValueAt(tablaPacientes.convertRowIndexToModel(fila), 0);
            if (numeroOrden != null) {
                OrdenManager.actualizarEstatus(numeroOrden.toString(), "Anulado");
            }
        } else {
            JOptionPane.showMessageDialog(ventana, "Seleccione una orden para anular.");
        }
    }

    // 🔹 Eliminar fisicamente la orden
    private void eliminarSeleccionada() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(ventana, "Seleccione una orden para eliminar."); return; }
        int m = tablaPacientes.convertRowIndexToModel(fila);
        Object numeroOrden = modeloTabla.getValueAt(m, 0);
        if (numeroOrden == null) { JOptionPane.showMessageDialog(ventana, "Orden inválida."); return; }
        int conf = JOptionPane.showConfirmDialog(ventana, "¿Eliminar la orden " + numeroOrden + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        // Quitar de UI y de manager
        modeloTabla.removeRow(m);
        OrdenManager.eliminarOrden(numeroOrden.toString());
    }

    // 🔹 Abrir detalle con datos precargados para modificar
    private void modificarSeleccionada() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(ventana, "Seleccione una orden para modificar."); return; }
        int m = tablaPacientes.convertRowIndexToModel(fila);
        String nro = String.valueOf(modeloTabla.getValueAt(m, 0));
        Orden o = OrdenManager.buscarPorNumero(nro);
        if (o == null) { 
            JOptionPane.showMessageDialog(ventana, "No se encontró la orden seleccionada."); 
            return; 
        }
        VentanaDetallesOrdenes detalles = new VentanaDetallesOrdenes(this);
        detalles.mostrar();
        // Cargar la orden usando el método público
        detalles.cargarOrden(o);
    }

    private String valorNoVacio(String principal, String respaldo) {
        if (principal != null && !principal.isBlank()) {
            return principal;
        }
        return respaldo != null ? respaldo : "";
    }

    private String limpiarEmpresa(String empresa) {
        if (empresa == null || empresa.isBlank() || "-- Seleccionar --".equalsIgnoreCase(empresa)) {
            return "";
        }
        return empresa;
    }
}

