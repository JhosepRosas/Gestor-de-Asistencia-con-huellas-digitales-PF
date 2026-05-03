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
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(255, 255, 255));
    }

    private void inicializarComponentes() {
        // Panel Superior: Reloj y Curso
        JPanel panelNorte = new JPanel(new GridLayout(3, 1));
        panelNorte.setOpaque(false);
        
        lblReloj = new JLabel("00:00:00", SwingConstants.CENTER);
        lblReloj.setFont(new Font("Arial", Font.BOLD, 48));
        lblReloj.setForeground(new Color(0, 51, 102));

        lblCursoActual = new JLabel("Buscando curso...", SwingConstants.CENTER);
        lblCursoActual.setFont(new Font("Arial", Font.ITALIC, 20));
        lblCursoActual.setForeground(new Color(102, 102, 102));

        lblMensaje = new JLabel("Pase su huella por el sensor", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 18));
        lblMensaje.setForeground(new Color(0, 102, 204));

        panelNorte.add(lblReloj);
        panelNorte.add(lblCursoActual);
        panelNorte.add(lblMensaje);
        add(panelNorte, BorderLayout.NORTH);

        // Centro: Log de eventos
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBackground(new Color(245, 245, 245));
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        // Panel Inferior: Botón Login
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        JButton btnLogin = new JButton("Acceso Personal");
        btnLogin.setBackground(new Color(0, 51, 102));
        btnLogin.setForeground(Color.WHITE);
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
                    lblMensaje.setForeground(Color.RED);
                });
            }
        });

        if (ok) {
            log("Sensor inicializado en " + PUERTO);
            arduino.enviarComando("V"); // Iniciar modo verificación
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
                    lblMensaje.setForeground(new Color(0, 150, 0));
                    log("Asistencia: " + nombre + " -> " + cursoInfo[1]);
                }
            } else {
                lblMensaje.setText("SIN CURSO PROGRAMADO");
                lblMensaje.setForeground(Color.ORANGE);
                log("Intento de asistencia de " + nombre + " sin curso actual.");
            }
            
            reiniciarLectura(2500);
            
        } else if (msg.equals("NOT_FOUND")) {
            lblMensaje.setText("USUARIO DESCONOCIDO");
            lblMensaje.setForeground(Color.RED);
            log("Huella no reconocida por el sistema.");
            reiniciarLectura(1500);
        } else if (msg.equals("SCANNING")) {
            lblMensaje.setText("ESPERANDO HUELLA...");
            lblMensaje.setForeground(new Color(0, 102, 204));
        } else if (msg.startsWith("ERR_")) {
            lblMensaje.setText("ERROR AL LEER");
            lblMensaje.setForeground(Color.RED);
            reiniciarLectura(1000);
        }
    }

    private void reiniciarLectura(int ms) {
        new Thread(() -> {
            try { Thread.sleep(ms); } catch (Exception e) {}
            if (arduino != null && arduino.estaConectado()) {
                arduino.enviarComando("V");
                SwingUtilities.invokeLater(() -> {
                    lblMensaje.setText("Pase su huella por el sensor");
                    lblMensaje.setForeground(new Color(0, 102, 204));
                });
            }
        }).start();
    }

    private void iniciarRelojYCurso() {
        Timer timer = new Timer(1000, e -> {
            Date ahora = new Date();
            lblReloj.setText(new SimpleDateFormat("HH:mm:ss").format(ahora));
            
            // Actualizar curso cada minuto o al inicio
            cursoInfo = dao.obtenerCursoActual();
            if (cursoInfo != null) {
                lblCursoActual.setText("Curso Actual: " + cursoInfo[1]);
                lblCursoActual.setForeground(new Color(0, 102, 0));
            } else {
                lblCursoActual.setText("No hay cursos en este horario");
                lblCursoActual.setForeground(Color.GRAY);
            }
        });
        timer.start();
    }

    private void log(String s) {
        txtLog.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + s + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}
