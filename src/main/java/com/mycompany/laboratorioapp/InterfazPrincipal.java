
package com.mycompany.laboratorioapp;

import com.mycompany.laboratorioapp.examenes.VentanaControlPrecios;
import com.mycompany.laboratorioapp.examenes.VentanaPresupuestos;
import com.mycompany.laboratorioapp.ordenes.VentanaOrdenes;
import com.mycompany.laboratorioapp.pacientes.VentanaPacientes;
import com.mycompany.laboratorioapp.resultados.VentanaResultados;
import com.mycompany.laboratorioapp.usuarios.VentanaRegistroUsuarios;

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
    // Tasa BCV leída de config (editable). Si no existe, usamos 201.47 por defecto.
    private static final String CONFIG_DIR = System.getProperty("user.home") + java.io.File.separator + ".laboratorioapp";
    private static final String CONFIG_FILE = CONFIG_DIR + java.io.File.separator + "config.properties";
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

        // Menú Usuarios
        javax.swing.JMenu menuUsuarios = new javax.swing.JMenu("Usuarios");
        javax.swing.JMenuItem itemRegistroUsuarios = new javax.swing.JMenuItem("Registros de Usuarios");
        itemRegistroUsuarios.addActionListener(e -> {
            VentanaRegistroUsuarios v = new VentanaRegistroUsuarios();
            v.mostrar();
        });
        menuUsuarios.add(itemRegistroUsuarios);
        menuBar.add(menuUsuarios);
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

    double tasaInicial = leerTasaBCVDeConfig();
    tasaManual = new JTextField(String.format("%.2f", tasaInicial));
    tasaManual.setPreferredSize(new Dimension(140, 36)); // ancho más reducido
    tasaManual.setFont(new Font("Arial", Font.BOLD, 18));
    tasaManual.setHorizontalAlignment(JTextField.CENTER);
    tasaManual.setBackground(Color.WHITE);
    tasaManual.setForeground(Color.BLACK);
    tasaManual.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    tasaManual.setEnabled(false);
    leftPanel.add(tasaManual, gbc);

    // Botón actualizar tasa
    gbc.gridy++;
    JButton btnActualizarTasa = new JButton("Actualizar tasa");
    btnActualizarTasa.setPreferredSize(new Dimension(160, 28));
    btnActualizarTasa.addActionListener(e -> actualizarTasaInteractivo());
    leftPanel.add(btnActualizarTasa, gbc);

    tasaCombo.addActionListener(e -> {
        if ("Automática".equals(tasaCombo.getSelectedItem())) {
            double tasa = leerTasaBCVDeConfig();
            tasaManual.setText(String.format("%.2f", tasa));
            tasaManual.setEnabled(false);
        } else {
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
        JLabel imagenCentral = null;
        
        // Intentar cargar con diferentes variantes del nombre
        // Prioridad: "fondo para la aplicacion.jpeg" (nueva imagen) y luego .jpg
        String[] nombresIntentar = {
            "fondo para la aplicacion.jpeg",     // Nueva imagen con extensión .jpeg
            "fondo para la aplicacion.jpg",      // Sin espacio (nombre exacto según el usuario)
            "fondo para la aplicacion .jpg",     // Con espacio antes de .jpg
            "fondo.png",                         // Fallback a PNG
            "fondo.jpg"                          // Fallback genérico
        };
        
        for (String nombre : nombresIntentar) {
            imagenCentral = crearImagenEscalada(nombre, 900, 420);
            if (imagenCentral != null && imagenCentral.getIcon() != null) {
                javax.swing.ImageIcon icon = (javax.swing.ImageIcon) imagenCentral.getIcon();
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    System.out.println("✅ Imagen de fondo cargada exitosamente: " + nombre);
                    break;
                }
            }
        }
        
        // Si aún no se cargó, intentar con CargarImagen
        if (imagenCentral == null || imagenCentral.getIcon() == null) {
            System.out.println("⚠️ Intentando cargar fondo con CargarImagen.obtenerFondo()...");
            javax.swing.ImageIcon fallbackIcon = CargarImagen.obtenerFondo();
            if (fallbackIcon != null && fallbackIcon.getIconWidth() > 0) {
                imagenCentral = crearImagenEscalada(fallbackIcon, 900, 420);
                System.out.println("✅ Imagen de fondo cargada desde CargarImagen");
            }
        }
        
        // Si aún no hay imagen, crear un label con mensaje
        if (imagenCentral == null || imagenCentral.getIcon() == null) {
            System.err.println("❌ No se pudo cargar ninguna imagen de fondo");
            imagenCentral = new JLabel("Imagen de fondo no encontrada");
            imagenCentral.setHorizontalAlignment(JLabel.CENTER);
            imagenCentral.setForeground(Color.GRAY);
        }
        
        imagenCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Centrar la imagen en el panel
        imagenCentral.setHorizontalAlignment(JLabel.CENTER);
        imagenCentral.setVerticalAlignment(JLabel.CENTER);
        centerPanel.add(imagenCentral, BorderLayout.CENTER);

        mainFrame.add(centerPanel, BorderLayout.CENTER);
    }

    // Utilidad para cargar y escalar imágenes desde resources o rutas locales
    private JLabel crearImagenEscalada(String recurso, int ancho, int alto) {
        java.awt.image.BufferedImage bi = null;
        String rutaEncontrada = null;
        
        // PRIORIDAD 1: Intentar cargar directamente desde archivo (más confiable para archivos con espacios)
        String base = System.getProperty("user.dir", ".");
        String[] rutasArchivo = new String[] {
            // Ruta absoluta desde src/main/resources (para desarrollo)
            base + java.io.File.separator + "src" + java.io.File.separator + "main" + java.io.File.separator + "resources" + java.io.File.separator + recurso,
            // Ruta desde target/classes (después de compilar con Maven)
            base + java.io.File.separator + "target" + java.io.File.separator + "classes" + java.io.File.separator + recurso,
            // Rutas relativas
            "src/main/resources/" + recurso,
            "target/classes/" + recurso,
            recurso
        };
        
        System.out.println("🔍 Buscando imagen: " + recurso);
        for (String ruta : rutasArchivo) {
            try {
                java.io.File f = new java.io.File(ruta);
                if (!f.isAbsolute() && !ruta.startsWith(base)) {
                    f = new java.io.File(base, ruta);
                }
                System.out.println("  🔍 Verificando archivo: " + f.getAbsolutePath() + " (existe: " + f.exists() + ", tamaño: " + (f.exists() ? f.length() + " bytes" : "N/A") + ")");
                if (f.exists() && f.isFile() && f.canRead()) {
                    // Verificar formato del archivo leyendo los primeros bytes (magic numbers)
                    try {
                        java.io.FileInputStream fis = new java.io.FileInputStream(f);
                        byte[] header = new byte[4];
                        int bytesRead = fis.read(header);
                        fis.close();
                        
                        if (bytesRead >= 2) {
                            // Verificar si es JPEG (empieza con FF D8)
                            boolean isJPEG = (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8;
                            // Verificar si es PNG (empieza con 89 50 4E 47)
                            boolean isPNG = bytesRead >= 4 && header[0] == (byte)0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
                            
                            System.out.println("  📄 Formato detectado: " + (isJPEG ? "JPEG" : isPNG ? "PNG" : "Desconocido") + 
                                             " (bytes: " + String.format("%02X %02X", header[0] & 0xFF, header[1] & 0xFF) + ")");
                            
                            if (!isJPEG && !isPNG) {
                                System.err.println("⚠️ El archivo no parece ser una imagen válida (JPEG/PNG)");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al verificar formato del archivo: " + e.getMessage());
                    }
                    
                    try {
                        bi = javax.imageio.ImageIO.read(f);
                        if (bi != null && bi.getWidth() > 0 && bi.getHeight() > 0) {
                            rutaEncontrada = f.getAbsolutePath();
                            System.out.println("✅ Imagen cargada desde archivo: " + rutaEncontrada + " (" + bi.getWidth() + "x" + bi.getHeight() + ")");
                            break;
                        } else {
                            System.err.println("⚠️ ImageIO.read() devolvió null o imagen inválida para: " + f.getAbsolutePath());
                            // Intentar con ImageIcon como alternativa
                            try {
                                System.out.println("  🔄 Intentando cargar con ImageIcon...");
                                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(f.getAbsolutePath());
                                System.out.println("  📊 ImageIcon dimensiones: " + icon.getIconWidth() + "x" + icon.getIconHeight());
                                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                                    java.awt.Image img = icon.getImage();
                                    if (img != null) {
                                        // Esperar a que la imagen se cargue completamente
                                        java.awt.MediaTracker tracker = new java.awt.MediaTracker(new javax.swing.JPanel());
                                        tracker.addImage(img, 0);
                                        try {
                                            tracker.waitForID(0, 2000); // Esperar hasta 2 segundos
                                        } catch (InterruptedException ie) {
                                            Thread.currentThread().interrupt();
                                        }
                                        
                                        bi = new java.awt.image.BufferedImage(icon.getIconWidth(), icon.getIconHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
                                        java.awt.Graphics2D g = bi.createGraphics();
                                        g.drawImage(img, 0, 0, null);
                                        g.dispose();
                                        if (bi != null && bi.getWidth() > 0 && bi.getHeight() > 0) {
                                            rutaEncontrada = f.getAbsolutePath() + " (via ImageIcon)";
                                            System.out.println("✅ Imagen cargada usando ImageIcon: " + rutaEncontrada + " (" + bi.getWidth() + "x" + bi.getHeight() + ")");
                                            break;
                                        }
                                    } else {
                                        System.err.println("⚠️ ImageIcon.getImage() devolvió null");
                                    }
                                } else {
                                    System.err.println("⚠️ ImageIcon también devolvió dimensiones inválidas");
                                }
                            } catch (Exception e2) {
                                System.err.println("❌ Error al cargar con ImageIcon: " + e2.getMessage());
                                e2.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error al leer archivo con ImageIO: " + ruta);
                        System.err.println("   Mensaje: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error al leer archivo " + ruta + ": " + e.getMessage());
            }
        }
        
        // PRIORIDAD 2: Intentar desde el classpath (solo si no se encontró en archivos)
        if (bi == null) {
            try {
                java.net.URL url = null;
                
                // Método 1: ClassLoader de la clase actual
                ClassLoader classLoader = getClass().getClassLoader();
                if (classLoader != null) {
                    url = classLoader.getResource(recurso);
                    if (url == null) {
                        url = classLoader.getResource("/" + recurso);
                    }
                }
                
                // Método 2: getResource de la clase
                if (url == null) {
                    url = getClass().getResource("/" + recurso);
                }
                if (url == null) {
                    url = getClass().getResource(recurso);
                }
                
                // Método 3: Context ClassLoader
                if (url == null) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) {
                        url = cl.getResource(recurso);
                        if (url == null) {
                            url = cl.getResource("/" + recurso);
                        }
                    }
                }
                
                if (url != null) {
                    System.out.println("🔍 Intentando cargar desde URL: " + url.toString());
                    bi = javax.imageio.ImageIO.read(url);
                    if (bi != null && bi.getWidth() > 0 && bi.getHeight() > 0) {
                        rutaEncontrada = "classpath: " + url.toString();
                        System.out.println("✅ Imagen cargada desde classpath: " + recurso + " (" + bi.getWidth() + "x" + bi.getHeight() + ")");
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error al cargar desde classpath: " + e.getMessage());
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

        // Si no se encontró, devolver label vacío con mensaje de error
        System.err.println("❌ No se pudo cargar la imagen: " + recurso);
        System.err.println("   Directorio de trabajo: " + base);
        System.err.println("   Ruta esperada: " + base + java.io.File.separator + "src" + java.io.File.separator + "main" + java.io.File.separator + "resources" + java.io.File.separator + recurso);
        JLabel fallback = new JLabel(" ");
        fallback.setToolTipText("No se encontró la imagen: " + recurso + " (dir=" + base + ")");
        fallback.setOpaque(true);
        fallback.setBackground(Color.WHITE);
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

    private double leerTasaBCVDeConfig() {
        try {
            java.util.Properties p = new java.util.Properties();
            java.io.File f = new java.io.File(CONFIG_FILE);
            if (f.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) { p.load(fis); }
                String v = p.getProperty("tasa_bcv", "");
                if (v != null && !v.isBlank()) return Double.parseDouble(v.replace(",", "."));
            }
        } catch (Exception ignored) {}
        return 201.47; // valor por defecto si no hay config
    }

    private void guardarTasaBCVEnConfig(double tasa) {
        try {
            java.io.File dir = new java.io.File(CONFIG_DIR);
            if (!dir.exists()) dir.mkdirs();
            java.util.Properties p = new java.util.Properties();
            java.io.File f = new java.io.File(CONFIG_FILE);
            if (f.exists()) { try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) { p.load(fis); } catch (Exception ignored) {} }
            p.setProperty("tasa_bcv", String.format(java.util.Locale.US, "%.2f", tasa));
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(f)) { p.store(fos, "Configuración Medisoft"); }
        } catch (Exception ignored) {}
    }

    private void actualizarTasaInteractivo() {
        String actual = tasaManual.getText() != null ? tasaManual.getText().trim() : "";
        String resp = javax.swing.JOptionPane.showInputDialog(mainFrame, "Nueva tasa cambiaria (BCV):", actual.isEmpty() ? "201.47" : actual);
        if (resp == null || resp.trim().isEmpty()) return;
        try {
            double val = Double.parseDouble(resp.replace(",", "."));
            guardarTasaBCVEnConfig(val);
            tasaManual.setText(String.format("%.2f", val));
            tasaManual.setEnabled(false);
            javax.swing.JOptionPane.showMessageDialog(mainFrame, "Tasa actualizada a " + String.format("%.2f", val));
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(mainFrame, "Valor inválido.");
        }
    }
}