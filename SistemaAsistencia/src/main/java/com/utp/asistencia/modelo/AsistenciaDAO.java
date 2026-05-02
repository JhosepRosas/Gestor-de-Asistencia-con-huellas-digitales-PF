package com.utp.asistencia.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AsistenciaDAO {
    
    public boolean registrarAsistencia(int huellaId) {
        String sqlUsuario = "SELECT id FROM usuarios WHERE huella_id = ?";
        String sqlAsistencia = "INSERT INTO asistencias (usuario_id) VALUES (?)";
        
        try (Connection con = ConexionDB.conectar()) {
            // 1. Buscar ID del usuario por su huella
            int usuarioId = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                ps.setInt(1, huellaId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    usuarioId = rs.getInt("id");
                }
            }
            
            if (usuarioId == -1) return false;
            
            // 2. Insertar marcaje
            try (PreparedStatement ps = con.prepareStatement(sqlAsistencia)) {
                ps.setInt(1, usuarioId);
                return ps.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error al registrar asistencia: " + e.getMessage());
            return false;
        }
    }

    public String obtenerNombrePorHuella(int huellaId) {
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE huella_id = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, huellaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombres") + " " + rs.getString("apellidos");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener nombre: " + e.getMessage());
        }
        return "Desconocido";
    }
}
