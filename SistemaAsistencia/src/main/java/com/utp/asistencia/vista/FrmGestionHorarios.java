package com.utp.asistencia.vista;

import com.utp.asistencia.modelo.AsistenciaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmGestionHorarios extends JFrame {
    private JTable tablaHorarios;
    private DefaultTableModel modeloTabla;
    private AsistenciaDAO dao;
    private int cursoId;
    private String cursoNombre;
    private JComboBox<String> cmbDia;
    private JTextField txtHoraInicio, txtHoraFin;
    private JLabel lblTitulo;

    private static final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    public FrmGestionHorarios(int cursoId, String cursoNombre) {
        this.cursoId = cursoId;
        this.cursoNombre = cursoNombre;
        this.dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Horarios - " + cursoNombre);
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        lblTitulo = new JLabel("Horarios de: " + cursoNombre);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panelNorte.add(lblTitulo, BorderLayout.WEST);
        
        add(panelNorte, BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 10, 20),
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Nuevo Horario", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)
            )
        ));

        JPanel panelDia = new JPanel();
        panelDia.setLayout(new BoxLayout(panelDia, BoxLayout.Y_AXIS));
        JLabel lblDia = new JLabel("Día:");
        lblDia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDia = new JComboBox<>(DIAS);
        cmbDia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelDia.add(lblDia);
        panelDia.add(Box.createRigidArea(new Dimension(0, 8)));
        panelDia.add(cmbDia);

        JPanel panelHoras = new JPanel();
        panelHoras.setLayout(new GridLayout(1, 2, 15, 0));
        
        JPanel panelInicio = new JPanel();
        panelInicio.setLayout(new BoxLayout(panelInicio, BoxLayout.Y_AXIS));
        JLabel lblInicio = new JLabel("Hora Inicio (HH:mm):");
        lblInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtHoraInicio = new JTextField();
        txtHoraInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtHoraInicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelInicio.add(lblInicio);
        panelInicio.add(Box.createRigidArea(new Dimension(0, 8)));
        panelInicio.add(txtHoraInicio);

        JPanel panelFin = new JPanel();
        panelFin.setLayout(new BoxLayout(panelFin, BoxLayout.Y_AXIS));
        JLabel lblFin = new JLabel("Hora Fin (HH:mm):");
        lblFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtHoraFin = new JTextField();
        txtHoraFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtHoraFin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelFin.add(lblFin);
        panelFin.add(Box.createRigidArea(new Dimension(0, 8)));
        panelFin.add(txtHoraFin);

        panelHoras.add(panelInicio);
        panelHoras.add(panelFin);

        panelForm.add(panelDia);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(panelHoras);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelBotonesForm = new JPanel();
        panelBotonesForm.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = crearBoton("Agregar Horario");
        btnAgregar.addActionListener(e -> agregarHorario());
        panelBotonesForm.add(btnAgregar);
        panelForm.add(panelBotonesForm);

        add(panelForm, BorderLayout.WEST);

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Día", "Inicio", "Fin"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHorarios = new JTable(modeloTabla);
        tablaHorarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHorarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHorarios.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaHorarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton btnEditar = crearBoton("Editar");
        btnEditar.addActionListener(e -> editarHorario());
        
        JButton btnEliminar = crearBoton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarHorario());
        
        JButton btnVolver = crearBoton("Volver");
        btnVolver.addActionListener(e -> this.dispose());
        
        panelSur.add(btnEditar);
        panelSur.add(btnEliminar);
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
        List<String[]> horarios = dao.listarHorariosPorCurso(cursoId);
        for (String[] h : horarios) {
            modeloTabla.addRow(h);
        }
    }

    private void agregarHorario() {
        String dia = (String) cmbDia.getSelectedItem();
        String horaInicio = txtHoraInicio.getText().trim();
        String horaFin = txtHoraFin.getText().trim();

        if (dia == null || horaInicio.isEmpty() || horaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarHora(horaInicio) || !validarHora(horaFin)) {
            JOptionPane.showMessageDialog(this, "Formato de hora inválido (use HH:mm, ej: 08:00).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dao.agregarHorario(cursoId, dia, horaInicio, horaFin)) {
            JOptionPane.showMessageDialog(this, "Horario agregado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            txtHoraInicio.setText("");
            txtHoraFin.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar horario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarHorario() {
        int fila = tablaHorarios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un horario para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int horarioId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String diaActual = modeloTabla.getValueAt(fila, 1).toString();
        String inicioActual = modeloTabla.getValueAt(fila, 2).toString();
        String finActual = modeloTabla.getValueAt(fila, 3).toString();

        JComboBox<String> cmbEditarDia = new JComboBox<>(DIAS);
        cmbEditarDia.setSelectedItem(diaActual);
        JTextField txtEditarInicio = new JTextField(inicioActual);
        JTextField txtEditarFin = new JTextField(finActual);

        Object[] message = {
            "Día:", cmbEditarDia,
            "Hora Inicio:", txtEditarInicio,
            "Hora Fin:", txtEditarFin
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Horario", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nuevoDia = (String) cmbEditarDia.getSelectedItem();
            String nuevoInicio = txtEditarInicio.getText().trim();
            String nuevoFin = txtEditarFin.getText().trim();

            if (!validarHora(nuevoInicio) || !validarHora(nuevoFin)) {
                JOptionPane.showMessageDialog(this, "Formato de hora inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dao.actualizarHorario(horarioId, nuevoDia, nuevoInicio, nuevoFin)) {
                JOptionPane.showMessageDialog(this, "Horario actualizado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar horario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarHorario() {
        int fila = tablaHorarios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un horario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de que desea eliminar este horario?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            int horarioId = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            if (dao.eliminarHorario(horarioId)) {
                JOptionPane.showMessageDialog(this, "Horario eliminado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar horario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarHora(String hora) {
        return hora.matches("\\d{2}:\\d{2}");
    }
}
