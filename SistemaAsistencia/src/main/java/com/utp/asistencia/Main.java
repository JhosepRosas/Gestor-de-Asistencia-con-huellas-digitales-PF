package com.utp.asistencia;

/**
 *
 * @author Haskell
 */

import com.utp.asistencia.modelo.ConexionDB;
import com.utp.asistencia.modelo.Usuario;
import com.utp.asistencia.modelo.UsuarioDAO;

public class Main {
    public static void main(String[] args) {
        // al arrancar el programa, verificamos y creamos las tablas si no existen
        System.out.println("Iniciando Sistema de Asistencia...");
        ConexionDB.inicializarBaseDeDatos();
        
        // creamos un objeto de prueba
        // huella_id por el momento 0, luego se actualiza con el arduino
        Usuario nuevoUsuario = new Usuario("Jordi", "Cusi Ayala", "71042143", "Alumno", 0);
        
        // llamamos al DAO para que guarde DAAH
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.registrar(nuevoUsuario)) {
            System.out.println("Exito, Usuario guardado en la base de datos.");
        } else {
            System.out.println("Error al guardad. quizas existe el DNI");
        }
    }
}
