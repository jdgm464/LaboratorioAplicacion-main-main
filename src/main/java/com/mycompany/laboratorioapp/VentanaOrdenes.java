package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;

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
                "Modificar", "Anular", "Buscar", "Imprimir", "Salir"};

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
            case "Nuevo", "Modificar", "Buscar" -> {
                VentanaDetallesOrdenes detalles = new VentanaDetallesOrdenes(this);
                detalles.mostrar();
            }
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

    // 🔹 Método para refrescar tabla con las órdenes de OrdenManager
    public void refrescarTabla() {
        modeloTabla.setRowCount(0); // limpiar
        for (Orden o : OrdenManager.getOrdenes()) {
            Object[] row = o.toRow();
            // Si la orden no tiene estatus, lo agregamos como "Activo"
            if (row.length == 15) {
                Object[] rowConEstatus = Arrays.copyOf(row, 16);
                rowConEstatus[15] = "Activo";
                modeloTabla.addRow(rowConEstatus);
            } else {
                modeloTabla.addRow(row);
            }
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
}