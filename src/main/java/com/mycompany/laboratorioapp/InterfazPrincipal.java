
package com.mycompany.laboratorioapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class InterfazPrincipal {

    private JFrame mainFrame;
    private static final double TASA_BCV = 131.12; // Valor fijo del BCV
    private JTextField tasaManual;
    private final String usuarioLogueado;
    private final String rolUsuario;
    private JLabel horaTextoRef;

    public InterfazPrincipal(String usuario, String rol) {
        this.usuarioLogueado = usuario;
        this.rolUsuario = rol;
        configurarVentanaPrincipal();
    }

    private void configurarVentanaPrincipal() {
        mainFrame = new JFrame("Medisoft");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 750);
        mainFrame.setMinimumSize(new Dimension(1000, 650));
        mainFrame.setLayout(new BorderLayout());

        // Barra de menús (superior)
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuVentas = new javax.swing.JMenu("Ventas");
        javax.swing.JMenuItem itemControlPrecios = new javax.swing.JMenuItem("Control de precios");
        itemControlPrecios.addActionListener(e -> {
            VentanaControlPrecios v = new VentanaControlPrecios();
            v.mostrar();
        });
        menuVentas.add(itemControlPrecios);
        menuBar.add(menuVentas);
        mainFrame.setJMenuBar(menuBar);

        agregarMenuSuperior();
        agregarPanelIzquierdo();
        agregarPanelCentral();
    }

    private void agregarMenuSuperior() {
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        menuPanel.setBackground(Color.WHITE);

        String[] menuItems = {"Órdenes", "Resultados", "Pacientes", "Presupuesto", "Archivos"};

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setBackground(new Color(0, 191, 255));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(120, 40));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1, true));

            // Acción al hacer clic
            button.addActionListener(e -> {
                switch (item) {
                    case "Órdenes" -> {
                        VentanaOrdenes ventana = new VentanaOrdenes();
                        ventana.mostrar();
                    }
                    case "Resultados" -> {
                        VentanaResultados ventana = new VentanaResultados();
                        ventana.mostrar();
                    }
                    case "Pacientes" -> {
                        VentanaPacientes ventana = new VentanaPacientes();
                        ventana.mostrar();
                    }
                    case "Presupuesto" -> {
                        VentanaPresupuestos ventana = new VentanaPresupuestos();
                        ventana.mostrar();
                    }
                    case "Archivos" -> {
                        VentanaArchivos ventana = new VentanaArchivos();
                        ventana.mostrar();
                    }
                }
            });

            // Limitar funciones si es usuario
            if (rolUsuario.equals("Usuario")
                    && (item.equals("Resultados") || item.equals("Pacientes") || item.equals("Presupuesto") || item.equals("Archivos"))) {
                button.setEnabled(false);
            }

            menuPanel.add(button);
        }

        mainFrame.add(menuPanel, BorderLayout.NORTH);
    }

    private void agregarPanelIzquierdo() {
    JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.setBackground(Color.WHITE);
    leftPanel.setPreferredSize(new Dimension(220, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new java.awt.Insets(6, 5, 6, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.NONE;

    // ---------- Logo arriba del nombre de la aplicación ----------
    JLabel logoLabel = crearImagenEscalada("logo para la aplicacion .jpg", 90, 90);
    leftPanel.add(logoLabel, gbc);

    // ---------- Nombre de la aplicación ----------
    gbc.gridy++;
    // ---------- Nombre de la aplicación ----------
    JLabel tituloApp = new JLabel("Medisoft");
    tituloApp.setFont(new Font("Arial", Font.BOLD, 22));
    tituloApp.setForeground(new Color(0, 102, 204)); // Azul elegante
    leftPanel.add(tituloApp, gbc);

    // ---------- Icono BCV ----------
    gbc.gridy++;
    JLabel iconBCV = new JLabel("<html>💲");
    iconBCV.setFont(new Font("Arial", Font.PLAIN, 36));
    leftPanel.add(iconBCV, gbc);

    // ---------- Combo selección automática/manual ----------
    gbc.gridy++;
    JComboBox<String> tasaCombo = new JComboBox<>(new String[]{"Automática", "Manual"});
    tasaCombo.setPreferredSize(new Dimension(160, 28));
    tasaCombo.setFont(new Font("Arial", Font.PLAIN, 14));
    leftPanel.add(tasaCombo, gbc);

    // ---------- Campo para la tasa ----------
    gbc.gridy++;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(6, 5, 6, 5);

    tasaManual = new JTextField(String.format("%.2f", TASA_BCV));
    tasaManual.setPreferredSize(new Dimension(140, 36)); // ancho más reducido
    tasaManual.setFont(new Font("Arial", Font.BOLD, 18));
    tasaManual.setHorizontalAlignment(JTextField.CENTER);
    tasaManual.setBackground(Color.WHITE);
    tasaManual.setForeground(Color.BLACK);
    tasaManual.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    tasaManual.setEnabled(false);
    leftPanel.add(tasaManual, gbc);

    tasaCombo.addActionListener(e -> {
        if ("Automática".equals(tasaCombo.getSelectedItem())) {
            tasaManual.setText(String.format("%.2f", TASA_BCV));
            tasaManual.setEnabled(false);
        } else {
            tasaManual.setText("0.00");
            tasaManual.setEnabled(true);
        }
    });

    // ---------- Fecha ----------
    gbc.gridy++;
    String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    fechaPanel.setOpaque(false);
    JLabel iconFecha = new JLabel("<html>📅</html>");
    iconFecha.setFont(new Font("Arial", Font.PLAIN, 16));
    JLabel fechaTexto = new JLabel(fecha);
    fechaTexto.setFont(new Font("Arial", Font.BOLD, 14));
    fechaPanel.add(iconFecha);
    fechaPanel.add(fechaTexto);
    leftPanel.add(fechaPanel, gbc);

    // ---------- Hora ----------
    gbc.gridy++;
    String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
    JPanel horaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    horaPanel.setOpaque(false);
    JLabel iconHora = new JLabel(new ClockIcon(16, Color.DARK_GRAY));
    horaTextoRef = new JLabel(hora);
    horaTextoRef.setFont(new Font("Arial", Font.BOLD, 14));
    horaPanel.add(iconHora);
    horaPanel.add(horaTextoRef);
    leftPanel.add(horaPanel, gbc);

    // ---------- Usuario logueado ----------
    gbc.gridy++;
    JLabel usuarioLabel = new JLabel("<html>👤 " + usuarioLogueado + " (" + rolUsuario + ")</html>");
    usuarioLabel.setFont(new Font("Arial", Font.BOLD, 16));
    leftPanel.add(usuarioLabel, gbc);

    // Filler para empujar todo hacia arriba
    gbc.gridy++;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    JPanel filler = new JPanel();
    filler.setOpaque(false);
    leftPanel.add(filler, gbc);

    mainFrame.add(leftPanel, BorderLayout.WEST);

    // Actualiza la hora cada segundo
    javax.swing.Timer t = new javax.swing.Timer(1000, e -> {
        horaTextoRef.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
    });
    t.start();
}

    private void agregarPanelCentral() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

    // Imagen central (doctor) como en el diseño
        // Cargar específicamente la imagen de fondo definida en resources, con fallback via CargarImagen
        JLabel imagenCentral;
        try {
            imagenCentral = crearImagenEscalada("fondo para la aplicacion.jpg", 900, 420);
            if (imagenCentral.getIcon() == null) {
                javax.swing.ImageIcon fallbackIcon = CargarImagen.obtenerFondo();
                imagenCentral = crearImagenEscalada(fallbackIcon, 900, 420);
            }
        } catch (Exception ex) {
            javax.swing.ImageIcon fallbackIcon = CargarImagen.obtenerFondo();
            imagenCentral = crearImagenEscalada(fallbackIcon, 900, 420);
        }
        imagenCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(imagenCentral, BorderLayout.CENTER);

        mainFrame.add(centerPanel, BorderLayout.CENTER);
    }

    // Utilidad para cargar y escalar imágenes desde resources o rutas locales
    private JLabel crearImagenEscalada(String recurso, int ancho, int alto) {
        java.awt.image.BufferedImage bi = null;
        // 1) Intentar desde el classpath
        try {
            java.net.URL url = getClass().getClassLoader().getResource(recurso);
            if (url == null) url = getClass().getResource("/" + recurso);
            if (url != null) {
                bi = javax.imageio.ImageIO.read(url);
            }
        } catch (Exception ignored) {}

        // 2) Intentar en rutas locales comunes
        if (bi == null) {
            String[] rutas = new String[] {
                recurso,
                "resources/" + recurso,
                "src/main/resources/" + recurso,
                "C:\\Users\\Gateway\\OneDrive\\Escritorio\\LaboratorioAplicacion-main-main\\src\\main\\resources\\depositphotos_23805293-stock-photo-medicine-doctor-hand-working-with.jpg"
            };
            // Buscar relativo al directorio de trabajo
            String base = System.getProperty("user.dir", ".");
            for (String ruta : rutas) {
                try {
                    java.io.File f = new java.io.File(ruta);
                    if (!f.isAbsolute()) f = new java.io.File(base, ruta);
                    if (f.exists()) {
                        bi = javax.imageio.ImageIO.read(f);
                        if (bi != null) break;
                    }
                } catch (Exception ignored) {}
            }
        }

        if (bi != null) {
            int iw = bi.getWidth();
            int ih = bi.getHeight();
            double scale = Math.min((double) ancho / Math.max(1, iw), (double) alto / Math.max(1, ih));
            int nw = Math.max(1, (int) Math.round(iw * scale));
            int nh = Math.max(1, (int) Math.round(ih * scale));
            java.awt.Image scaled = bi.getScaledInstance(nw, nh, java.awt.Image.SCALE_SMOOTH);
            java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(ancho, alto, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = canvas.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, ancho, alto);
            int x = (ancho - nw) / 2;
            int y = (alto - nh) / 2;
            g2.drawImage(scaled, x, y, null);
            g2.dispose();
            return new JLabel(new javax.swing.ImageIcon(canvas));
        }

        // 3) Si no se encontró, devolver label vacío con pista
        JLabel fallback = new JLabel(" ");
        fallback.setToolTipText("No se encontró la imagen: " + recurso + " (dir=" + System.getProperty("user.dir") + ")");
        return fallback;
    }

    // Sobrecarga: escalar desde un ImageIcon existente conservando proporciones y centrado
    private JLabel crearImagenEscalada(javax.swing.ImageIcon icon, int ancho, int alto) {
        try {
            java.awt.Image src = icon.getImage();
            int iw = icon.getIconWidth();
            int ih = icon.getIconHeight();
            if (iw <= 0 || ih <= 0) return new JLabel(icon);
            double scale = Math.min((double) ancho / Math.max(1, iw), (double) alto / Math.max(1, ih));
            int nw = Math.max(1, (int) Math.round(iw * scale));
            int nh = Math.max(1, (int) Math.round(ih * scale));
            java.awt.Image scaled = src.getScaledInstance(nw, nh, java.awt.Image.SCALE_SMOOTH);
            java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(ancho, alto, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = canvas.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, ancho, alto);
            int x = (ancho - nw) / 2;
            int y = (alto - nh) / 2;
            g2.drawImage(scaled, x, y, null);
            g2.dispose();
            return new JLabel(new javax.swing.ImageIcon(canvas));
        } catch (Exception e) {
            return new JLabel(icon);
        }
    }

    // Icono vectorial de reloj para evitar problemas de fuente/emoji
    private static class ClockIcon implements javax.swing.Icon {
        private final int size;
        private final java.awt.Color color;
        ClockIcon(int size, java.awt.Color color) { this.size = size; this.color = color; }
        @Override public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            int s = Math.min(size, Math.min(getIconWidth(), getIconHeight()));
            int r = s - 2;
            g2.setColor(color);
            g2.drawOval(x + 1, y + 1, r, r);
            // Centro
            int cx = x + 1 + r/2;
            int cy = y + 1 + r/2;
            // Manecillas: hora y minutos fijas a 10:10
            g2.drawLine(cx, cy, cx, cy - r/3);
            g2.drawLine(cx, cy, cx + r/3, cy);
            g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public void mostrar() {
        mainFrame.setVisible(true);
    }
}