package com.utp.asistencia.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.utp.asistencia.controlador.ArduinoControlador;
import com.utp.asistencia.modelo.AsistenciaDAO;

public class FrmAsistencia extends JFrame {

    private JLabel lblReloj;
    private JLabel lblMensaje;
    private JTextArea txtLog;
    private JComboBox<String> cbCursos;
    private List<String[]> listaCursos;
    private ArduinoControlador arduino;
    private AsistenciaDAO dao;
    private final String PUERTO = "COM3";

    public FrmAsistencia() {
        arduino = new ArduinoControlador();
        dao = new AsistenciaDAO();
        configurarVentana();
        inicializarComponentes();
        cargarCursos();
        conectarArduino();
        iniciarReloj();
    }

    private void configurarVentana() {
        setTitle("Control de Asistencia - UTP");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel(new GridLayout(3, 1));
        lblReloj = new JLabel("00:00:00", SwingConstants.CENTER);
        lblReloj.setFont(new Font("Arial", Font.BOLD, 30));
        
        cbCursos = new JComboBox<>();
        JPanel panelCurso = new JPanel(new FlowLayout());
        panelCurso.add(new JLabel("Curso:"));
        panelCurso.add(cbCursos);

        lblMensaje = new JLabel("Pase su huella por el sensor", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 18));
        lblMensaje.setForeground(Color.BLUE);

        panelNorte.add(lblReloj);
        panelNorte.add(panelCurso);
        panelNorte.add(lblMensaje);
        add(panelNorte, BorderLayout.NORTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> {
            arduino.desconectar();
            this.dispose();
            // Aquí idealmente volveríamos al Dashboard, pero para simplificar solo cerramos
        });
        add(btnVolver, BorderLayout.SOUTH);
    }

    private void cargarCursos() {
        listaCursos = dao.listarCursos();
        cbCursos.removeAllItems();
        if (listaCursos.isEmpty()) {
            cbCursos.addItem("Sin cursos disponibles");
        } else {
            for (String[] c : listaCursos) {
                cbCursos.addItem(c[1]);
            }
        }
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
            
            int cursoIdx = cbCursos.getSelectedIndex();
            int cursoId = (listaCursos.isEmpty() || cursoIdx < 0) ? 0 : Integer.parseInt(listaCursos.get(cursoIdx)[0]);

            if (dao.registrarAsistencia(id, cursoId)) {
                lblMensaje.setText("¡Bienvenido " + nombre + "!");
                lblMensaje.setForeground(new Color(0, 150, 0));
                log("Asistencia registrada: " + nombre + " en " + cbCursos.getSelectedItem());
            }
            
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (Exception e) {}
                if (arduino.estaConectado()) {
                    arduino.enviarComando("V");
                    lblMensaje.setText("Pase su huella por el sensor");
                    lblMensaje.setForeground(Color.BLUE);
                }
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
