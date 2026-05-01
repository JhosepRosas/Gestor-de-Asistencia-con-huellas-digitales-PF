package com.utp.asistencia.modelo;
/**
 *
 * @author Haskell
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDAO {
    
    // metodo para insertar nuevo usuario en la tabla
    public boolean registrar(Usuario usr) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, dni, rol, huella_id) VALUES (?, ?, ?, ?, ?)";
        
        // el bloque try cierra la conexion
        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usr.getNombres());
            ps.setString(2, usr.getApellidos());
            ps.setString(3, usr.getDni());
            ps.setString(4, usr.getRol());
            ps.setInt(5, usr.getHuella_id());
            
            ps.execute();
            return true; // retorna true si se guardo con exito
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false; // retorna false si hubo un problema como un duplicado
        }
    }
}
