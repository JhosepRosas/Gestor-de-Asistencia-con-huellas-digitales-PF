package com.utp.asistencia.controlador;

/**
 *
 * @author Haskell
 */
import com.utp.asistencia.modelo.Usuario;
import com.utp.asistencia.modelo.UsuarioDAO;

public class UsuarioControlador {
    
    private UsuarioDAO dao;
    
    // constructor
    public UsuarioControlador() {
        this.dao = new UsuarioDAO();
    }
    
    // metodo que recibe los datos de la ventana y hace la logica
    public boolean procesarRegistro(String nombres, String apellidos, String dni, String rol) {
        // validacion basica para que no envien campos vacios
        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            return false;
        }
        
        // creamos el objeto usuario. el huella_id va en 0 porque aun no pasa por el sensor
        Usuario nuevoUsuario = new Usuario(nombres, apellidos, dni, rol, 0);
        
        // le decimos la DAO que lo guarde en SQLite
        return dao.registrar(nuevoUsuario);
    }
}
