package com.utp.asistencia.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AsistenciaDAO {
    
    public String[] obtenerCursoActual() {
        String[] curso = null;
        String sql = "SELECT c.id, c.nombre FROM cursos c " +
                     "JOIN horarios h ON c.id = h.curso_id " +
                     "WHERE h.dia = ? AND ? BETWEEN h.hora_inicio AND h.hora_fin";
        
        String[] diasES = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String diaActual = diasES[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
        String horaActual = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, diaActual);
            ps.setString(2, horaActual);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                curso = new String[]{String.valueOf(rs.getInt("id")), rs.getString("nombre")};
            }
        } catch (SQLException e) {
            System.out.println("Error obtener curso actual: " + e.getMessage());
        }
        return curso;
    }

    public boolean registrarAsistencia(int huellaId, int cursoId) {
        String sqlUsuario = "SELECT id FROM usuarios WHERE huella_id = ?";
        String sqlAsistencia = "INSERT INTO asistencias (usuario_id, curso_id) VALUES (?, ?)";
        
        try (Connection con = ConexionDB.conectar()) {
            int usuarioId = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                ps.setInt(1, huellaId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    usuarioId = rs.getInt("id");
                }
            }
            
            if (usuarioId == -1) return false;
            
            try (PreparedStatement ps = con.prepareStatement(sqlAsistencia)) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, cursoId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar asistencia: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listarCursos() {
        List<String[]> cursos = new java.util.ArrayList<>();
        String sql = "SELECT id, nombre FROM cursos";
        try (Connection con = ConexionDB.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                cursos.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("nombre")});
            }
        } catch (SQLException e) {
            System.out.println("Error listar cursos: " + e.getMessage());
        }
        return cursos;
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

    public boolean registrarCurso(String nombre, String codigo) {
        String sql = "INSERT INTO cursos (nombre, codigo) VALUES (?, ?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registrar curso: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listarAsistenciasDetalladas() {
        List<String[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT u.nombres || ' ' || u.apellidos as usuario, c.nombre as curso, a.fecha_hora " +
                     "FROM asistencias a " +
                     "JOIN usuarios u ON a.usuario_id = u.id " +
                     "JOIN cursos c ON a.curso_id = c.id " +
                     "ORDER BY a.fecha_hora DESC";
        try (Connection con = ConexionDB.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("usuario"), 
                    rs.getString("curso"), 
                    rs.getString("fecha_hora")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error listar asistencias: " + e.getMessage());
        }
        return lista;
    }
}
