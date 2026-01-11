package com.mycompany.laboratorioapp.usuarios;

import com.mycompany.laboratorioapp.dao.UsuarioDAO;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class Registro {
    private final JFrame frame;
    private final JTextField cedulaField;
    private final JTextField nombresField;
    private final JTextField apellidosField;
    private final JTextField usuarioField;
    private final JPasswordField passwordField;
    private final JComboBox<String> rolCombo;

    // Módulos
    private final JCheckBox modVentasLab;
    private final JCheckBox modCompras;
    private final JCheckBox modAdministrativos;

    // Autorizaciones
    private final JCheckBox autDescuento;
    private final JCheckBox autRecargo;
    private final JCheckBox autCierre;
    private final JCheckBox autCortesias;
    private final JCheckBox autControlPrecios;
    private final JCheckBox autControlResultados;
    private final JCheckBox autAnular;
    private final JCheckBox autModificarOrdenes;
    private final JCheckBox autPermisosAdministrativos;
    private final boolean esEdicion;
    private VentanaRegistroUsuarios ventanaPadre;

    public Registro() {
        this(null);
    }

    public Registro(Usuario existente) {

        frame = new JFrame("Registros de Usuarios");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(680, 520);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int colLabel = 0;
        int colField = 1;

        // Cédula
        gbc.gridx = colLabel; gbc.gridy = 0;
        frame.add(new JLabel("Cédula"), gbc);
        gbc.gridx = colField;
        cedulaField = new JTextField(18);
        frame.add(cedulaField, gbc);

        // Nombres
        gbc.gridx = colLabel; gbc.gridy = 1;
        frame.add(new JLabel("Nombres"), gbc);
        gbc.gridx = colField;
        nombresField = new JTextField(18);
        frame.add(nombresField, gbc);

        // Apellidos
        gbc.gridx = colLabel; gbc.gridy = 2;
        frame.add(new JLabel("Apellidos"), gbc);
        gbc.gridx = colField;
        apellidosField = new JTextField(18);
        frame.add(apellidosField, gbc);

        // Usuario
        gbc.gridx = colLabel; gbc.gridy = 3;
        frame.add(new JLabel("Usuario"), gbc);
        gbc.gridx = colField;
        usuarioField = new JTextField(15);
        frame.add(usuarioField, gbc);

        // Contraseña
        gbc.gridx = colLabel; gbc.gridy = 4;
        frame.add(new JLabel("Clave"), gbc);
        gbc.gridx = colField;
        passwordField = new JPasswordField(15);
        frame.add(passwordField, gbc);

        // Rol
        gbc.gridx = colLabel; gbc.gridy = 5;
        frame.add(new JLabel("Perfil"), gbc);
        gbc.gridx = colField;
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Bioanalista", "Usuario"});
        frame.add(rolCombo, gbc);

        // Panel de módulos
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel modPanel = new JPanel(new GridBagLayout());
        modPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Módulos"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(4, 6, 4, 6);
        g2.anchor = GridBagConstraints.WEST;
        modVentasLab = new JCheckBox("Ventas y Laboratorio");
        modCompras = new JCheckBox("Compras");
        modAdministrativos = new JCheckBox("Administrativos");
        g2.gridx = 0; g2.gridy = 0; modPanel.add(modVentasLab, g2);
        g2.gridx = 1; g2.gridy = 0; modPanel.add(modCompras, g2);
        g2.gridx = 2; g2.gridy = 0; modPanel.add(modAdministrativos, g2);
        frame.add(modPanel, gbc);

        // Panel de autorizaciones
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel autPanel = new JPanel(new GridBagLayout());
        autPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Autorizado a"));
        GridBagConstraints g3 = new GridBagConstraints();
        g3.insets = new Insets(4, 6, 4, 6);
        g3.anchor = GridBagConstraints.WEST;
        autDescuento = new JCheckBox("Descuento");
        autRecargo = new JCheckBox("Recargo");
        autCierre = new JCheckBox("Cierre");
        autCortesias = new JCheckBox("Cortesías");
        autControlPrecios = new JCheckBox("Control de Precios");
        autControlResultados = new JCheckBox("Control de Resultados");
        autAnular = new JCheckBox("Anular");
        autModificarOrdenes = new JCheckBox("Modificar en Órdenes");
        autPermisosAdministrativos = new JCheckBox("Permisos Administrativos");
        int r = 0; int c = 0;
        JCheckBox[] boxes = new JCheckBox[]{autDescuento, autRecargo, autCierre, autCortesias, autControlPrecios, autControlResultados, autAnular, autModificarOrdenes, autPermisosAdministrativos};
        for (JCheckBox cb : boxes) {
            g3.gridx = c; g3.gridy = r; autPanel.add(cb, g3);
            c++;
            if (c == 3) { c = 0; r++; }
        }
        frame.add(autPanel, gbc);

        // Botones inferiores
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JPanel acciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 4));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnBorrar = new JButton("Borrar");
        JButton btnRegresar = new JButton("Regresar");
        acciones.add(btnGuardar);
        acciones.add(btnActualizar);
        acciones.add(btnBorrar);
        acciones.add(btnRegresar);
        frame.add(acciones, gbc);

        // Acciones
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnBorrar.addActionListener(e -> borrarUsuario());
        btnRegresar.addActionListener(e -> frame.dispose());

        // Si estamos en modo edición, prellenar
        this.esEdicion = (existente != null);
        if (existente != null) {
            precargarUsuario(existente);
        } else {
            // valor por defecto del flag en creación
        }

        frame.setVisible(true);
    }

    private Usuario buildUsuario() {
        String cedula = cedulaField.getText().trim();
        String nombres = nombresField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String password = new String(passwordField.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Complete los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Usuario(
                cedula,
                nombres,
                apellidos,
                usuario,
                password,
                rol,
                modVentasLab.isSelected(),
                modCompras.isSelected(),
                modAdministrativos.isSelected(),
                autDescuento.isSelected(),
                autRecargo.isSelected(),
                autCierre.isSelected(),
                autCortesias.isSelected(),
                autControlPrecios.isSelected(),
                autControlResultados.isSelected(),
                autAnular.isSelected(),
                autModificarOrdenes.isSelected(),
                autPermisosAdministrativos.isSelected()
        );
    }

    private void guardarUsuario() {
        Usuario u = buildUsuario();
        if (u == null) return;
        
        // Guardar en la base de datos
        boolean guardado = UsuarioDAO.insertar(u);
        
        if (guardado) {
            // También guardar en el sistema de archivos para compatibilidad
            login.registrarUsuarioExtendido(u);
            JOptionPane.showMessageDialog(frame, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Refrescar la ventana de registro de usuarios si está abierta
            if (ventanaPadre != null) {
                ventanaPadre.refrescarTabla();
            }
            
            // NO limpiar campos - mantener los datos
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Error al registrar el usuario en la base de datos.\n" +
                "Verifique que el usuario no exista ya.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarUsuario() {
        Usuario u = buildUsuario();
        if (u == null) return;
        
        // Actualizar en la base de datos
        boolean actualizado = UsuarioDAO.actualizar(u);
        
        if (actualizado) {
            // También actualizar en el sistema de archivos para compatibilidad
            login.actualizarUsuario(u);
            JOptionPane.showMessageDialog(frame, "Usuario actualizado", "Información", JOptionPane.INFORMATION_MESSAGE);
            
            // Refrescar la ventana de registro de usuarios si está abierta
            if (ventanaPadre != null) {
                ventanaPadre.refrescarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Error al actualizar el usuario en la base de datos.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarUsuario() {
        String usuario = usuarioField.getText().trim();
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Indique el usuario a borrar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean ok = login.eliminarUsuario(usuario);
        if (ok) {
            JOptionPane.showMessageDialog(frame, "Usuario borrado", "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Usuario no encontrado", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void precargarUsuario(Usuario u) {
        cedulaField.setText(u.getCedula());
        nombresField.setText(u.getNombres());
        apellidosField.setText(u.getApellidos());
        usuarioField.setText(u.getUsuario());
        passwordField.setText(u.getPassword());
        rolCombo.setSelectedItem(u.getRol());
        modVentasLab.setSelected(u.hasModuloVentasLaboratorio());
        modCompras.setSelected(u.hasModuloCompras());
        modAdministrativos.setSelected(u.hasModuloAdministrativos());
        autDescuento.setSelected(u.isPuedeDescuento());
        autRecargo.setSelected(u.isPuedeRecargo());
        autCierre.setSelected(u.isPuedeCierre());
        autCortesias.setSelected(u.isPuedeCortesias());
        autControlPrecios.setSelected(u.isPuedeControlPrecios());
        autControlResultados.setSelected(u.isPuedeControlResultados());
        autAnular.setSelected(u.isPuedeAnular());
        autModificarOrdenes.setSelected(u.isPuedeModificarOrdenes());
        autPermisosAdministrativos.setSelected(u.isPermisosAdministrativos());
    }
    
    public void setVentanaPadre(VentanaRegistroUsuarios ventanaPadre) {
        this.ventanaPadre = ventanaPadre;
    }
}

