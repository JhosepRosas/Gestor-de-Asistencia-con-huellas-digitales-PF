package com.utp.asistencia.modelo;

/**
 *
 * @author Haskell
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    
    // nombre del archivo de la base de datos
    private static final String URL = "jdbc:sqlite:asistencia_utp.db";
    
    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexion a SQLite establecido.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a SQLite: " + e.getMessage());
        }
        return conexion;
    }
    
    // metodo para crear las tablas si el archivo es nuevo
    public static void inicializarBaseDeDatos() {
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombres TEXT NOT NULL,"
                + " apellidos TEXT NOT NULL,"
                + " dni TEXT NOT NULL UNIQUE,"
                + " rol TEXT NOT NULL,"
                + " huella_id INTEGER" // aqui guardaremos el ID que manda el Arduino
                + ");";
        
        String sqlAsistencias = "CREATE TABLE IF NOT EXISTS asistencias ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " usuario_id INTEGER NOT NULL,"
                + " fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + " FOREIGN KEY (usuario_id) REFERENCES usuarios(id)"
                + ");";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlAsistencias);
            System.out.println("Tablas verificadas/creadas correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
    
}
