package com.mycompany.laboratorioapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OrdenManager {
    private static List<Orden> ordenes = new ArrayList<>();
    private static final String FILE = System.getProperty("user.home")
            + java.io.File.separator + ".laboratorioapp" + java.io.File.separator + "ordenes.properties";
    private static boolean cargado = false;
    private static final String EXCEL_FILE = "IPPUSNEG informacion..xlsx";
    private static final String SHEET_ORDENES = "Ordenes";

    static {
        cargar();
    }

    public static void agregarOrden(Orden orden) {
        ordenes.add(orden);
        guardar();
    }

    public static List<Orden> getOrdenes() {
        if (!cargado) cargar();
        return ordenes;
    }

    public static Orden buscarPorNumero(String numeroOrden) {
        if (!cargado) cargar();
        if (numeroOrden == null) return null;
        for (Orden o : ordenes) {
            if (numeroOrden.equalsIgnoreCase(o.getNumeroOrden())) return o;
        }
        return null;
    }

    private static void cargar() {
        try {
            // 1) Intentar cargar desde Excel (si existe y tiene hoja Ordenes)
            File externo = new File(EXCEL_FILE);
            if (externo.exists()) {
                try (FileInputStream fis = new FileInputStream(externo)) {
                    try (Workbook wb = new XSSFWorkbook(fis)) {
                        Sheet sh = wb.getSheet(SHEET_ORDENES);
                        if (sh == null) sh = wb.getSheet("ordenes");
                        if (sh == null) sh = wb.getSheet("ORDENES");
                        if (sh != null) {
                            ordenes.clear();
                            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                                Row r = sh.getRow(i);
                                if (r == null) continue;
                                Orden o = new Orden(
                                        getString(r,0), getString(r,1), getString(r,2), getString(r,3),
                                        getString(r,4), getString(r,5), getString(r,6), getString(r,7),
                                        getString(r,8), getString(r,9), getString(r,10), getString(r,11),
                                        getString(r,12), getString(r,13), getString(r,14),
                                        parseExamenes(getString(r,15)), parseDoubleSafe(getString(r,16))
                                );
                                // Estatus en columna 17 si existe
                                String est = getString(r,17);
                                if (est != null && !est.isBlank()) o.setEstatus(est);
                                ordenes.add(o);
                            }
                            cargado = true;
                            return;
                        }
                    }
                }
            }

            // 2) Fallback a archivo properties
            File f = new File(FILE);
            if (!f.getParentFile().exists()) {
                // Crear directorio en el home si no existe
                f.getParentFile().mkdirs();
            }
            if (!f.exists()) return;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
            int n = Integer.parseInt(p.getProperty("count", "0"));
            ordenes.clear();
            for (int i = 0; i < n; i++) {
                String prefix = "o." + i + ".";
                Orden o = new Orden(
                    p.getProperty(prefix + "orden", ""),
                    p.getProperty(prefix + "factura", ""),
                    p.getProperty(prefix + "control", ""),
                    p.getProperty(prefix + "lote", ""),
                    p.getProperty(prefix + "fecha", ""),
                    p.getProperty(prefix + "hora", ""),
                    p.getProperty(prefix + "codpac", ""),
                    p.getProperty(prefix + "cedula", ""),
                    p.getProperty(prefix + "nombres", ""),
                    p.getProperty(prefix + "apellidos", ""),
                    p.getProperty(prefix + "direccion", ""),
                    p.getProperty(prefix + "telefono", ""),
                    p.getProperty(prefix + "correo", ""),
                    p.getProperty(prefix + "codemp", ""),
                    p.getProperty(prefix + "empresa", ""),
                    parseExamenes(p.getProperty(prefix + "examenes", "")),
                    parseDoubleSafe(p.getProperty(prefix + "total", "0"))
                );
                String est = p.getProperty(prefix + "estatus", "Activo");
                if (est != null && !est.isBlank()) o.setEstatus(est);
                ordenes.add(o);
            }
            cargado = true;
        } catch (Exception ignored) { cargado = true; }
    }

    private static void guardar() {
        try {
            // 1) Guardar/apendizar en Excel (hoja Ordenes)
            File externo = new File(EXCEL_FILE);
            Workbook wb;
            Sheet sh;
            if (externo.exists()) {
                try (FileInputStream fis = new FileInputStream(externo)) { wb = new XSSFWorkbook(fis); }
                sh = wb.getSheet(SHEET_ORDENES);
                if (sh == null) sh = wb.createSheet(SHEET_ORDENES);
            } else {
                wb = new XSSFWorkbook();
                sh = wb.createSheet(SHEET_ORDENES);
            }

            // Limpiar y reescribir todas las órdenes actuales
            // Cabeceras
            Row h = sh.getRow(0);
            if (h == null) h = sh.createRow(0);
            String[] cols = {"Orden N°","Factura N°","Control N°","Lote N°","Fecha Reg","Hora Reg","Cod Paciente","Cédula","Nombres","Apellidos","Dirección","Teléfono","Correo","Cod Empresa","Empresa","Exámenes","Total","Estatus"};
            for (int c = 0; c < cols.length; c++) {
                if (h.getCell(c) == null) h.createCell(c).setCellValue(cols[c]);
                else h.getCell(c).setCellValue(cols[c]);
            }
            // Borrar filas viejas
            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                Row r = sh.getRow(i);
                if (r != null) sh.removeRow(r);
            }
            // Escribir filas
            for (int i = 0; i < ordenes.size(); i++) {
                Orden o = ordenes.get(i);
                Row r = sh.getRow(i + 1);
                if (r == null) r = sh.createRow(i + 1);
                setCell(r,0,o.getNumeroOrden());
                setCell(r,1,o.getNumeroFactura());
                setCell(r,2,o.getNumeroControl());
                setCell(r,3,o.getNumeroLote());
                setCell(r,4,o.getFechaRegistro());
                setCell(r,5,o.getHoraRegistro());
                setCell(r,6,o.getCodigoPaciente());
                setCell(r,7,o.getCedula());
                setCell(r,8,o.getNombres());
                setCell(r,9,o.getApellidos());
                setCell(r,10,o.getDireccion());
                setCell(r,11,o.getTelefono());
                setCell(r,12,o.getCorreo());
                setCell(r,13,o.getCodigoEmpresa());
                setCell(r,14,o.getEmpresa());
                setCell(r,15,joinExamenes(o.getExamenes()));
                setCell(r,16,String.valueOf(o.getTotal()));
                setCell(r,17,o.getEstatus());
            }
            try (FileOutputStream fosExcel = new FileOutputStream(externo)) { wb.write(fosExcel); }
            wb.close();

            // 2) Guardar también en properties como respaldo
            Properties p = new Properties();
            p.setProperty("count", String.valueOf(ordenes.size()));
            for (int i = 0; i < ordenes.size(); i++) {
                Orden o = ordenes.get(i);
                String prefix = "o." + i + ".";
                p.setProperty(prefix + "orden", o.getNumeroOrden());
                p.setProperty(prefix + "factura", o.getNumeroFactura());
                p.setProperty(prefix + "control", o.getNumeroControl());
                p.setProperty(prefix + "lote", o.getNumeroLote());
                p.setProperty(prefix + "fecha", o.getFechaRegistro());
                p.setProperty(prefix + "hora", o.getHoraRegistro());
                p.setProperty(prefix + "codpac", o.getCodigoPaciente());
                p.setProperty(prefix + "cedula", o.getCedula());
                p.setProperty(prefix + "nombres", o.getNombres());
                p.setProperty(prefix + "apellidos", o.getApellidos());
                p.setProperty(prefix + "direccion", o.getDireccion());
                p.setProperty(prefix + "telefono", o.getTelefono());
                p.setProperty(prefix + "correo", o.getCorreo());
                p.setProperty(prefix + "codemp", o.getCodigoEmpresa());
                p.setProperty(prefix + "empresa", o.getEmpresa());
                p.setProperty(prefix + "examenes", joinExamenes(o.getExamenes()));
                p.setProperty(prefix + "total", String.valueOf(o.getTotal()));
                p.setProperty(prefix + "estatus", o.getEstatus());
            }
            File f = new File(FILE);
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(f)) { p.store(fos, "Ordenes persistidas"); }
        } catch (Exception ignored) {}
    }

    private static String getString(Row r, int c) {
        if (r.getCell(c) == null) return "";
        return switch (r.getCell(c).getCellType()) {
            case STRING -> r.getCell(c).getStringCellValue();
            case NUMERIC -> String.valueOf((long) r.getCell(c).getNumericCellValue());
            default -> r.getCell(c).toString();
        };
    }

    private static void setCell(Row r, int c, String v) {
        if (r.getCell(c) == null) r.createCell(c).setCellValue(v == null ? "" : v);
        else r.getCell(c).setCellValue(v == null ? "" : v);
    }

    private static List<String> parseExamenes(String data) {
        if (data == null || data.isBlank()) return java.util.List.of();
        String[] parts = data.split(";\\s*");
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String p : parts) {
            if (!p.isBlank()) out.add(p.trim());
        }
        return out;
    }

    private static String joinExamenes(List<String> examenes) {
        if (examenes == null || examenes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < examenes.size(); i++) {
            if (i > 0) sb.append("; ");
            sb.append(examenes.get(i));
        }
        return sb.toString();
    }

    private static double parseDoubleSafe(String v) {
        try { return Double.parseDouble(v); } catch (Exception e) { return 0.0; }
    }

    // ====== Actualizar estatus y persistir ======
    public static void actualizarEstatus(String numeroOrden, String nuevoEstatus) {
        if (numeroOrden == null) return;
        for (Orden o : getOrdenes()) {
            if (numeroOrden.equalsIgnoreCase(o.getNumeroOrden())) {
                o.setEstatus(nuevoEstatus);
                guardar();
                break;
            }
        }
    }
}