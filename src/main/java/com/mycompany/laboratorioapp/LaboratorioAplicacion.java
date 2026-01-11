package com.mycompany.laboratorioapp;

import com.mycompany.laboratorioapp.usuarios.login;
import javax.swing.SwingUtilities;

public class LaboratorioAplicacion {
        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login(); // Abre directamente el login
        });
    }
}