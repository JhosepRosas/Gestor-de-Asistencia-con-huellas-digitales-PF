package com.utp.asistencia.vista;

import com.utp.asistencia.modelo.Usuario;
import com.utp.asistencia.modelo.UsuarioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmGestionAlumnos extends JFrame {
    private JTable tablaAlumnos;
    private DefaultTableModel modeloTabla;
    private UsuarioDAO dao;
    private JButton btnRefrescar, btnEnrolar, btnVolver;

    public FrmGestionAlumnos() {
        dao = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Alumnos");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "DNI", "Huella ID"}, 0);
        tablaAlumnos = new JTable(modeloTabla);
        add(new JScrollPane(tablaAlumnos), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        btnRefrescar = new JButton("Refrescar");
        btnEnrolar = new JButton("Enrolar Huella");
        btnVolver = new JButton("Volver");

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnEnrolar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnEnrolar.addActionListener(e -> {
            int fila = tablaAlumnos.getSelectedRow();
            if (fila >= 0) {
                String dni = modeloTabla.getValueAt(fila, 3).toString();
                new FrmEnrolamiento(dni).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un alumno de la lista.");
            }
        });
        btnVolver.addActionListener(e -> {
            // Regresar al dashboard (asumiendo que somos admin si entramos aquí)
            this.dispose();
            // Para simplificar no pasamos el usuario completo de vuelta, 
            // en un sistema real se mantendría la sesión.
        });
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Usuario> alumnos = dao.listarAlumnos();
        for (Usuario a : alumnos) {
            modeloTabla.addRow(new Object[]{
                a.getId(), a.getNombres(), a.getApellidos(), a.getDni(), 
                (a.getHuella_id() == 0 ? "No registrada" : a.getHuella_id())
            });
        }
    }
}
