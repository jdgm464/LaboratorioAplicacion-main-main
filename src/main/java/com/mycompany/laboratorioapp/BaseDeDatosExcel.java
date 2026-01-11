package com.mycompany.laboratorioapp;

import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.dao.PacienteDAO;
import com.mycompany.laboratorioapp.examenes.Examen;
import com.mycompany.laboratorioapp.pacientes.Paciente;

import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class BaseDeDatosExcel {
    // Métodos que ahora usan los DAOs
    public static void guardarPaciente(java.util.Map<String, String> datos) {
        try {
            Paciente paciente = new Paciente(
                datos.getOrDefault("cedula", ""),
                datos.getOrDefault("nombre", ""),
                datos.getOrDefault("apellido", ""),
                Integer.parseInt(datos.getOrDefault("edad", "0")),
                datos.getOrDefault("direccion", ""),
                datos.getOrDefault("telefono", ""),
                datos.getOrDefault("correo", "")
            );
            PacienteDAO.insertar(paciente);
        } catch (Exception e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
        }
    }

    public static void actualizarPaciente(String cedula, java.util.Map<String, String> datos) {
        try {
            Paciente paciente = new Paciente(
                cedula,
                datos.getOrDefault("nombre", ""),
                datos.getOrDefault("apellido", ""),
                Integer.parseInt(datos.getOrDefault("edad", "0")),
                datos.getOrDefault("direccion", ""),
                datos.getOrDefault("telefono", ""),
                datos.getOrDefault("correo", "")
            );
            PacienteDAO.actualizar(paciente);
        } catch (Exception e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
        }
    }

    public static void borrarPaciente(String cedula) {
        PacienteDAO.eliminar(cedula);
    }

    // Método para buscar paciente por cédula (ahora usa DAO)
    public static Paciente buscarPacientePorCedula(String cedula) {
        return PacienteDAO.buscarPorCedula(cedula);
    }
    private static String listaPreciosPath;
    private static final String CONFIG_DIR = System.getProperty("user.home") + java.io.File.separator + ".laboratorioapp";
    private static final String CONFIG_FILE = CONFIG_DIR + java.io.File.separator + "config.properties";
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

    // Cargar exámenes desde archivo de exámenes (catálogo clínico) y guardarlos en BD
    public static void cargarDesdeExcel(String rutaArchivo) throws Exception {
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

                Cell codigoCell = row.getCell(0);
                Cell nombreCell = row.getCell(1);
                Cell precioCell = row.getCell(2);

                if (nombreCell == null || precioCell == null) continue;

                String codigo = codigoCell != null ? codigoCell.toString().trim() : null;
                if (codigo == null || codigo.isBlank()) continue;
                
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

                // Guardar en la base de datos
                Examen examen = new Examen(codigo, nombre, precio);
                Examen existente = ExamenDAO.buscarPorCodigo(codigo);
                if (existente != null) {
                    ExamenDAO.actualizar(examen);
                } else {
                    ExamenDAO.insertar(examen);
                }
            }
        }
    }

    public static List<Examen> getExamenes() {
        return ExamenDAO.obtenerTodos();
    }

    // Cargar precios desde "lista precios examenes" y actualizar en BD
    public static void cargarListaPrecios(String rutaArchivo) throws Exception {
        listaPreciosPath = rutaArchivo;
        try (FileInputStream fis = new FileInputStream(rutaArchivo); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell codigoCell = row.getCell(0);
                Cell precioCell = row.getCell(2);
                if (codigoCell == null || precioCell == null) continue;
                String codigo = codigoCell.toString().trim();
                if (codigo.isBlank()) continue;
                
                double precio;
                switch (precioCell.getCellType()) {
                    case NUMERIC -> precio = precioCell.getNumericCellValue();
                    case STRING -> {
                        try { precio = Double.parseDouble(precioCell.getStringCellValue()); }
                        catch (NumberFormatException e) { precio = 0.0; }
                    }
                    default -> precio = 0.0;
                }
                
                // Actualizar precio en la base de datos
                Examen examen = ExamenDAO.buscarPorCodigo(codigo);
                if (examen != null) {
                    examen = new Examen(codigo, examen.getNombre(), precio);
                    ExamenDAO.actualizar(examen);
                }
            }
        }
    }

    public static Double getPrecioPorCodigo(String codigo) {
        return ExamenDAO.obtenerPrecio(codigo);
    }

    public static void actualizarPrecio(String codigo, double nuevoPrecio) {
        Examen examen = ExamenDAO.buscarPorCodigo(codigo);
        if (examen != null) {
            examen = new Examen(codigo, examen.getNombre(), nuevoPrecio);
            ExamenDAO.actualizar(examen);
        }
    }

    public static void setListaPreciosPath(String path) { listaPreciosPath = path; }
    public static String getListaPreciosPath() { return listaPreciosPath; }

    public static void cargarRutaListaPreciosDesdeConfig() {
        try {
            java.io.File f = new java.io.File(CONFIG_FILE);
            if (!f.exists()) return;
            java.util.Properties p = new java.util.Properties();
            try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) { p.load(fis); }
            listaPreciosPath = p.getProperty("lista_precios_path", listaPreciosPath);
        } catch (Exception ignored) {}
    }

    public static void guardarRutaListaPreciosEnConfig(String path) {
        try {
            java.io.File dir = new java.io.File(CONFIG_DIR);
            if (!dir.exists()) dir.mkdirs();
            java.util.Properties p = new java.util.Properties();
            java.io.File f = new java.io.File(CONFIG_FILE);
            if (f.exists()) { try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) { p.load(fis); } catch (Exception ignored) {} }
            p.setProperty("lista_precios_path", path == null ? "" : path);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(f)) { p.store(fos, "Configuración LaboratorioApp"); }
            listaPreciosPath = path;
        } catch (Exception ignored) {}
    }

    public static void guardarListaPrecios() throws Exception {
        if (listaPreciosPath == null || listaPreciosPath.isBlank()) return;
        try (java.io.FileInputStream fis = new java.io.FileInputStream(listaPreciosPath);
             org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis)) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) continue;
                org.apache.poi.ss.usermodel.Cell codigoCell = row.getCell(0);
                if (codigoCell == null) continue;
                String codigo = codigoCell.toString().trim();
                // Obtener precio desde la base de datos
                Double precio = ExamenDAO.obtenerPrecio(codigo);
                if (precio != null) {
                    org.apache.poi.ss.usermodel.Cell precioCell = row.getCell(2);
                    if (precioCell == null) precioCell = row.createCell(2);
                    precioCell.setCellValue(precio);
                }
            }
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(listaPreciosPath)) {
                workbook.write(fos);
            }
        }
    }
}