package com.utp.asistencia.modelo;

/**
 *
 * @author Haskell
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
                + " username TEXT UNIQUE,"
                + " password TEXT,"
                + " rol TEXT NOT NULL," // ADMIN, DOCENTE, ALUMNO
                + " huella_id INTEGER"
                + ");";
        
        String sqlCursos = "CREATE TABLE IF NOT EXISTS cursos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombre TEXT NOT NULL,"
                + " codigo TEXT UNIQUE NOT NULL"
                + ");";

        String sqlHorarios = "CREATE TABLE IF NOT EXISTS horarios ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " curso_id INTEGER NOT NULL,"
                + " dia TEXT NOT NULL," // Lunes, Martes, etc.
                + " hora_inicio TEXT NOT NULL,"
                + " hora_fin TEXT NOT NULL,"
                + " FOREIGN KEY (curso_id) REFERENCES cursos(id)"
                + ");";
        
        String sqlAsistencias = "CREATE TABLE IF NOT EXISTS asistencias ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " usuario_id INTEGER NOT NULL,"
                + " curso_id INTEGER,"
                + " fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + " FOREIGN KEY (usuario_id) REFERENCES usuarios(id),"
                + " FOREIGN KEY (curso_id) REFERENCES cursos(id)"
                + ");";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            
            // Si la tabla ya existía de antes, faltarán estas columnas. Las agregamos por si acaso:
            try { stmt.execute("ALTER TABLE usuarios ADD COLUMN username TEXT UNIQUE"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE usuarios ADD COLUMN password TEXT"); } catch (Exception e) {}
            
            stmt.execute(sqlCursos);
            stmt.execute(sqlHorarios);
            stmt.execute(sqlAsistencias);
            
            // Insertar admin por defecto si no existe
            String sqlCheckAdmin = "SELECT COUNT(*) FROM usuarios WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(sqlCheckAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO usuarios (nombres, apellidos, dni, username, password, rol) "
                        + "VALUES ('Administrador', 'Sistema', '00000000', 'admin', 'admin123', 'ADMIN')");
                stmt.execute("INSERT INTO usuarios (nombres, apellidos, dni, username, password, rol) "
                        + "VALUES ('Docente', 'Prueba', '11111111', 'docente', 'docente123', 'DOCENTE')");
            }
            
            System.out.println("Base de datos estructurada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al estructurar BD: " + e.getMessage());
        }
    }
    
}
