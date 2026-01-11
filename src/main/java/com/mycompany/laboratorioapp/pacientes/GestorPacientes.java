package com.mycompany.laboratorioapp.pacientes;

import com.mycompany.laboratorioapp.dao.PacienteDAO;
import java.util.List;

public class GestorPacientes {
    
    // ✅ Normalizar cédula para evitar errores de búsqueda
    private static String normalizarCedula(String cedula) {
        if (cedula == null) return "";
        String raw = cedula.trim().toUpperCase();
        String prefijo;
        String resto;
        if (raw.startsWith("V") || raw.startsWith("E") || raw.startsWith("A")) {
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

    // ✅ Cargar todos los pacientes desde la base de datos
    public static List<Paciente> cargarPacientes() {
        try {
            return PacienteDAO.obtenerTodos();
        } catch (Exception e) {
            System.err.println("Error al cargar pacientes: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // ✅ Buscar paciente por cédula (normalizando)
    public static Paciente buscarPorCedula(String cedula) {
        if (cedula == null) return null;
        String objetivo = normalizarCedula(cedula);
        
        try {
            // Intentar buscar con la cédula normalizada
            Paciente paciente = PacienteDAO.buscarPorCedula(objetivo);
            if (paciente != null) {
                return paciente;
            }
            
            // Si no se encuentra, intentar con la cédula original
            if (!objetivo.equals(cedula)) {
                paciente = PacienteDAO.buscarPorCedula(cedula);
            }
            
            return paciente;
        } catch (Exception e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
            return null;
        }
    }

    // ✅ Guardar un nuevo paciente en la base de datos
    // Verifica si el paciente ya existe por cédula antes de agregar
    public static void guardarPaciente(Paciente paciente) {
        try {
            // Normalizar la cédula antes de guardar
            String cedulaNormalizada = normalizarCedula(paciente.getCedula());
            Paciente pacienteNormalizado = new Paciente(
                cedulaNormalizada,
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getEdad(),
                paciente.getDireccion(),
                paciente.getTelefono(),
                paciente.getCorreo(),
                paciente.getSexo()
            );
            
            // Verificar si el paciente ya existe
            Paciente existente = PacienteDAO.buscarPorCedula(cedulaNormalizada);
            if (existente != null) {
                // Actualizar el paciente existente
                PacienteDAO.actualizar(pacienteNormalizado);
            } else {
                // Insertar nuevo paciente
                PacienteDAO.insertar(pacienteNormalizado);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
        }
    }

    // ✅ Actualizar un paciente existente en la base de datos
    public static void actualizarPaciente(Paciente paciente) {
        try {
            // Normalizar la cédula antes de actualizar
            String cedulaNormalizada = normalizarCedula(paciente.getCedula());
            Paciente pacienteNormalizado = new Paciente(
                cedulaNormalizada,
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getEdad(),
                paciente.getDireccion(),
                paciente.getTelefono(),
                paciente.getCorreo(),
                paciente.getSexo()
            );
            
            PacienteDAO.actualizar(pacienteNormalizado);
        } catch (Exception e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
        }
    }
}

