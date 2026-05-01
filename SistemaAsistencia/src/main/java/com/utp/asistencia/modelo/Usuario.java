package com.utp.asistencia.modelo;

/**
 *
 * @author Haskell
 */
public class Usuario {
    private int id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String rol;
    private int huella_id;
    
    // constructor vacio (pa hacer las consultas)
    public Usuario() {
    }
    
    // constructor sin ID (pa registrar nuevos usuarios, el ID ya lo pone sqlite)
    public Usuario(String nombres, String apellidos, String dni, String rol, int huella_id) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.rol = rol;
        this.huella_id = huella_id;
    }
    
    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getHuella_id() { return huella_id; }
    public void setHuella_id(int huella_id) { this.huella_id = huella_id; }    
}
