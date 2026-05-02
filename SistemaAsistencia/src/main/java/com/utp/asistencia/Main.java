package com.utp.asistencia;

import com.utp.asistencia.modelo.ConexionDB;
import com.utp.asistencia.vista.FrmAsistencia;
import javax.swing.UIManager;

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
        
        // 3. Iniciar en modo Asistencia (Verificación continua)
        java.awt.EventQueue.invokeLater(() -> {
            FrmAsistencia ventana = new FrmAsistencia();
            ventana.setVisible(true);
        });
    }
}
