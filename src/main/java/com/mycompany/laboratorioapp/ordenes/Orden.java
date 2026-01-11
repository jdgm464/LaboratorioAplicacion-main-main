package com.mycompany.laboratorioapp.ordenes;

import java.util.List;

public class Orden {
    private String numeroOrden;
    private String numeroFactura;
    private String numeroControl;
    private String numeroLote;
    private String fechaRegistro;
    private String horaRegistro;
    private String codigoPaciente;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String direccion;
    private String telefono;
    private String correo;
    private String codigoEmpresa;
    private String empresa;
    private String sexo; // M o F
    private List<String> examenes;
    private double total;
    private String estatus = "Activo";

    public Orden(String numeroOrden, String numeroFactura, String numeroControl, String numeroLote,
                 String fechaRegistro, String horaRegistro, String codigoPaciente, String cedula,
                 String nombres, String apellidos, String direccion, String telefono, String correo,
                 String codigoEmpresa, String empresa, List<String> examenes, double total) {
        this(numeroOrden, numeroFactura, numeroControl, numeroLote, fechaRegistro, horaRegistro,
             codigoPaciente, cedula, nombres, apellidos, direccion, telefono, correo,
             codigoEmpresa, empresa, "", examenes, total);
    }

    public Orden(String numeroOrden, String numeroFactura, String numeroControl, String numeroLote,
                 String fechaRegistro, String horaRegistro, String codigoPaciente, String cedula,
                 String nombres, String apellidos, String direccion, String telefono, String correo,
                 String codigoEmpresa, String empresa, String sexo, List<String> examenes, double total) {
        this.numeroOrden = numeroOrden;
        this.numeroFactura = numeroFactura;
        this.numeroControl = numeroControl;
        this.numeroLote = numeroLote;
        this.fechaRegistro = fechaRegistro;
        this.horaRegistro = horaRegistro;
        this.codigoPaciente = codigoPaciente;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.codigoEmpresa = codigoEmpresa;
        this.empresa = empresa;
        this.sexo = sexo != null ? sexo : "";
        this.examenes = examenes;
        this.total = total;
        this.estatus = "Activo";
    }

    // =================== GETTERS ===================
    public String getNumeroOrden() { return numeroOrden; }
    public String getNumeroFactura() { return numeroFactura; }
    public String getNumeroControl() { return numeroControl; }
    public String getNumeroLote() { return numeroLote; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getHoraRegistro() { return horaRegistro; }
    public String getCodigoPaciente() { return codigoPaciente; }
    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getCodigoEmpresa() { return codigoEmpresa; }
    public String getEmpresa() { return empresa; }
    public String getSexo() { return sexo != null ? sexo : ""; }
    public List<String> getExamenes() { return examenes; }
    public double getTotal() { return total; }
    public String getEstatus() { return estatus; }

    // =================== SETTERS opcionales ===================
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setHoraRegistro(String horaRegistro) { this.horaRegistro = horaRegistro; }
    public void setCodigoPaciente(String codigoPaciente) { this.codigoPaciente = codigoPaciente; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setCodigoEmpresa(String codigoEmpresa) { this.codigoEmpresa = codigoEmpresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public void setSexo(String sexo) { this.sexo = sexo != null ? sexo : ""; }
    public void setExamenes(List<String> examenes) { this.examenes = examenes; }
    public void setTotal(double total) { this.total = total; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    // Devuelve los datos de la orden en formato de fila para la tabla
    public Object[] toRow() {
        return new Object[] {
            numeroOrden, numeroFactura, numeroControl, numeroLote,
            fechaRegistro, horaRegistro, codigoPaciente, cedula,
            nombres, apellidos, direccion, telefono, correo,
            codigoEmpresa, empresa, estatus
        };
    }
}

