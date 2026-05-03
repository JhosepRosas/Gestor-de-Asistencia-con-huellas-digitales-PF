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
    private JButton btnRefrescar, btnEnrolar, btnVolver, btnNuevo;

    public FrmGestionAlumnos() {
        dao = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Alumnos - UTP");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "DNI", "Huella ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAlumnos = new JTable(modeloTabla);
        add(new JScrollPane(tablaAlumnos), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        btnNuevo = new JButton("Nuevo Alumno");
        btnRefrescar = new JButton("Refrescar");
        btnEnrolar = new JButton("Enrolar Huella");
        btnVolver = new JButton("Volver");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnEnrolar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnNuevo.addActionListener(e -> mostrarFormularioRegistro());
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnEnrolar.addActionListener(e -> {
            int fila = tablaAlumnos.getSelectedRow();
            if (fila >= 0) {
                String dni = modeloTabla.getValueAt(fila, 3).toString();
                // Abrir enrolamiento para el DNI seleccionado
                new FrmEnrolamiento(dni).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un alumno de la lista para enrolar su huella.");
            }
        });
        btnVolver.addActionListener(e -> this.dispose());
    }

    private void mostrarFormularioRegistro() {
        JTextField txtNombres = new JTextField();
        JTextField txtApellidos = new JTextField();
        JTextField txtDni = new JTextField();
        Object[] message = {
            "Nombres:", txtNombres,
            "Apellidos:", txtApellidos,
            "DNI / Código:", txtDni
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Registrar Nuevo Alumno", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nom = txtNombres.getText().trim();
            String ape = txtApellidos.getText().trim();
            String dni = txtDni.getText().trim();

            if (!nom.isEmpty() && !ape.isEmpty() && !dni.isEmpty()) {
                Usuario nuevo = new Usuario(nom, ape, dni, dni, "123", "ALUMNO", 0);
                if (dao.registrar(nuevo)) {
                    JOptionPane.showMessageDialog(this, "Alumno registrado. Ahora selecciónelo en la lista y pulse 'Enrolar Huella'.");
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar. El DNI/Código ya podría existir.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            }
        }
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
