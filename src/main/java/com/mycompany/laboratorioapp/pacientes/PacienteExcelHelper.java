package com.mycompany.laboratorioapp.pacientes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class PacienteExcelHelper {
    private static final String ARCHIVO_EXCEL = "IPPUSNEG informacion..xlsx";

    private PacienteExcelHelper() {
    }

    public static Map<String, String> buscarPorCedula(String cedula) {
        Map<String, String> datos = new LinkedHashMap<>();
        if (cedula == null || cedula.trim().isEmpty()) {
            return datos;
        }

        String cedulaBuscada = normalizarCedula(cedula);
        try (Workbook workbook = abrirWorkbook()) {
            if (workbook == null) {
                return datos;
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) {
                return datos;
            }

            Map<String, Integer> map = new LinkedHashMap<>();
            for (int j = 0; j < 40; j++) {
                if (header.getCell(j) != null) {
                    map.put(normalizar(header.getCell(j).toString()), j);
                }
            }

            int idxCedula = getCol(map, "cedula");
            if (idxCedula < 0) {
                return datos;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String cedulaFila = normalizarCedula(leerCelda(row, idxCedula, false));
                if (!cedulaBuscada.equalsIgnoreCase(cedulaFila)) {
                    continue;
                }

                datos.put("codigo", leerCelda(row, getCol(map, "codigo"), true));
                datos.put("fechaNacimiento", leerCeldaFecha(row, getCol(map, "fecha nacimiento"), sdf));
                datos.put("edad", leerCelda(row, getCol(map, "edad"), true));
                datos.put("sexo", leerCelda(row, getCol(map, "sexo"), false));
                datos.put("direccion", leerCelda(row, getCol(map, "direccion"), false));
                datos.put("telefono", leerCelda(row, getCol(map, "telefono1"), true));
                datos.put("sede", leerCelda(row, getCol(map, "sede"), false));
                datos.put("categoria", leerCelda(row, getCol(map, "categoria"), false));
                datos.put("dedicacion", leerCelda(row, getCol(map, "dedicacion"), false));
                datos.put("estatus", leerCelda(row, getCol(map, "estatus"), false));
                datos.put("fechaIngreso", leerCeldaFecha(row, getCol(map, "fecha ingreso"), sdf));
                datos.put("email", leerCelda(row, getCol(map, "email"), false));
                break;
            }
        } catch (Exception e) {
            System.err.println("Error al leer datos del Excel de pacientes: " + e.getMessage());
        }

        return datos;
    }

    private static Workbook abrirWorkbook() throws Exception {
        File externo = new File(ARCHIVO_EXCEL);
        if (externo.exists()) {
            try (FileInputStream fis = new FileInputStream(externo)) {
                return WorkbookFactory.create(fis);
            }
        }

        InputStream recurso = PacienteExcelHelper.class.getClassLoader().getResourceAsStream(ARCHIVO_EXCEL);
        if (recurso == null) {
            return null;
        }
        return WorkbookFactory.create(recurso);
    }

    private static int getCol(Map<String, Integer> map, String nombre) {
        String key = normalizar(nombre);
        if (map.containsKey(key)) {
            return map.get(key);
        }

        String[] aliases = switch (key) {
            case "codigo" -> new String[]{"codigo web", "codigo paciente", "cod", "cod paciente", "n carpeta", "ncarpeta", "n carpeta paciente"};
            case "cedula" -> new String[]{"cédula"};
            case "telefono1" -> new String[]{"telefono", "telefono 1", "tlf1"};
            case "fecha nacimiento" -> new String[]{"fecha nac", "nacimiento"};
            case "fecha ingreso" -> new String[]{"ingreso", "fechaingreso"};
            case "estatus" -> new String[]{"status", "estado"};
            case "email" -> new String[]{"correo", "correo electronico"};
            default -> new String[]{};
        };

        for (String alias : aliases) {
            String aliasKey = normalizar(alias);
            if (map.containsKey(aliasKey)) {
                return map.get(aliasKey);
            }
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getKey().contains(key)) {
                return entry.getValue();
            }
        }
        return -1;
    }

    private static String leerCelda(Row row, int col, boolean enteroPreferido) {
        if (col < 0 || row.getCell(col) == null) {
            return "";
        }

        var cell = row.getCell(col);
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double valor = cell.getNumericCellValue();
                if (enteroPreferido || valor == (long) valor) {
                    yield String.valueOf((long) valor);
                }
                yield String.valueOf(valor);
            }
            case FORMULA -> {
                var tipo = cell.getCachedFormulaResultType();
                if (tipo == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                    double valor = cell.getNumericCellValue();
                    if (enteroPreferido || valor == (long) valor) {
                        yield String.valueOf((long) valor);
                    }
                    yield String.valueOf(valor);
                }
                if (tipo == org.apache.poi.ss.usermodel.CellType.STRING) {
                    yield cell.getStringCellValue().trim();
                }
                yield "";
            }
            default -> "";
        };
    }

    private static String leerCeldaFecha(Row row, int col, SimpleDateFormat sdf) {
        if (col < 0 || row.getCell(col) == null) {
            return "";
        }

        var cell = row.getCell(col);
        try {
            if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
                    || cell.getCachedFormulaResultType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                java.util.Date fecha = cell.getDateCellValue();
                return fecha != null ? sdf.format(fecha) : "";
            }
        } catch (Exception ignored) {
        }
        return cell.toString().trim();
    }

    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        String t = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
        t = t.replace("n°", "numero");
        t = t.replaceAll("[^a-z0-9 ]", "");
        return t.replaceAll("\\s+", " ");
    }

    private static String normalizarCedula(String s) {
        if (s == null) {
            return "";
        }
        String raw = s.trim().toUpperCase();
        if (raw.isEmpty()) {
            return "";
        }
        String prefijo = (raw.startsWith("V") || raw.startsWith("E") || raw.startsWith("A"))
                ? raw.substring(0, 1)
                : "V";
        String resto = raw.length() > prefijo.length() ? raw.substring(prefijo.length()) : "";
        resto = resto.replaceAll("[^0-9]", "");
        return prefijo + resto;
    }
}
