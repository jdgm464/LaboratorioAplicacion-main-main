package com.mycompany.laboratorioapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
 

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class GestorPacientes {
    private static final String ARCHIVO = "IPPUSNEG informacion..xlsx";

    // ✅ Normalizar cédula para evitar errores de búsqueda
    private static String normalizarCedula(String cedula) {
        if (cedula == null) return "";
        String raw = cedula.trim().toUpperCase();
        String prefijo;
        String resto;
        if (raw.startsWith("V") || raw.startsWith("E")) {
            prefijo = raw.substring(0, 1);
            resto = raw.substring(1);
        } else {
            prefijo = "V";
            resto = raw;
        }
        // Eliminar separadores: guiones, puntos, espacios, etc. y dejar solo dígitos
        resto = resto.replaceAll("[^0-9]", "");
        return prefijo + resto;
    }

    // ✅ Cargar todos los pacientes desde el Excel
    public static List<Paciente> cargarPacientes() {
        List<Paciente> lista = new ArrayList<>();

        try {
            Workbook workbook = abrirWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> header = construirMapaCabeceras(sheet);
            int colCedula = header.getOrDefault("cedula", 3);
            int colNombre = header.getOrDefault("nombres", 1);
            int colApellido = header.getOrDefault("apellidos", 2);
            int colEdad = header.getOrDefault("edad", 5);
            int colFechaNac = header.getOrDefault("fecha nacimiento", header.getOrDefault("fecha nac", 4));
            int colDireccion = header.getOrDefault("direccion", 7);
            int colTelefono = header.getOrDefault("telefono1", 8);
            int colCorreo = header.getOrDefault("email", header.getOrDefault("correo", 15));
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // saltar encabezado
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int edad = calcularEdadDesdeCelda(row.getCell(colFechaNac), getCellValue(row.getCell(colEdad)));
                Paciente p = new Paciente(
                        normalizarCedula(getCellValue(row.getCell(colCedula))),
                        getCellValue(row.getCell(colNombre)),
                        getCellValue(row.getCell(colApellido)),
                        edad,
                        getCellValue(row.getCell(colDireccion)),
                        getCellValue(row.getCell(colTelefono)),
                        getCellValue(row.getCell(colCorreo))
                );
                lista.add(p);
            }
            if (workbook != null) {
                workbook.close();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar pacientes: " + e.getMessage());
        }

        return lista;
    }

    // ✅ Buscar paciente por cédula (normalizando)
   public static Paciente buscarPorCedula(String cedula) {
       if (cedula == null) return null;
       String objetivo = normalizarCedula(cedula);

       try {
           Workbook workbook = abrirWorkbook();
           Sheet sheet = workbook.getSheetAt(0);
           Map<String, Integer> header = construirMapaCabeceras(sheet);
           int colCedula = header.getOrDefault("cedula", 3);
           for (int i = 1; i <= sheet.getLastRowNum(); i++) {
               Row row = sheet.getRow(i);
               if (row == null) continue;
               String ced = normalizarCedula(getCellValue(row.getCell(colCedula)));
               if (ced.equals(objetivo)) {
                   int colFechaNac = header.getOrDefault("fecha nacimiento", header.getOrDefault("fecha nac", 4));
                   int colEdad = header.getOrDefault("edad", 5);
                   int edad = calcularEdadDesdeCelda(row.getCell(colFechaNac), getCellValue(row.getCell(colEdad)));
                   Paciente p = new Paciente(
                           ced,
                           getCellValue(row.getCell(header.getOrDefault("nombres", 1))),
                           getCellValue(row.getCell(header.getOrDefault("apellidos", 2))),
                           edad,
                           getCellValue(row.getCell(header.getOrDefault("direccion", 7))),
                           getCellValue(row.getCell(header.getOrDefault("telefono1", 8))),
                           getCellValue(row.getCell(header.getOrDefault("email", header.getOrDefault("correo", 15))))
                   );
                   workbook.close();
                   return p;
               }
           }
           workbook.close();
       } catch (Exception e) {
           System.err.println("Error al buscar paciente: " + e.getMessage());
       }
       return null;
   }

    // ==== Helpers de apertura y cabeceras ====
    private static Workbook abrirWorkbook() throws Exception {
        File externo = new File(ARCHIVO);
        if (externo.exists()) {
            try (FileInputStream fis = new FileInputStream(externo)) {
                return new XSSFWorkbook(fis);
            }
        } else {
            try (InputStream is = GestorPacientes.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
                if (is == null) throw new RuntimeException("No se encontró el archivo " + ARCHIVO + " en resources");
                return new XSSFWorkbook(is);
            }
        }
    }

    private static Map<String, Integer> construirMapaCabeceras(Sheet sheet) {
        Map<String, Integer> map = new HashMap<>();
        Row header = sheet.getRow(0);
        if (header == null) return map;
        for (int j = 0; j < 40; j++) {
            Cell c = header.getCell(j);
            if (c == null) continue;
            String key = normalizarHeader(c.toString());
            map.put(key, j);
        }
        // Aliases comunes
        if (!map.containsKey("cedula") && map.containsKey("cédula")) map.put("cedula", map.get("cédula"));
        if (!map.containsKey("email") && map.containsKey("correo")) map.put("email", map.get("correo"));
        if (!map.containsKey("telefono1") && map.containsKey("telefono")) map.put("telefono1", map.get("telefono"));
        return map;
    }

    private static String normalizarHeader(String s) {
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

    // ✅ Guardar un nuevo paciente
    public static void guardarPaciente(Paciente paciente) {
        try {
            File file = new File(ARCHIVO);
            Workbook workbook;
            Sheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
                sheet = workbook.getSheetAt(0);
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Pacientes");

                // encabezados
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Cedula");
                header.createCell(1).setCellValue("Nombre");
                header.createCell(2).setCellValue("Apellido");
                header.createCell(3).setCellValue("Edad");
                header.createCell(4).setCellValue("Direccion");
                header.createCell(5).setCellValue("Telefono");
                header.createCell(6).setCellValue("Correo");
            }

            int ultimaFila = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(ultimaFila);
            row.createCell(0).setCellValue(normalizarCedula(paciente.getCedula()));
            row.createCell(1).setCellValue(paciente.getNombre());
            row.createCell(2).setCellValue(paciente.getApellido());
            row.createCell(3).setCellValue(paciente.getEdad());
            row.createCell(4).setCellValue(paciente.getDireccion());
            row.createCell(5).setCellValue(paciente.getTelefono());
            row.createCell(6).setCellValue(paciente.getCorreo());

            try (FileOutputStream fos = new FileOutputStream(ARCHIVO)) {
                workbook.write(fos);
            }
            workbook.close();

        } catch (Exception e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
        }
    }

    // ✅ Actualizar un paciente existente
    public static void actualizarPaciente(Paciente paciente) {
        try (FileInputStream fis = new FileInputStream(ARCHIVO)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            String cedulaNorm = normalizarCedula(paciente.getCedula());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && normalizarCedula(getCellValue(row.getCell(0))).equals(cedulaNorm)) {
                    row.getCell(1).setCellValue(paciente.getNombre());
                    row.getCell(2).setCellValue(paciente.getApellido());
                    row.getCell(3).setCellValue(paciente.getEdad());
                    row.getCell(4).setCellValue(paciente.getDireccion());
                    row.getCell(5).setCellValue(paciente.getTelefono());
                    row.getCell(6).setCellValue(paciente.getCorreo());
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(ARCHIVO)) {
                workbook.write(fos);
            }
            workbook.close();

        } catch (Exception e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
        }
    }

    // ✅ Utilidades
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        if (cell.getCellType() == CellType.FORMULA) {
            CellType cached = cell.getCachedFormulaResultType();
            if (cached == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
            if (cached == CellType.STRING) return cell.getStringCellValue();
            return "";
        }
        return "";
    }

    // Intentar calcular edad desde fecha de nacimiento; si falla, usar la edad cruda
    private static int calcularEdadDesdeCelda(Cell fechaCell, String edadCruda) {
        try {
            java.util.Date fecha = null;
            if (fechaCell != null) {
                if (fechaCell.getCellType() == CellType.NUMERIC ||
                        fechaCell.getCachedFormulaResultType() == CellType.NUMERIC) {
                    fecha = fechaCell.getDateCellValue();
                } else if (fechaCell.getCellType() == CellType.STRING) {
                    String s = fechaCell.getStringCellValue().trim();
                    if (!s.isEmpty()) {
                        // Intentar varios formatos comunes
                        String[] patrones = {"dd/MM/yyyy", "d/M/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"};
                        for (String p : patrones) {
                            try {
                                java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern(p);
                                java.time.LocalDate ld = java.time.LocalDate.parse(s, f);
                                fecha = java.util.Date.from(ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                                break;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
            if (fecha != null) {
                java.time.LocalDate nacimiento = fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                return java.time.Period.between(nacimiento, java.time.LocalDate.now()).getYears();
            }
        } catch (Exception ignored) {}
        return parseIntSafe(edadCruda);
    }

    private static int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}