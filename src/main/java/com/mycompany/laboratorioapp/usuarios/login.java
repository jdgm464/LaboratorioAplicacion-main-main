package com.mycompany.laboratorioapp.usuarios;

import com.mycompany.laboratorioapp.dao.UsuarioDAO;
import com.mycompany.laboratorioapp.InterfazPrincipal;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class login {
    private final JFrame frame;
    private final JTextField usuarioField;
    private final JPasswordField passwordField;
    private final JComboBox<String> rolCombo;

    // Simulamos una base de datos en memoria
    private static final Map<String, Usuario> usuariosRegistrados = new HashMap<>();
    private static final String USUARIOS_FILE = System.getProperty("user.home")
            + File.separator + ".laboratorioapp" + File.separator + "usuarios.properties";

    // Cargar usuarios al iniciar la clase
    static {
        cargarUsuarios();
    }

    public login() {
        // Si no hay usuarios cargados o el archivo no existe, crear usuarios por defecto
        if (usuariosRegistrados.isEmpty()) {
            usuariosRegistrados.put("admin", new Usuario("00000000", "Administrador", "Sistema", "admin", "admin123", "Administrador",
                    true, true, true, false, false, false, false, true, false, false, false, true));
            usuariosRegistrados.put("bio1", new Usuario("", "Bio", "Analista", "bio1", "1234", "Bioanalista",
                    false, false, false, false, false, false, false, false, false, false, false, false));
            usuariosRegistrados.put("jose", new Usuario("", "Jose", "Perez", "jose", "1234", "Administrador",
                    false, false, false, false, false, false, false, false, false, false, false, false));
            guardarUsuarios(); // Guardar usuarios por defecto
            
            // Intentar guardar usuarios en BD también
            try {
                for (Usuario u : usuariosRegistrados.values()) {
                    Usuario existente = UsuarioDAO.buscarPorUsuario(u.getUsuario());
                    if (existente == null) {
                        UsuarioDAO.insertar(u);
                    }
                }
            } catch (Exception e) {
                System.err.println("No se pudieron guardar usuarios en BD (esto es normal si la BD no está configurada): " + e.getMessage());
            }
        }

        frame = new JFrame("Login - Medisoft");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 260);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usuarioField = new JTextField(15);
        frame.add(usuarioField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        frame.add(passwordField, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Bioanalista"});
        frame.add(rolCombo, gbc);

        // Botón iniciar sesión
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(e -> autenticar());
        frame.add(loginButton, gbc);

        // Botón crear cuenta
        gbc.gridy = 4;
        JButton registroButton = new JButton("Crear Cuenta");
        registroButton.addActionListener(e -> {
            // Crear registro simple (sin ventana padre ya que viene del login)
            new RegistroSimple();
        });
        frame.add(registroButton, gbc);

        frame.setVisible(true);
    }

    private void autenticar() {
        String usuario = usuarioField.getText();
        String password = new String(passwordField.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        // Intentar autenticar desde la base de datos primero
        Usuario u = null;
        try {
            u = UsuarioDAO.buscarPorCredenciales(usuario, password);
            // Si se encuentra en BD, verificar el rol
            if (u != null && !u.getRol().equals(rol)) {
                u = null; // El rol no coincide
            }
        } catch (Exception e) {
            // Si hay error con BD, intentar con usuarios en memoria como fallback
            System.err.println("Error al buscar en BD, usando fallback: " + e.getMessage());
        }
        
        // Fallback: buscar en usuarios en memoria
        if (u == null) {
            u = usuariosRegistrados.get(usuario);
            if (u != null && (!u.getPassword().equals(password) || !u.getRol().equals(rol))) {
                u = null;
            }
        }

        if (u != null) {
            // Si el usuario existe en memoria pero no en BD, guardarlo en BD
            try {
                Usuario usuarioBD = UsuarioDAO.buscarPorUsuario(usuario);
                if (usuarioBD == null) {
                    // El usuario no está en BD, intentar guardarlo
                    UsuarioDAO.insertar(u);
                }
            } catch (Exception e) {
                System.err.println("No se pudo guardar usuario en BD: " + e.getMessage());
            }
            
            abrirInterfaz(usuario, rol);
        } else {
            JOptionPane.showMessageDialog(frame, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirInterfaz(String usuario, String rol) {
        // Establecer el usuario en la sesión
        SesionUsuario.establecerUsuario(usuario);
        frame.dispose();
        SwingUtilities.invokeLater(() -> new InterfazPrincipal(usuario, rol).mostrar());
    }

    // Método legacy (mantener compatibilidad). Guarda sólo con nombre como "nombres".
    public static void registrarUsuario(String nombre, String usuario, String password, String rol) {
        registrarUsuario(nombre, "", usuario, password, rol);
    }

    // Nuevo método con nombres y apellidos para reflejarse en la tabla
    public static void registrarUsuario(String nombres, String apellidos, String usuario, String password, String rol) {
        Usuario u = new Usuario(
                "", // cédula vacía en registro simple
                nombres,
                apellidos,
                usuario,
                password,
                rol,
                false, false, false, // módulos
                false, false, false, false, // autorizaciones 1
                false, false, // control precios/resultados
                false, false, // anular / modificar ordenes
                false // permisos administrativos
        );
        usuariosRegistrados.put(usuario, u);
        guardarUsuarios(); // Persistir cambios
    }

    // Métodos extendidos para la nueva ventana de registro
    public static void registrarUsuarioExtendido(Usuario u) {
        if (u == null) return;
        usuariosRegistrados.put(u.getUsuario(), u);
        guardarUsuarios(); // Persistir cambios
    }

    public static void actualizarUsuario(Usuario u) {
        if (u == null) return;
        usuariosRegistrados.put(u.getUsuario(), u);
        guardarUsuarios(); // Persistir cambios
    }

    public static boolean eliminarUsuario(String usuario) {
        boolean eliminado = usuariosRegistrados.remove(usuario) != null;
        if (eliminado) {
            guardarUsuarios(); // Persistir cambios
        }
        return eliminado;
    }

    // Consultas para el listado en VentanaRegistroUsuarios
    public static java.util.List<Usuario> listarUsuarios() {
        // Primero intentar cargar desde la base de datos
        java.util.List<Usuario> usuariosBD = com.mycompany.laboratorioapp.dao.UsuarioDAO.obtenerTodos();
        if (!usuariosBD.isEmpty()) {
            return usuariosBD;
        }
        // Si no hay en BD, usar el sistema de archivos para compatibilidad
        return new java.util.ArrayList<>(usuariosRegistrados.values());
    }

    public static Usuario buscarUsuarioPorNombre(String username) {
        // Primero buscar en la base de datos
        Usuario usuarioBD = com.mycompany.laboratorioapp.dao.UsuarioDAO.buscarPorUsuario(username);
        if (usuarioBD != null) {
            return usuarioBD;
        }
        // Si no está en BD, buscar en el sistema de archivos para compatibilidad
        return usuariosRegistrados.get(username);
    }

    // Cargar usuarios desde archivo Properties
    private static void cargarUsuarios() {
        try {
            File f = new File(USUARIOS_FILE);
            if (!f.exists()) {
                return; // No hay archivo, se crearán usuarios por defecto
            }
            
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
            }
            
            int count = Integer.parseInt(p.getProperty("count", "0"));
            usuariosRegistrados.clear();
            
            for (int i = 0; i < count; i++) {
                String prefix = "u." + i + ".";
                String usuario = p.getProperty(prefix + "usuario", "");
                if (usuario.isEmpty()) continue;
                
                Usuario u = new Usuario(
                    p.getProperty(prefix + "cedula", ""),
                    p.getProperty(prefix + "nombres", ""),
                    p.getProperty(prefix + "apellidos", ""),
                    usuario,
                    p.getProperty(prefix + "password", ""),
                    p.getProperty(prefix + "rol", ""),
                    Boolean.parseBoolean(p.getProperty(prefix + "modVentasLab", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "modCompras", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "modAdministrativos", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeDescuento", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeRecargo", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeCierre", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeCortesias", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeControlPrecios", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeControlResultados", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeAnular", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "puedeModificarOrdenes", "false")),
                    Boolean.parseBoolean(p.getProperty(prefix + "permisosAdministrativos", "false"))
                );
                usuariosRegistrados.put(usuario, u);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    // Guardar usuarios en archivo Properties
    private static void guardarUsuarios() {
        try {
            File f = new File(USUARIOS_FILE);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            
            Properties p = new Properties();
            p.setProperty("count", String.valueOf(usuariosRegistrados.size()));
            
            int index = 0;
            for (Usuario u : usuariosRegistrados.values()) {
                String prefix = "u." + index + ".";
                p.setProperty(prefix + "cedula", u.getCedula() != null ? u.getCedula() : "");
                p.setProperty(prefix + "nombres", u.getNombres() != null ? u.getNombres() : "");
                p.setProperty(prefix + "apellidos", u.getApellidos() != null ? u.getApellidos() : "");
                p.setProperty(prefix + "usuario", u.getUsuario() != null ? u.getUsuario() : "");
                p.setProperty(prefix + "password", u.getPassword() != null ? u.getPassword() : "");
                p.setProperty(prefix + "rol", u.getRol() != null ? u.getRol() : "");
                p.setProperty(prefix + "modVentasLab", String.valueOf(u.hasModuloVentasLaboratorio()));
                p.setProperty(prefix + "modCompras", String.valueOf(u.hasModuloCompras()));
                p.setProperty(prefix + "modAdministrativos", String.valueOf(u.hasModuloAdministrativos()));
                p.setProperty(prefix + "puedeDescuento", String.valueOf(u.isPuedeDescuento()));
                p.setProperty(prefix + "puedeRecargo", String.valueOf(u.isPuedeRecargo()));
                p.setProperty(prefix + "puedeCierre", String.valueOf(u.isPuedeCierre()));
                p.setProperty(prefix + "puedeCortesias", String.valueOf(u.isPuedeCortesias()));
                p.setProperty(prefix + "puedeControlPrecios", String.valueOf(u.isPuedeControlPrecios()));
                p.setProperty(prefix + "puedeControlResultados", String.valueOf(u.isPuedeControlResultados()));
                p.setProperty(prefix + "puedeAnular", String.valueOf(u.isPuedeAnular()));
                p.setProperty(prefix + "puedeModificarOrdenes", String.valueOf(u.isPuedeModificarOrdenes()));
                p.setProperty(prefix + "permisosAdministrativos", String.valueOf(u.isPermisosAdministrativos()));
                index++;
            }
            
            try (FileOutputStream fos = new FileOutputStream(f)) {
                p.store(fos, "Usuarios persistidos");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}

