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
    private ArduinoControlador arduino;
    private UsuarioDAO dao;
    private int idGenerado;

    public FrmEnrolamiento() {
        arduino = new ArduinoControlador();
        dao = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        this.setTitle("Registro de Huella - UTP");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("DNI del Alumno:"));
        txtDni = new JTextField();
        panel.add(txtDni);

        btnActivarSensor = new JButton("Iniciar Registro");
        panel.add(btnActivarSensor);

        lblEstado = new JLabel("Esperando DNI...", SwingConstants.CENTER);
        panel.add(lblEstado);

        JButton btnVolver = new JButton("Volver a Asistencia");
        btnVolver.addActionListener(e -> {
            arduino.desconectar();
            new FrmAsistencia().setVisible(true);
            this.dispose();
        });
        panel.add(btnVolver);

        add(panel);

        btnActivarSensor.addActionListener(e -> iniciarEnrolamiento());
    }

    private void iniciarEnrolamiento() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido");
            return;
        }

        btnActivarSensor.setEnabled(false);
        lblEstado.setText("Conectando...");

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

        // Simular un ID libre (en un sistema real se consultaría al Arduino o DB)
        idGenerado = new Random().nextInt(126) + 1;
        arduino.enviarComando("E");
        arduino.enviarComando(String.valueOf(idGenerado));
    }

    private void procesarMensaje(String msg, String dni) {
        switch (msg) {
            case "WAIT_FINGER":
                lblEstado.setText("Coloque el dedo en el sensor");
                lblEstado.setForeground(Color.BLUE);
                break;
            case "REMOVE_FINGER":
                lblEstado.setText("Retire el dedo");
                break;
            case "REPLACE_FINGER":
                lblEstado.setText("Coloque el mismo dedo otra vez");
                break;
            default:
                if (msg.startsWith("SAVED:")) {
                    int id = Integer.parseInt(msg.split(":")[1]);
                    if (dao.asignarHuella(dni, id)) {
                        lblEstado.setText("¡Guardado con éxito! ID: " + id);
                        lblEstado.setForeground(new Color(0, 150, 0));
                        JOptionPane.showMessageDialog(this, "Usuario enrolado correctamente");
                    }
                    btnActivarSensor.setEnabled(true);
                } else if (msg.startsWith("ERR_")) {
                    lblEstado.setText("Error: " + msg);
                    lblEstado.setForeground(Color.RED);
                    btnActivarSensor.setEnabled(true);
                }
                break;
        }
    }
}
