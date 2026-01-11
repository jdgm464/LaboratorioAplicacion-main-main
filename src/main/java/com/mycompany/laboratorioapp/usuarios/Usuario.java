package com.mycompany.laboratorioapp.usuarios;

public class Usuario {
    private final String rol; // Equivale a "Perfil" en UI
    private final String usuario;
    private final String password;

    // Datos personales
    private final String cedula;
    private final String nombres;
    private final String apellidos;

    // Módulos
    private final boolean moduloVentasLaboratorio;
    private final boolean moduloCompras;
    private final boolean moduloAdministrativos;

    // Autorizaciones
    private final boolean puedeDescuento;
    private final boolean puedeRecargo;
    private final boolean puedeCierre;
    private final boolean puedeCortesias;
    private final boolean puedeControlPrecios;
    private final boolean puedeControlResultados;
    private final boolean puedeAnular;
    private final boolean puedeModificarOrdenes;
    private final boolean permisosAdministrativos;

    // Constructor clásico para compatibilidad con el código existente
    public Usuario(String rol, String usuario, String password) {
        this.rol = rol;
        this.usuario = usuario;
        this.password = password;

        // Valores por defecto para los nuevos campos
        this.cedula = "";
        this.nombres = "";
        this.apellidos = "";
        this.moduloVentasLaboratorio = false;
        this.moduloCompras = false;
        this.moduloAdministrativos = false;
        this.puedeDescuento = false;
        this.puedeRecargo = false;
        this.puedeCierre = false;
        this.puedeCortesias = false;
        this.puedeControlPrecios = false;
        this.puedeControlResultados = false;
        this.puedeAnular = false;
        this.puedeModificarOrdenes = false;
        this.permisosAdministrativos = false;
    }

    // Nuevo constructor extendido para cubrir lo mostrado en la imagen
    public Usuario(
            String cedula,
            String nombres,
            String apellidos,
            String usuario,
            String password,
            String rol,
            boolean moduloVentasLaboratorio,
            boolean moduloCompras,
            boolean moduloAdministrativos,
            boolean puedeDescuento,
            boolean puedeRecargo,
            boolean puedeCierre,
            boolean puedeCortesias,
            boolean puedeControlPrecios,
            boolean puedeControlResultados,
            boolean puedeAnular,
            boolean puedeModificarOrdenes,
            boolean permisosAdministrativos
    ) {
        this.rol = rol;
        this.usuario = usuario;
        this.password = password;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.moduloVentasLaboratorio = moduloVentasLaboratorio;
        this.moduloCompras = moduloCompras;
        this.moduloAdministrativos = moduloAdministrativos;
        this.puedeDescuento = puedeDescuento;
        this.puedeRecargo = puedeRecargo;
        this.puedeCierre = puedeCierre;
        this.puedeCortesias = puedeCortesias;
        this.puedeControlPrecios = puedeControlPrecios;
        this.puedeControlResultados = puedeControlResultados;
        this.puedeAnular = puedeAnular;
        this.puedeModificarOrdenes = puedeModificarOrdenes;
        this.permisosAdministrativos = permisosAdministrativos;
    }

    public String getRol() {
        return rol;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public boolean hasModuloVentasLaboratorio() {
        return moduloVentasLaboratorio;
    }

    public boolean hasModuloCompras() {
        return moduloCompras;
    }

    public boolean hasModuloAdministrativos() {
        return moduloAdministrativos;
    }

    public boolean isPuedeDescuento() {
        return puedeDescuento;
    }

    public boolean isPuedeRecargo() {
        return puedeRecargo;
    }

    public boolean isPuedeCierre() {
        return puedeCierre;
    }

    public boolean isPuedeCortesias() {
        return puedeCortesias;
    }

    public boolean isPuedeControlPrecios() {
        return puedeControlPrecios;
    }

    public boolean isPuedeControlResultados() {
        return puedeControlResultados;
    }

    public boolean isPuedeAnular() {
        return puedeAnular;
    }

    public boolean isPuedeModificarOrdenes() {
        return puedeModificarOrdenes;
    }

    public boolean isPermisosAdministrativos() {
        return permisosAdministrativos;
    }
}

