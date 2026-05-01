package com.utp.asistencia;

/**
 *
 * @author Haskell
 */

import com.utp.asistencia.modelo.ConexionDB;

public class Main {
    public static void main(String[] args) {
        // al arrancar el programa, verificamos y creamos las tablas si no existen
        System.out.println("Iniciando Sistema de Asistencia...");
        ConexionDB.inicializarBaseDeDatos();
    }
    
}
