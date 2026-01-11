package com.mycompany.laboratorioapp.examenes;

import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.ordenes.Orden;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class VentanaDetallesExamen {
    private final JFrame frame;
    private final Orden orden;

    // UI dinámica: lista de exámenes a la izquierda y panel de detalles a la derecha
    private JList<String> listaExamenes;
    private JPanel panelDerecho;
    private CardLayout cards;

    // Campos Hematología para cálculo de índices
    private JTextField hbField, htoField, rbcField, wbcField, pltField, vcmField, hcmField, chcmField;

    public VentanaDetallesExamen(Orden orden) {
        this.orden = orden;
        frame = new JFrame("Detalles de Exámenes - Orden " + (orden != null ? orden.getNumeroOrden() : ""));
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Header (igual que antes)
        JPanel header = new JPanel(new GridLayout(0, 2, 8, 6));
        header.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));
        header.add(new JLabel("Cédula:"));
        header.add(new JLabel(orden != null ? orden.getCedula() : ""));
        header.add(new JLabel("Nombre:"));
        header.add(new JLabel(orden != null ? (orden.getNombres() + " " + orden.getApellidos()) : ""));
        header.add(new JLabel("Fecha:"));
        header.add(new JLabel(orden != null ? orden.getFechaRegistro() : ""));

        // Centro: Split con lista de exámenes a la izquierda y panel dinámico a la derecha
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.25);

        // Izquierda: exámenes de la orden
        // Convertir códigos de exámenes a nombres para mostrar
        DefaultListModel<String> lm = new DefaultListModel<>();
        if (orden != null && orden.getExamenes() != null) {
            for (String codigoExamen : orden.getExamenes()) {
                if (codigoExamen != null && !codigoExamen.trim().isEmpty()) {
                    // Intentar obtener el nombre del examen por su código
                    String nombreExamen = ExamenDAO.obtenerNombrePorCodigo(codigoExamen);
                    if (nombreExamen != null && !nombreExamen.isEmpty()) {
                        lm.addElement(nombreExamen);
                    } else {
                        // Si no se encuentra el nombre, mostrar el código como fallback
                        lm.addElement(codigoExamen);
                    }
                }
            }
        }
        listaExamenes = new JList<>(lm);
        listaExamenes.setBorder(BorderFactory.createTitledBorder("Exámenes"));
        split.setLeftComponent(new JScrollPane(listaExamenes));

        // Derecha: tarjetas por tipo de examen
        cards = new CardLayout();
        panelDerecho = new JPanel(cards);
        panelDerecho.add(crearPanelPlaceholder("Seleccione un examen de la lista."), "blank");
        panelDerecho.add(crearPanelHematologia(), "hematologia");
        split.setRightComponent(panelDerecho);

        // Listener de selección para cambiar panel
        listaExamenes.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String sel = listaExamenes.getSelectedValue();
            if (sel == null) { cards.show(panelDerecho, "blank"); return; }
            String n = normalizar(sel);
            if (n.contains("hematologia completa") || n.contains("hematologia")) {
                cards.show(panelDerecho, "hematologia");
            } else {
                panelDerecho.add(crearPanelSimple(sel), "simple");
                cards.show(panelDerecho, "simple");
            }
        });

        // Inferior: acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        JButton guardar = new JButton("Guardar");
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> frame.dispose());
        acciones.add(guardar);
        acciones.add(cerrar);

        frame.add(header, BorderLayout.NORTH);
        frame.add(split, BorderLayout.CENTER);
        frame.add(acciones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelPlaceholder(String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(msg, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelSimple(String nombreExamen) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(nombreExamen));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Obtener rango de referencia para este examen
        String referencia = obtenerReferenciaPorNombre(nombreExamen);
        
        int y = 0;
        gbc.gridx = 0; gbc.gridy = y;
        JLabel labelResultado = new JLabel("Resultado:");
        labelResultado.setFont(labelResultado.getFont().deriveFont(Font.BOLD));
        p.add(labelResultado, gbc);
        gbc.gridx = 1;
        JTextField campoResultado = new JTextField(18);
        p.add(campoResultado, gbc);
        gbc.gridx = 2;
        JLabel refLabel = new JLabel("Ref:");
        refLabel.setFont(refLabel.getFont().deriveFont(Font.BOLD));
        p.add(refLabel, gbc);
        gbc.gridx = 3;
        JLabel refValue = new JLabel(referencia);
        refValue.setForeground(new Color(0, 100, 0)); // Verde oscuro
        refValue.setFont(refValue.getFont().deriveFont(Font.PLAIN, 10f));
        p.add(refValue, gbc);
        
        return p;
    }
    
    /**
     * Obtiene el rango de referencia recomendado basado en el nombre del examen
     */
    private String obtenerReferenciaPorNombre(String nombreExamen) {
        if (nombreExamen == null || nombreExamen.trim().isEmpty()) {
            return "-";
        }
        
        String nombreNormalizado = normalizar(nombreExamen);
        
        // Anticuerpos y pruebas serológicas
        if (nombreNormalizado.contains("anti") || nombreNormalizado.contains("anticuerpo") || 
            nombreNormalizado.contains("ac.") || nombreNormalizado.contains("ac ")) {
            if (nombreNormalizado.contains("dna")) {
                return "Negativo";
            }
            if (nombreNormalizado.contains("la") || nombreNormalizado.contains("ssb") || 
                nombreNormalizado.contains("ro") || nombreNormalizado.contains("scl")) {
                return "Negativo (< 20 U/mL)";
            }
            return "Negativo/Positivo";
        }
        
        // Helicobacter Pylori
        if (nombreNormalizado.contains("helicobacter") || nombreNormalizado.contains("pylori")) {
            if (nombreNormalizado.contains("igm") || nombreNormalizado.contains("igg") || 
                nombreNormalizado.contains("iga")) {
                return "Negativo (< 0.9) | Positivo (≥ 1.1)";
            }
            if (nombreNormalizado.contains("heces") || nombreNormalizado.contains("heces")) {
                return "Negativo";
            }
            return "Negativo/Positivo";
        }
        
        // Pruebas de DNA
        if (nombreNormalizado.contains("dna") || nombreNormalizado.contains("adn")) {
            return "Negativo";
        }
        
        // Pruebas de inmunofluorescencia
        if (nombreNormalizado.contains("inmunofluorescencia") || nombreNormalizado.contains("immunofluorescence")) {
            return "Negativo";
        }
        
        // Pruebas de glucosa
        if (nombreNormalizado.contains("glucosa") || nombreNormalizado.contains("glucose")) {
            return "70-100 mg/dL (ayunas)";
        }
        
        // Pruebas de colesterol
        if (nombreNormalizado.contains("colesterol") || nombreNormalizado.contains("cholesterol")) {
            if (nombreNormalizado.contains("total")) {
                return "< 200 mg/dL";
            }
            if (nombreNormalizado.contains("hdl")) {
                return "> 40 mg/dL (H) | > 50 mg/dL (M)";
            }
            if (nombreNormalizado.contains("ldl")) {
                return "< 100 mg/dL";
            }
            return "< 200 mg/dL";
        }
        
        // Pruebas de triglicéridos
        if (nombreNormalizado.contains("triglicerido") || nombreNormalizado.contains("triglyceride")) {
            return "< 150 mg/dL";
        }
        
        // Pruebas de función hepática
        if (nombreNormalizado.contains("transaminasa") || nombreNormalizado.contains("alt") || 
            nombreNormalizado.contains("ast") || nombreNormalizado.contains("gpt") || 
            nombreNormalizado.contains("got")) {
            if (nombreNormalizado.contains("alt") || nombreNormalizado.contains("gpt")) {
                return "7-56 U/L";
            }
            if (nombreNormalizado.contains("ast") || nombreNormalizado.contains("got")) {
                return "10-40 U/L";
            }
            return "10-56 U/L";
        }
        
        // Pruebas de función renal
        if (nombreNormalizado.contains("creatinina") || nombreNormalizado.contains("creatinine")) {
            return "0.6-1.2 mg/dL (H) | 0.5-1.1 mg/dL (M)";
        }
        
        if (nombreNormalizado.contains("urea") || nombreNormalizado.contains("bun")) {
            return "7-20 mg/dL";
        }
        
        // Pruebas de tiroides
        if (nombreNormalizado.contains("tsh")) {
            return "0.4-4.0 mUI/L";
        }
        if (nombreNormalizado.contains("t4") || nombreNormalizado.contains("tiroxina")) {
            return "4.5-12.0 µg/dL";
        }
        if (nombreNormalizado.contains("t3")) {
            return "80-200 ng/dL";
        }
        
        // Pruebas de hemoglobina glicosilada
        if (nombreNormalizado.contains("hba1c") || nombreNormalizado.contains("hemoglobina glicosilada")) {
            return "< 5.7% (Normal) | 5.7-6.4% (Prediabetes) | ≥ 6.5% (Diabetes)";
        }
        
        // Pruebas de proteína C reactiva
        if (nombreNormalizado.contains("proteina c reactiva") || nombreNormalizado.contains("pcr") || 
            nombreNormalizado.contains("crp")) {
            return "< 3.0 mg/L";
        }
        
        // Pruebas de ferritina
        if (nombreNormalizado.contains("ferritina") || nombreNormalizado.contains("ferritin")) {
            return "15-200 ng/mL (H) | 12-150 ng/mL (M)";
        }
        
        // Pruebas de vitamina D
        if (nombreNormalizado.contains("vitamina d") || nombreNormalizado.contains("vitamin d")) {
            return "30-100 ng/mL (Suficiente)";
        }
        
        // Pruebas de ácido úrico
        if (nombreNormalizado.contains("acido urico") || nombreNormalizado.contains("uric acid")) {
            return "3.5-7.2 mg/dL (H) | 2.6-6.0 mg/dL (M)";
        }
        
        // Por defecto, para exámenes no identificados
        return "Consultar valores de referencia";
    }

    private JPanel crearPanelHematologia() {
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBorder(BorderFactory.createTitledBorder("Hematología Completa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        // Parámetros básicos con rangos de referencia
        hbField = addCampoConReferencia(cuerpo, gbc, y++, "Hemoglobina (g/dL)", "H: 13.5-17.5 | M: 12.0-15.5");
        htoField = addCampoConReferencia(cuerpo, gbc, y++, "Hematocrito (%)", "H: 40-50 | M: 36-46");
        rbcField = addCampoConReferencia(cuerpo, gbc, y++, "Eritrocitos (mill/µL)", "H: 4.5-5.5 | M: 4.0-5.0");
        wbcField = addCampoConReferencia(cuerpo, gbc, y++, "Leucocitos (mil/µL)", "4.5-11.0");
        pltField = addCampoConReferencia(cuerpo, gbc, y++, "Plaquetas (mil/µL)", "150-450");
        
        // Separador visual
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cuerpo.add(new JSeparator(), gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        
        // Índices calculados con fórmulas (cada uno ocupa 2 filas: campo + fórmula)
        vcmField = addCampoCalculadoConFormula(cuerpo, gbc, y, "VCM (fL)", "80-100", "VCM = (Hto × 10) / RBC");
        y += 2; // Incrementar por campo + fórmula
        hcmField = addCampoCalculadoConFormula(cuerpo, gbc, y, "HCM (pg)", "27-31", "HCM = (Hb × 10) / RBC");
        y += 2; // Incrementar por campo + fórmula
        chcmField = addCampoCalculadoConFormula(cuerpo, gbc, y, "CHCM (g/dL)", "32-36", "CHCM = (Hb × 100) / Hto");

        // Recalcular índices cuando cambian hb/hto/rbc
        DocumentListener dl = new SimpleDocListener(this::recalcularIndices);
        hbField.getDocument().addDocumentListener(dl);
        htoField.getDocument().addDocumentListener(dl);
        rbcField.getDocument().addDocumentListener(dl);

        return cuerpo;
    }

    private JTextField addCampo(JPanel p, GridBagConstraints gbc, int y, String label) {
        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; JTextField t = new JTextField(18); p.add(t, gbc);
        gbc.gridx = 2; p.add(new JLabel("Ref:"), gbc);
        gbc.gridx = 3; p.add(new JLabel("-"), gbc);
        return t;
    }
    
    private JTextField addCampoConReferencia(JPanel p, GridBagConstraints gbc, int y, String label, String referencia) {
        gbc.gridx = 0; gbc.gridy = y; 
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(labelComp.getFont().deriveFont(Font.BOLD));
        p.add(labelComp, gbc);
        gbc.gridx = 1; JTextField t = new JTextField(18); p.add(t, gbc);
        gbc.gridx = 2; 
        JLabel refLabel = new JLabel("Ref:");
        refLabel.setFont(refLabel.getFont().deriveFont(Font.BOLD));
        p.add(refLabel, gbc);
        gbc.gridx = 3; 
        JLabel refValue = new JLabel(referencia);
        refValue.setForeground(new Color(0, 100, 0)); // Verde oscuro
        refValue.setFont(refValue.getFont().deriveFont(Font.PLAIN, 10f));
        p.add(refValue, gbc);
        return t;
    }

    private JTextField addCampoSoloLectura(JPanel p, GridBagConstraints gbc, int y, String label) {
        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; JTextField t = new JTextField(18); t.setEditable(false); p.add(t, gbc);
        gbc.gridx = 2; p.add(new JLabel("Ref:"), gbc);
        gbc.gridx = 3; p.add(new JLabel("-"), gbc);
        return t;
    }
    
    private JTextField addCampoCalculadoConFormula(JPanel p, GridBagConstraints gbc, int y, String label, String referencia, String formula) {
        gbc.gridx = 0; gbc.gridy = y; 
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(labelComp.getFont().deriveFont(Font.BOLD));
        p.add(labelComp, gbc);
        gbc.gridx = 1; 
        JTextField t = new JTextField(18); 
        t.setEditable(false);
        t.setBackground(new Color(240, 240, 240)); // Fondo gris claro para indicar que es calculado
        p.add(t, gbc);
        gbc.gridx = 2; 
        JLabel refLabel = new JLabel("Ref:");
        refLabel.setFont(refLabel.getFont().deriveFont(Font.BOLD));
        p.add(refLabel, gbc);
        gbc.gridx = 3; 
        JLabel refValue = new JLabel(referencia);
        refValue.setForeground(new Color(0, 100, 0)); // Verde oscuro
        refValue.setFont(refValue.getFont().deriveFont(Font.PLAIN, 10f));
        p.add(refValue, gbc);
        
        // Agregar fórmula en la siguiente fila
        gbc.gridx = 1; gbc.gridy = y + 1; gbc.gridwidth = 3;
        JLabel formulaLabel = new JLabel("Fórmula: " + formula);
        formulaLabel.setForeground(new Color(0, 0, 150)); // Azul oscuro
        formulaLabel.setFont(formulaLabel.getFont().deriveFont(Font.ITALIC, 9f));
        p.add(formulaLabel, gbc);
        gbc.gridwidth = 1;
        
        return t;
    }

    private void recalcularIndices() {
        double hb = parseDouble(hbField.getText());
        double hto = parseDouble(htoField.getText());
        double rbc = parseDouble(rbcField.getText());
        if (rbc > 0) {
            double vcm = (hto * 10.0) / rbc; // fL
            double hcm = (hb * 10.0) / rbc;  // pg
            vcmField.setText(form(vcm));
            hcmField.setText(form(hcm));
        } else {
            vcmField.setText("");
            hcmField.setText("");
        }
        if (hto > 0) {
            double chcm = (hb * 100.0) / hto; // g/dL
            chcmField.setText(form(chcm));
        } else {
            chcmField.setText("");
        }
    }

    private static String form(double v) { return String.format("%.2f", v); }
    private static double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; } }

    private static class SimpleDocListener implements DocumentListener {
        private final Runnable onChange;
        private SimpleDocListener(Runnable r) { this.onChange = r; }
        @Override public void insertUpdate(DocumentEvent e) { onChange.run(); }
        @Override public void removeUpdate(DocumentEvent e) { onChange.run(); }
        @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String t = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return t.toLowerCase().trim();
    }

    public void mostrar() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}




