package com.utp.asistencia.modelo;
/**
 *
 * @author Haskell
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    
    // Método para validar login
    public Usuario login(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setDni(rs.getString("dni"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listarAlumnos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'ALUMNO'";
        try (Connection con = ConexionDB.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setDni(rs.getString("dni"));
                u.setHuella_id(rs.getInt("huella_id"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error listar alumnos: " + e.getMessage());
        }
        return lista;
    }

    public boolean asignarHuella(String dni, int huellaId) {
        String sql = "UPDATE usuarios SET huella_id = ? WHERE dni = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, huellaId);
            ps.setString(2, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al asignar huella: " + e.getMessage());
            return false;
        }
    }
}
