package com.mycompany.laboratorioapp;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class BaseDeDatosExcel {
    // Métodos vacíos para evitar errores de compilación
    public static void guardarPaciente(java.util.Map<String, String> datos) {
        // Implementar lógica real aquí
    }

    public static void actualizarPaciente(String cedula, java.util.Map<String, String> datos) {
        // Implementar lógica real aquí
    }

    public static void borrarPaciente(String cedula) {
        // Implementar lógica real aquí
    }
    // Lista de pacientes en memoria
    private final static List<Paciente> pacientes = new ArrayList<>();

    // Método para buscar paciente por cédula
    public static Paciente buscarPacientePorCedula(String cedula) {
        for (Paciente p : pacientes) {
            if (p.getCedula().equals(cedula)) {
                return p;
            }
        }
        return null;
    }
    private final static List<Examen> examenes = new ArrayList<>();
    private final static List<String> entidades = List.of(
        "Hospital Central",
        "Clínica La Salud",
        "Laboratorio ABC",
        "Centro Médico XYZ",
        "Sanatorio Nacional"
    );
    public static List<String> getEntidades() {
        return entidades;
    }

    public static void cargarDesdeExcel(String rutaArchivo) throws Exception {
        examenes.clear();

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {


            // Buscar hoja con nombre alternativo si no existe 'Examenes'
            Sheet sheet = workbook.getSheet("Examenes");
            if (sheet == null) sheet = workbook.getSheet("examenes");
            if (sheet == null) sheet = workbook.getSheet("EXAMENES");
            if (sheet == null) sheet = workbook.getSheet("Exámenes");
            if (sheet == null) sheet = workbook.getSheet("EXÁMENES");
            if (sheet == null) sheet = workbook.getSheetAt(0); // como último recurso, la primera hoja
            if (sheet == null) {
                throw new Exception("No se encontró una hoja de exámenes válida en el archivo Excel.");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // comenzamos desde fila 1 (asumiendo fila 0 = encabezado)
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell nombreCell = row.getCell(0);
                Cell precioCell = row.getCell(1);

                if (nombreCell == null || precioCell == null) continue;


                String nombre;
                switch (nombreCell.getCellType()) {
                    case STRING -> nombre = nombreCell.getStringCellValue();
                    case NUMERIC -> nombre = String.valueOf(nombreCell.getNumericCellValue());
                    default -> nombre = "";
                }

                double precio;
                switch (precioCell.getCellType()) {
                    case NUMERIC -> precio = precioCell.getNumericCellValue();
                    case STRING -> {
                        try {
                            precio = Double.parseDouble(precioCell.getStringCellValue());
                        } catch (NumberFormatException e) {
                            precio = 0.0;
                        }
                    }
                    default -> precio = 0.0;
                }

                examenes.add(new Examen(nombre, precio));
            }
        }
    }

    public static List<Examen> getExamenes() {
        return examenes;
    }
}