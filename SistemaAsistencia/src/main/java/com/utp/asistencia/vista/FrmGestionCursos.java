package com.utp.asistencia.vista;

import com.utp.asistencia.modelo.AsistenciaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmGestionCursos extends JFrame {
    private JTable tablaCursos;
    private DefaultTableModel modeloTabla;
    private AsistenciaDAO dao;
    private JTextField txtNombre, txtCodigo;

    public FrmGestionCursos() {
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Cursos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Formulario
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nuevo Curso"));
        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        panelForm.add(txtCodigo);
        
        JButton btnGuardar = new JButton("Guardar Curso");
        panelForm.add(btnGuardar);
        add(panelForm, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Código"}, 0);
        tablaCursos = new JTable(modeloTabla);
        add(new JScrollPane(tablaCursos), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            String cod = txtCodigo.getText().trim();
            if (!nom.isEmpty() && !cod.isEmpty()) {
                if (dao.registrarCurso(nom, cod)) {
                    JOptionPane.showMessageDialog(this, "Curso registrado.");
                    cargarDatos();
                    txtNombre.setText("");
                    txtCodigo.setText("");
                }
            }
        });
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<String[]> cursos = dao.listarCursos();
        for (String[] c : cursos) {
            // Suponiendo que listarCursos devuelve [id, nombre, codigo]
            // Nota: El método actual solo devuelve [id, nombre], vamos a actualizarlo.
            modeloTabla.addRow(new Object[]{c[0], c[1], "N/A"}); 
        }
    }
}
