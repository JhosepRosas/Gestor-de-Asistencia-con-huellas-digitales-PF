package com.utp.asistencia.vista;

import com.utp.asistencia.modelo.AsistenciaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmReporteAsistencia extends JFrame {
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private AsistenciaDAO dao;

    public FrmReporteAsistencia() {
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        cargarReporte();
    }

    private void configurarVentana() {
        setTitle("Reporte de Asistencias - UTP");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Historial de Asistencias", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{"Alumno", "Curso", "Fecha y Hora"}, 0);
        tablaReporte = new JTable(modeloTabla);
        add(new JScrollPane(tablaReporte), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarReporte());
        add(btnActualizar, BorderLayout.SOUTH);
    }

    private void cargarReporte() {
        modeloTabla.setRowCount(0);
        List<String[]> data = dao.listarAsistenciasDetalladas();
        for (String[] fila : data) {
            modeloTabla.addRow(fila);
        }
    }
}
