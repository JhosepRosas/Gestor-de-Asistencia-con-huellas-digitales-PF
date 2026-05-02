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
    
    // Método para vincular el ID de la huella del Arduino con un alumno existente
    public boolean asignarHuella(String dni, int huellaId) {
        String sql = "UPDATE usuarios SET huella_id = ? WHERE dni = ?";
        
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, huellaId);
            ps.setString(2, dni);
            
            // executeUpdate() devuelve el número de filas afectadas. Si es > 0, se actualizó bien.
            return ps.executeUpdate() > 0; 
            
        } catch (SQLException e) {
            System.out.println("Error al asignar huella: " + e.getMessage());
            return false;
        }
    }    
}
