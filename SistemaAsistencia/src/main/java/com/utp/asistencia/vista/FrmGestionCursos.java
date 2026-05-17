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
    private JLabel lblTitulo;
    private JButton btnVolver;

    public FrmGestionCursos() {
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Cursos - UTP");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        lblTitulo = new JLabel("Gestión de Cursos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        
        add(panelNorte, BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Nuevo Curso", 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 10, 20),
            panelForm.getBorder()
        ));

        JPanel panelNombre = new JPanel();
        panelNombre.setLayout(new BoxLayout(panelNombre, BoxLayout.Y_AXIS));
        JLabel lblNombre = new JLabel("Nombre del Curso:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelNombre.add(lblNombre);
        panelNombre.add(Box.createRigidArea(new Dimension(0, 8)));
        panelNombre.add(txtNombre);

        JPanel panelCodigo = new JPanel();
        panelCodigo.setLayout(new BoxLayout(panelCodigo, BoxLayout.Y_AXIS));
        JLabel lblCodigo = new JLabel("Código del Curso:");
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCodigo = new JTextField();
        txtCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCodigo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelCodigo.add(lblCodigo);
        panelCodigo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelCodigo.add(txtCodigo);

        panelForm.add(panelNombre);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(panelCodigo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelBotonesForm = new JPanel();
        panelBotonesForm.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = crearBoton("Guardar Curso");
        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            String cod = txtCodigo.getText().trim();
            if (!nom.isEmpty() && !cod.isEmpty()) {
                if (dao.registrarCurso(nom, cod)) {
                    JOptionPane.showMessageDialog(this, "Curso registrado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    txtNombre.setText("");
                    txtCodigo.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar curso. El código ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        panelBotonesForm.add(btnGuardar);
        panelForm.add(panelBotonesForm);

        add(panelForm, BorderLayout.WEST);

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Código"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCursos = new JTable(modeloTabla);
        tablaCursos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCursos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCursos.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaCursos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton btnGestionHorarios = crearBoton("Gestionar Horarios");
        btnGestionHorarios.addActionListener(e -> {
            int fila = tablaCursos.getSelectedRow();
            if (fila >= 0) {
                int cId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
                String cNombre = modeloTabla.getValueAt(fila, 1).toString();
                new FrmGestionHorarios(cId, cNombre).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un curso primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnVolver = crearBoton("Volver");
        btnVolver.addActionListener(e -> this.dispose());
        
        panelSur.add(btnGestionHorarios);
        panelSur.add(btnVolver);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<String[]> cursos = dao.listarCursos();
        for (String[] c : cursos) {
            modeloTabla.addRow(new Object[]{c[0], c[1], c[2]});
        }
    }
}
