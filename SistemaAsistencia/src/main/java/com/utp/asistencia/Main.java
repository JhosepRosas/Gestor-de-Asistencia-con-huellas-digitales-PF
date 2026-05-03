package com.utp.asistencia;

import javax.swing.UIManager;

import com.utp.asistencia.modelo.ConexionDB;
import com.utp.asistencia.vista.FrmAsistencia;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializar Base de Datos (SQLite)
        ConexionDB.inicializarBaseDeDatos();
        
        // 2. Establecer Look and Feel del sistema para que se vea moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. Iniciar directamente en la ventana de Asistencia
        java.awt.EventQueue.invokeLater(() -> {
            new FrmAsistencia().setVisible(true);
        });
    }
}
