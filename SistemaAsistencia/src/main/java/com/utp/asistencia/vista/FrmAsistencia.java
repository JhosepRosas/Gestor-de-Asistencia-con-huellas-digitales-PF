package com.utp.asistencia.vista;

import com.utp.asistencia.controlador.ArduinoControlador;
import com.utp.asistencia.modelo.AsistenciaDAO;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrmAsistencia extends JFrame {

    private JLabel lblReloj;
    private JLabel lblCursoActual;
    private JLabel lblMensaje;
    private JTextArea txtLog;
    private ArduinoControlador arduino;
    private AsistenciaDAO dao;
    private String[] cursoInfo;
    private final String PUERTO = "COM3";

    public FrmAsistencia() {
        arduino = new ArduinoControlador();
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        conectarArduino();
        iniciarRelojYCurso();
    }

    private void configurarVentana() {
        setTitle("Sistema de Asistencia Biométrico - UTP");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        lblReloj = new JLabel("00:00:00", SwingConstants.CENTER);
        lblReloj.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblReloj.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCursoActual = new JLabel("Buscando curso...", SwingConstants.CENTER);
        lblCursoActual.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblCursoActual.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblMensaje = new JLabel("Pase su huella por el sensor", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelNorte.add(lblReloj);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 10)));
        panelNorte.add(lblCursoActual);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 15)));
        panelNorte.add(lblMensaje);
        add(panelNorte, BorderLayout.NORTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JButton btnLogin = new JButton("Acceso Personal");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            arduino.desconectar();
            new FrmLogin().setVisible(true);
            this.dispose();
        });
        panelSur.add(btnLogin);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void conectarArduino() {
        boolean ok = arduino.conectar(PUERTO, new ArduinoControlador.ArduinoListener() {
            @Override
            public void onMessageReceived(String message) {
                procesarMensaje(message);
            }

            @Override
            public void onConnectionLost() {
                SwingUtilities.invokeLater(() -> {
                    lblMensaje.setText("¡ERROR: SENSOR DESCONECTADO!");
                    lblMensaje.setForeground(new Color(255, 100, 100));
                });
            }
        });

        if (ok) {
            log("Sensor inicializado en " + PUERTO);
            arduino.enviarComando("V");
        } else {
            log("No se pudo conectar con el sensor en " + PUERTO);
            lblMensaje.setText("ERROR DE CONEXIÓN");
        }
    }

    private void procesarMensaje(String msg) {
        if (msg.startsWith("FOUND:")) {
            String[] parts = msg.substring(6).split(",");
            int id = Integer.parseInt(parts[0]);
            String nombre = dao.obtenerNombrePorHuella(id);
            
            if (cursoInfo != null) {
                int cursoId = Integer.parseInt(cursoInfo[0]);
                if (dao.registrarAsistencia(id, cursoId)) {
                    lblMensaje.setText("¡HOLA " + nombre.toUpperCase() + "!");
                    lblMensaje.setForeground(new Color(100, 255, 100));
                    log("Asistencia: " + nombre + " -> " + cursoInfo[1] + " a las " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                } else {
                    lblMensaje.setText(nombre.toUpperCase() + " YA MARCÓ ASISTENCIA");
                    lblMensaje.setForeground(new Color(255, 200, 100));
                    log("Asistencia duplicada: " + nombre + " ya tiene asistencia hoy en " + cursoInfo[1]);
                }
            } else {
                lblMensaje.setText("SIN CURSO PROGRAMADO");
                lblMensaje.setForeground(new Color(255, 200, 100));
                log("Intento de asistencia de " + nombre + " sin curso actual.");
            }
            
            reiniciarLectura(2500);
            
        } else if (msg.equals("NOT_FOUND")) {
            lblMensaje.setText("USUARIO DESCONOCIDO");
            lblMensaje.setForeground(new Color(255, 100, 100));
            log("Huella no reconocida por el sistema.");
            reiniciarLectura(1500);
        }
    }

    private void reiniciarLectura(int ms) {
        new Thread(() -> {
            try { Thread.sleep(ms); } catch (Exception e) {}
            if (arduino != null && arduino.estaConectado()) {
                arduino.enviarComando("V");
                SwingUtilities.invokeLater(() -> {
                    lblMensaje.setText("Pase su huella por el sensor");
                    lblMensaje.setForeground(new Color(150, 200, 255));
                });
            }
        }).start();
    }

    private void iniciarRelojYCurso() {
        Timer timer = new Timer(1000, e -> {
            Date ahora = new Date();
            lblReloj.setText(new SimpleDateFormat("HH:mm:ss").format(ahora));
            
            cursoInfo = dao.obtenerCursoActual();
            if (cursoInfo != null) {
                lblCursoActual.setText("Curso Actual: " + cursoInfo[1]);
                lblCursoActual.setForeground(new Color(150, 255, 150));
            } else {
                lblCursoActual.setText("No hay cursos en este horario");
                lblCursoActual.setForeground(new Color(150, 150, 150));
            }
        });
        timer.start();
    }

    private void log(String s) {
        txtLog.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + s + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}
