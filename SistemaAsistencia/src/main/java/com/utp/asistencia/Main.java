package com.utp.asistencia;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;

import com.utp.asistencia.modelo.ConexionDB;
import com.utp.asistencia.vista.FrmAsistencia;

public class Main {
    public static void main(String[] args) {
        // 1. Configurar FlatLaf (Tema Oscuro Moderno)
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 2. Inicializar Base de Datos (SQLite)
        ConexionDB.inicializarBaseDeDatos();
                
        // 3. Iniciar directamente en la ventana de Asistencia
        java.awt.EventQueue.invokeLater(() -> {
            new FrmAsistencia().setVisible(true);
        });
    }
}
