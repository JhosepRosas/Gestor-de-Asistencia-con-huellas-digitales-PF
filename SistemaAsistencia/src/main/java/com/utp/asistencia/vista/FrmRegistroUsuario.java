package com.utp.asistencia.vista;

/**
 *
 * @author Haskell
 */
import com.utp.asistencia.controlador.UsuarioControlador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrmRegistroUsuario extends JFrame {

// 1. Declaramos los componentes visuales
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDni;
    private JComboBox<String> cbxRol;
    private JButton btnGuardar;
    private UsuarioControlador controlador;

    // 2. El Constructor configura la ventana al iniciar
    public FrmRegistroUsuario() {
        controlador = new UsuarioControlador(); // Inicializamos el controlador
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        this.setTitle("Registro de Usuarios");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra el programa al darle a la X
        this.setLocationRelativeTo(null); // Centra la ventana en la pantalla
        this.setResizable(false); // Evita que se cambie el tamaño
    }

    private void inicializarComponentes() {
        // Usamos un JPanel base con un borde vacío para darle "respiración" (márgenes)
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Usamos GridLayout: 5 filas, 2 columnas, y 10px de separación entre cada celda
        panelPrincipal.setLayout(new GridLayout(5, 2, 10, 10));

        // Instanciamos los componentes
        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtDni = new JTextField();
        cbxRol = new JComboBox<>(new String[]{"Alumno", "Docente"});
        btnGuardar = new JButton("Guardar Usuario");

        // Agregamos los componentes al panel (el orden importa, se llena de izquierda a derecha)
        // Fila 1
        panelPrincipal.add(new JLabel("Nombres:"));
        panelPrincipal.add(txtNombres);
        
        // Fila 2
        panelPrincipal.add(new JLabel("Apellidos:"));
        panelPrincipal.add(txtApellidos);
        
        // Fila 3
        panelPrincipal.add(new JLabel("DNI:"));
        panelPrincipal.add(txtDni);
        
        // Fila 4
        panelPrincipal.add(new JLabel("Rol:"));
        panelPrincipal.add(cbxRol);
        
        // Fila 5
        panelPrincipal.add(new JLabel("")); // Un espacio en blanco para empujar el botón a la derecha
        panelPrincipal.add(btnGuardar);

        // Agregamos el panel a la ventana
        this.add(panelPrincipal);

        // 3. Le damos la acción al botón (El Evento)
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarDatos();
            }
        });
    }

    // 4. El método que se ejecuta al hacer clic
    private void guardarDatos() {
        String nombres = txtNombres.getText();
        String apellidos = txtApellidos.getText();
        String dni = txtDni.getText();
        String rol = cbxRol.getSelectedItem().toString();

        if(controlador.procesarRegistro(nombres, apellidos, dni, rol)) {
            JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!");
            // Limpiamos las cajas
            txtNombres.setText("");
            txtApellidos.setText("");
            txtDni.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Revisa los datos o el DNI ya existe.");
        }
    }    
}
