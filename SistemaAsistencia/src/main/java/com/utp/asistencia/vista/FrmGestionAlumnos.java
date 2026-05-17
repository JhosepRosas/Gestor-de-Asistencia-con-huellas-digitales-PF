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
    private JLabel lblTitulo;

    public FrmGestionAlumnos() {
        dao = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Alumnos - UTP");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        lblTitulo = new JLabel("Gestión de Alumnos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        
        add(panelNorte, BorderLayout.NORTH);
        
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "DNI", "Huella ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAlumnos = new JTable(modeloTabla);
        tablaAlumnos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAlumnos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaAlumnos.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaAlumnos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnNuevo = crearBoton("Nuevo Alumno");
        btnRefrescar = crearBoton("Refrescar");
        btnEnrolar = crearBoton("Enrolar Huella");
        btnVolver = crearBoton("Volver");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnEnrolar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> mostrarFormularioRegistro());
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnEnrolar.addActionListener(e -> {
            int fila = tablaAlumnos.getSelectedRow();
            if (fila >= 0) {
                String dni = modeloTabla.getValueAt(fila, 3).toString();
                new FrmEnrolamiento(dni).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un alumno de la lista para enrolar su huella.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnVolver.addActionListener(e -> this.dispose());
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
                    JOptionPane.showMessageDialog(this, "Alumno registrado. Ahora selecciónelo en la lista y pulse 'Enrolar Huella'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar. El DNI/Código ya podría existir.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
