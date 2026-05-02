package com.utp.asistencia.vista;

import com.utp.asistencia.controlador.ArduinoControlador;
import com.utp.asistencia.modelo.AsistenciaDAO;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrmAsistencia extends JFrame {

    private JLabel lblReloj;
    private JLabel lblMensaje;
    private JTextArea txtLog;
    private ArduinoControlador arduino;
    private AsistenciaDAO dao;
    private final String PUERTO = "COM3";

    public FrmAsistencia() {
        arduino = new ArduinoControlador();
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        conectarArduino();
        iniciarReloj();
    }

    private void configurarVentana() {
        setTitle("Control de Asistencia - UTP");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new GridLayout(2, 1));
        lblReloj = new JLabel("00:00:00", SwingConstants.CENTER);
        lblReloj.setFont(new Font("Arial", Font.BOLD, 30));
        
        lblMensaje = new JLabel("Pase su huella por el sensor", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 18));
        lblMensaje.setForeground(Color.BLUE);

        panelNorte.add(lblReloj);
        panelNorte.add(lblMensaje);
        add(panelNorte, BorderLayout.NORTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        JButton btnModoRegistro = new JButton("Ir a Modo Registro");
        btnModoRegistro.addActionListener(e -> {
            arduino.desconectar();
            new FrmEnrolamiento().setVisible(true);
            this.dispose();
        });
        add(btnModoRegistro, BorderLayout.SOUTH);
    }

    private void conectarArduino() {
        boolean ok = arduino.conectar(PUERTO, new ArduinoControlador.ArduinoListener() {
            @Override
            public void onMessageReceived(String message) {
                procesarMensaje(message);
            }

            @Override
            public void onConnectionLost() {
                lblMensaje.setText("¡ERROR: Arduino desconectado!");
                lblMensaje.setForeground(Color.RED);
            }
        });

        if (ok) {
            log("Conectado a " + PUERTO);
            arduino.enviarComando("V"); // Iniciar modo verificación
        } else {
            log("Error al conectar con " + PUERTO);
        }
    }

    private void procesarMensaje(String msg) {
        if (msg.startsWith("FOUND:")) {
            String[] parts = msg.substring(6).split(",");
            int id = Integer.parseInt(parts[0]);
            String nombre = dao.obtenerNombrePorHuella(id);
            
            if (dao.registrarAsistencia(id)) {
                lblMensaje.setText("¡Bienvenido " + nombre + "!");
                lblMensaje.setForeground(new Color(0, 150, 0));
                log("Asistencia registrada: " + nombre + " (ID: " + id + ")");
            }
            // Reiniciar modo verificación después de un breve delay
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (Exception e) {}
                arduino.enviarComando("V");
                lblMensaje.setText("Pase su huella por el sensor");
                lblMensaje.setForeground(Color.BLUE);
            }).start();
            
        } else if (msg.equals("NOT_FOUND")) {
            lblMensaje.setText("Huella no reconocida");
            lblMensaje.setForeground(Color.RED);
            arduino.enviarComando("V");
        } else if (msg.equals("SCANNING")) {
            lblMensaje.setText("Escaneando...");
        }
    }

    private void iniciarReloj() {
        Timer timer = new Timer(1000, e -> {
            lblReloj.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        });
        timer.start();
    }

    private void log(String s) {
        txtLog.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + s + "\n");
    }
}
