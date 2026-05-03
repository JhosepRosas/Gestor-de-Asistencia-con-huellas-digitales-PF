package com.utp.asistencia.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.utp.asistencia.modelo.Usuario;

public class FrmDashboard extends JFrame {
    private Usuario user;

    public FrmDashboard(Usuario user) {
        this.user = user;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Panel Principal - " + user.getRol());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblWelcome = new JLabel("Bienvenido, " + user.getNombres() + " (" + user.getRol() + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblWelcome, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnAsistencia = new JButton("Control de Asistencia");
        JButton btnAlumnos = new JButton("Gestión de Alumnos");
        JButton btnCursos = new JButton("Gestión de Cursos");
        JButton btnReportes = new JButton("Reportes de Asistencia");

        // Restricciones por ROL
        if (user.getRol().equals("DOCENTE")) {
            btnAlumnos.setEnabled(false);
            btnCursos.setEnabled(false);
        }

        panelBotones.add(btnAsistencia);
        panelBotones.add(btnAlumnos);
        panelBotones.add(btnCursos);
        panelBotones.add(btnReportes);

        panel.add(panelBotones, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Cerrar Sesión");
        panel.add(btnLogout, BorderLayout.SOUTH);

        add(panel);

        // Acciones
        btnAsistencia.addActionListener(e -> {
            new FrmAsistencia().setVisible(true);
            this.dispose();
        });

        btnAlumnos.addActionListener(e -> {
            new FrmGestionAlumnos().setVisible(true);
            this.dispose();
        });

        btnLogout.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
        });
        
        btnCursos.addActionListener(e -> new FrmGestionCursos().setVisible(true));
        btnReportes.addActionListener(e -> new FrmReporteAsistencia().setVisible(true));
    }
}
