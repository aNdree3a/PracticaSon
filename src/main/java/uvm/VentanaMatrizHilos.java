package uvm;

import javax.swing.*;
import java.awt.*;

public class VentanaMatrizHilos extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JLabel[][] matriz = new JLabel[15][15];

    private HiloSerpiente serpiente;
    private HiloCazador cazador;
    private HiloRebotador rebotador;
    private HiloLimpiador limpiador;

    private JButton btIniciarS;
    private JButton btPausaS;
    private JButton btTerminarS;

    private JButton btIniciarC;
    private JButton btPausaC;
    private JButton btTerminarC;

    private JButton btIniciarR;
    private JButton btPausaR;
    private JButton btTerminarR;

    private JButton btIniciarL;
    private JButton btPausaL;
    private JButton btTerminarL;

    public VentanaMatrizHilos() {
        super("Practica de Concurrencia - Matriz Compartida");

        inicializarGUI();
        asociarEventos();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 650));
    }

    private void inicializarGUI() {
        setLayout(new BorderLayout(5, 5));

        crearMatriz();
        crearPanelNorte();
        crearPanelSur();
        crearPanelOeste();
    }

    private void crearMatriz() {
        JPanel panelMatriz = new JPanel(
                new GridLayout(15, 15, 1, 1));

        panelMatriz.setBorder(
                BorderFactory.createTitledBorder(
                        "Matriz Compartida (15x15)"));

        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {

                JLabel etiqueta = new JLabel("-", SwingConstants.CENTER);

                etiqueta.setOpaque(true);
                etiqueta.setBackground(Color.WHITE);
                etiqueta.setForeground(Color.BLACK);

                etiqueta.setBorder(
                        BorderFactory.createLineBorder(
                                Color.LIGHT_GRAY));

                etiqueta.setFont(
                        new Font("Monospaced", Font.BOLD, 10));

                matriz[fila][columna] = etiqueta;
                panelMatriz.add(etiqueta);
            }
        }

        add(panelMatriz, BorderLayout.CENTER);
    }

    private void crearPanelNorte() {
        JPanel panelNorte = new JPanel(
                new GridLayout(1, 2, 5, 5));

        JPanel panelSerpiente = crearPanelHilo("Hilo Serpiente");

        btIniciarS = crearBoton(panelSerpiente, "Iniciar");
        btPausaS = crearBoton(panelSerpiente, "Pausa");
        btTerminarS = crearBoton(panelSerpiente, "Terminar");

        JPanel panelCazador = crearPanelHilo("Hilo Cazador");

        btIniciarC = crearBoton(panelCazador, "Iniciar");
        btPausaC = crearBoton(panelCazador, "Pausa");
        btTerminarC = crearBoton(panelCazador, "Terminar");

        panelNorte.add(panelSerpiente);
        panelNorte.add(panelCazador);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void crearPanelSur() {
        JPanel panelSur = new JPanel(
                new GridLayout(1, 1, 5, 5));

        JPanel panelRebotador = crearPanelHilo("Hilo Rebotador");

        btIniciarR = crearBoton(panelRebotador, "Iniciar");
        btPausaR = crearBoton(panelRebotador, "Pausa");
        btTerminarR = crearBoton(panelRebotador, "Terminar");

        panelSur.add(panelRebotador);

        add(panelSur, BorderLayout.SOUTH);
    }

    private void crearPanelOeste() {
        JPanel panelLimpiador = new JPanel(
                new GridLayout(3, 1, 5, 5));

        panelLimpiador.setBorder(
                BorderFactory.createTitledBorder(
                        "Hilo Limpiador"));

        btIniciarL = crearBoton(panelLimpiador, "Iniciar");
        btPausaL = crearBoton(panelLimpiador, "Pausa");
        btTerminarL = crearBoton(panelLimpiador, "Terminar");

        JPanel contenedor = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        contenedor.add(panelLimpiador);

        add(contenedor, BorderLayout.WEST);
    }

    private JPanel crearPanelHilo(String titulo) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(
                BorderFactory.createTitledBorder(titulo));
        return panel;
    }

    private JButton crearBoton(JPanel panel, String texto) {
        JButton boton = new JButton(texto);
        panel.add(boton);
        return boton;
    }

    private void asociarEventos() {
        btIniciarS.addActionListener(e -> iniciarSerpiente());
        btPausaS.addActionListener(e -> pausarSerpiente());
        btTerminarS.addActionListener(e -> terminarSerpiente());

        btIniciarC.addActionListener(e -> iniciarCazador());
        btPausaC.addActionListener(e -> pausarCazador());
        btTerminarC.addActionListener(e -> terminarCazador());

        btIniciarR.addActionListener(e -> iniciarRebotador());
        btPausaR.addActionListener(e -> pausarRebotador());
        btTerminarR.addActionListener(e -> terminarRebotador());

        btIniciarL.addActionListener(e -> iniciarLimpiador());
        btPausaL.addActionListener(e -> pausarLimpiador());
        btTerminarL.addActionListener(e -> terminarLimpiador());
    }

    // HILO SERPIENTE

    private void iniciarSerpiente() {
        if (serpiente != null && serpiente.isAlive()) {
            serpiente.continuar();
            return;
        }

        limpiarMatriz();

        serpiente = new HiloSerpiente(matriz);
        serpiente.start();
    }

    private void pausarSerpiente() {
        if (serpiente != null && serpiente.isAlive()) {
            serpiente.pausar();
        }
    }

    private void terminarSerpiente() {
        if (serpiente != null) {
            serpiente.terminar();
            serpiente = null;
        }
    }

    // HILO CAZADOR

    private void iniciarCazador() {
        if (cazador != null && cazador.isAlive()) {
            cazador.reanudar();
            return;
        }

        cazador = new HiloCazador(matriz);
        cazador.start();
    }

    private void pausarCazador() {
        if (cazador != null && cazador.isAlive()) {
            cazador.pausar();
        }
    }

    private void terminarCazador() {
        if (cazador != null) {
            cazador.terminar();
            cazador = null;
        }
    }

    // HILO REBOTADOR

    private void iniciarRebotador() {
        if (rebotador != null && rebotador.isAlive()) {
            rebotador.continuar();
            return;
        }

        rebotador = new HiloRebotador(matriz);
        rebotador.start();
    }

    private void pausarRebotador() {
        if (rebotador != null && rebotador.isAlive()) {
            rebotador.pausar();
        }
    }

    private void terminarRebotador() {
        if (rebotador != null) {
            rebotador.terminar();
            rebotador = null;
        }
    }

    // HILO LIMPIADOR

    private void iniciarLimpiador() {
        if (limpiador != null && limpiador.isAlive()) {
            limpiador.continuar();
            return;
        }

        limpiador = new HiloLimpiador(matriz);
        limpiador.start();
    }

    private void pausarLimpiador() {
        if (limpiador != null && limpiador.isAlive()) {
            limpiador.pausar();
        }
    }

    private void terminarLimpiador() {
        if (limpiador != null) {
            limpiador.terminar();
            limpiador = null;
        }
    }

    // LIMPIAR MATRIZ

    private void limpiarMatriz() {
        for (JLabel[] fila : matriz) {
            for (JLabel celda : fila) {
                celda.setText("-");
                celda.setForeground(Color.BLACK);
                celda.setBackground(Color.WHITE);
                celda.setVisible(true);
            }
        }
    }

    public JLabel[][] getMatrizLabels() {
        return matriz;
    }
}
