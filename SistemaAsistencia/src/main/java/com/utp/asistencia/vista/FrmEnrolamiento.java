package com.utp.asistencia.vista;

import com.utp.asistencia.controlador.ArduinoControlador;
import com.utp.asistencia.modelo.UsuarioDAO;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class FrmEnrolamiento extends JFrame {

    private JTextField txtDni;
    private JButton btnActivarSensor;
    private JLabel lblEstado;
    private JLabel lblTitulo;
    private ArduinoControlador arduino;
    private UsuarioDAO dao;
    private int idGenerado;

    private String dniPredefinido;

    public FrmEnrolamiento() {
        this(null);
    }

    public FrmEnrolamiento(String dni) {
        this.dniPredefinido = dni;
        arduino = new ArduinoControlador();
        dao = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        
        if (dniPredefinido != null) {
            txtDni.setText(dniPredefinido);
            txtDni.setEditable(false);
        }
    }

    private void configurarVentana() {
        this.setTitle("Registro de Huella - UTP");
        this.setSize(480, 380);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        lblTitulo = new JLabel("Registro de Huella Digital");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelDni = new JPanel();
        panelDni.setLayout(new BoxLayout(panelDni, BoxLayout.Y_AXIS));
        JLabel lblDni = new JLabel("DNI del Alumno:");
        lblDni.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDni.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDni = new JTextField();
        txtDni.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDni.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtDni.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDni.add(lblDni);
        panelDni.add(Box.createRigidArea(new Dimension(0, 8)));
        panelDni.add(txtDni);
        panelPrincipal.add(panelDni);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 25)));

        btnActivarSensor = new JButton("Iniciar Registro");
        btnActivarSensor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActivarSensor.setPreferredSize(new Dimension(200, 40));
        btnActivarSensor.setMaximumSize(new Dimension(200, 40));
        btnActivarSensor.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnActivarSensor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelPrincipal.add(btnActivarSensor);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        lblEstado = new JLabel("Esperando DNI...", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(150, 150, 150));
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblEstado);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnVolver = new JButton("Volver a Asistencia");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setPreferredSize(new Dimension(180, 35));
        btnVolver.setMaximumSize(new Dimension(180, 35));
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            arduino.desconectar();
            new FrmAsistencia().setVisible(true);
            this.dispose();
        });
        panelPrincipal.add(btnVolver);

        add(panelPrincipal);

        btnActivarSensor.addActionListener(e -> iniciarEnrolamiento());
    }

    private void iniciarEnrolamiento() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnActivarSensor.setEnabled(false);
        lblEstado.setText("Conectando...");
        lblEstado.setForeground(new Color(100, 150, 255));

        arduino.conectar("COM3", new ArduinoControlador.ArduinoListener() {
            @Override
            public void onMessageReceived(String msg) {
                SwingUtilities.invokeLater(() -> procesarMensaje(msg, dni));
            }

            @Override
            public void onConnectionLost() {
                SwingUtilities.invokeLater(() -> lblEstado.setText("Error de conexión"));
            }
        });

        idGenerado = new Random().nextInt(126) + 1;
        arduino.enviarComando("E");
        arduino.enviarComando(String.valueOf(idGenerado));
    }

    private void procesarMensaje(String msg, String dni) {
        switch (msg) {
            case "WAIT_FINGER":
                lblEstado.setText("Coloque el dedo en el sensor");
                lblEstado.setForeground(new Color(100, 150, 255));
                break;
            case "REMOVE_FINGER":
                lblEstado.setText("Retire el dedo");
                lblEstado.setForeground(new Color(255, 200, 100));
                break;
            case "REPLACE_FINGER":
                lblEstado.setText("Coloque el mismo dedo otra vez");
                lblEstado.setForeground(new Color(255, 200, 100));
                break;
            default:
                if (msg.startsWith("SAVED:")) {
                    int id = Integer.parseInt(msg.split(":")[1]);
                    if (dao.asignarHuella(dni, id)) {
                        lblEstado.setText("¡Guardado con éxito! ID: " + id);
                        lblEstado.setForeground(new Color(100, 255, 100));
                        JOptionPane.showMessageDialog(this, "Usuario enrolado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnActivarSensor.setEnabled(true);
                } else if (msg.startsWith("ERR_")) {
                    lblEstado.setText("Error: " + msg);
                    lblEstado.setForeground(new Color(255, 100, 100));
                    btnActivarSensor.setEnabled(true);
                }
                break;
        }
    }
}
