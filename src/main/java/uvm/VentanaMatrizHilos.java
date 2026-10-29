package uvm;

import javax.swing.*;
import java.awt.*;

public class VentanaMatrizHilos extends JFrame {

    HiloCazador hcaz;
    HiloClima hcli;
    HiloLimpiador hlim;
    HiloRebotador hreb;
    HiloSerpiente hser;

    private JLabel[][] matriz = new JLabel[15][15];

    private JButton btIniciarS;
    private JButton btPausaS;
    private JButton btTerminarS;

    private JButton btIniciarC;
    private JButton btPausaC;
    private JButton btTerminarC;

    private JButton btIniciarR;
    private JButton btPausaR;
    private JButton btTerminarR;

    private JButton btIniciarCl;
    private JButton btPausaCl;
    private JButton btTerminarCl;

    private JButton btIniciarL;
    private JButton btPausaL;
    private JButton btTerminarL;

    public VentanaMatrizHilos() {
        super("Practica de Concurrencia - Matriz Compartida");

        inicializarGUI();
        asociarEventos();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 650));
    }

    private void inicializarGUI() {
        setLayout(new BorderLayout(5, 5));

        // MATRIZ 15 x 15
        JPanel panelMatriz = new JPanel(
                new GridLayout(15, 15, 1, 1));

        panelMatriz.setBorder(
                BorderFactory.createTitledBorder(
                        "Matriz Compartida (15x15)"));

        for (int fila = 0; fila < 15; fila++) {
            for (int col = 0; col < 15; col++) {

                JLabel lbl = new JLabel("-", SwingConstants.CENTER);

                lbl.setOpaque(true);
                lbl.setBackground(Color.WHITE);

                lbl.setBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                // Fuente más pequeña para mostrar números de 3 cifras
                lbl.setFont(new Font("Monospaced", Font.BOLD, 10));

                matriz[fila][col] = lbl;
                panelMatriz.add(lbl);
            }
        }

        add(panelMatriz, BorderLayout.CENTER);

        // NORTE: SERPIENTE Y CAZADOR
        JPanel panelNorte = new JPanel(
                new GridLayout(1, 2, 5, 5));

        JPanel panelSerpiente = new JPanel(new FlowLayout());
        panelSerpiente.setBorder(
                BorderFactory.createTitledBorder("Hilo Serpiente"));

        btIniciarS = new JButton("Iniciar");
        btPausaS = new JButton("Pausa");
        btTerminarS = new JButton("Terminar");

        panelSerpiente.add(btIniciarS);
        panelSerpiente.add(btPausaS);
        panelSerpiente.add(btTerminarS);

        JPanel panelCazador = new JPanel(new FlowLayout());
        panelCazador.setBorder(
                BorderFactory.createTitledBorder("Hilo Cazador"));

        btIniciarC = new JButton("Iniciar");
        btPausaC = new JButton("Pausa");
        btTerminarC = new JButton("Terminar");

        panelCazador.add(btIniciarC);
        panelCazador.add(btPausaC);
        panelCazador.add(btTerminarC);

        panelNorte.add(panelSerpiente);
        panelNorte.add(panelCazador);

        add(panelNorte, BorderLayout.NORTH);

        // SUR: REBOTADOR Y CLIMA
        JPanel panelSur = new JPanel(
                new GridLayout(1, 2, 5, 5));

        JPanel panelRebotador = new JPanel(new FlowLayout());
        panelRebotador.setBorder(
                BorderFactory.createTitledBorder("Hilo Rebotador"));

        btIniciarR = new JButton("Iniciar");
        btPausaR = new JButton("Pausa");
        btTerminarR = new JButton("Terminar");

        panelRebotador.add(btIniciarR);
        panelRebotador.add(btPausaR);
        panelRebotador.add(btTerminarR);

        JPanel panelClima = new JPanel(new FlowLayout());
        panelClima.setBorder(
                BorderFactory.createTitledBorder("Hilo Clima"));

        btIniciarCl = new JButton("Iniciar");
        btPausaCl = new JButton("Pausa");
        btTerminarCl = new JButton("Terminar");

        panelClima.add(btIniciarCl);
        panelClima.add(btPausaCl);
        panelClima.add(btTerminarCl);

        panelSur.add(panelRebotador);
        panelSur.add(panelClima);

        add(panelSur, BorderLayout.SOUTH);

        // OESTE: LIMPIADOR
        JPanel panelLimpiador = new JPanel(
                new GridLayout(3, 1, 5, 5));

        panelLimpiador.setBorder(
                BorderFactory.createTitledBorder("Limpiador"));

        btIniciarL = new JButton("Iniciar");
        btPausaL = new JButton("Pausa");
        btTerminarL = new JButton("Terminar");

        panelLimpiador.add(btIniciarL);
        panelLimpiador.add(btPausaL);
        panelLimpiador.add(btTerminarL);

        JPanel panelWestContenedor = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        panelWestContenedor.add(panelLimpiador);

        add(panelWestContenedor, BorderLayout.WEST);
    }

    private void asociarEventos() {

        // SERPIENTE
        btIniciarS.addActionListener(e -> iniciarSerpiente());
        btPausaS.addActionListener(e -> pausarSerpiente());
        btTerminarS.addActionListener(e -> terminarSerpiente());

        // CAZADOR
        btIniciarC.addActionListener(e -> iniciarCazador());
        btPausaC.addActionListener(e -> pausarCazador());
        btTerminarC.addActionListener(e -> terminarCazador());

        // REBOTADOR
        btIniciarR.addActionListener(e -> iniciarRebotador());
        btPausaR.addActionListener(e -> pausarRebotador());
        btTerminarR.addActionListener(e -> terminarRebotador());

        // CLIMA
        btIniciarCl.addActionListener(e -> iniciarClima());
        btPausaCl.addActionListener(e -> pausarClima());
        btTerminarCl.addActionListener(e -> terminarClima());

        // LIMPIADOR
        btIniciarL.addActionListener(e -> iniciarLimpiador());
        btPausaL.addActionListener(e -> pausarLimpiador());
        btTerminarL.addActionListener(e -> terminarLimpiador());
    }

    // EVENTOS SERPIENTE
    private void iniciarSerpiente() {
        System.out.println("Evento: Iniciar Serpiente");
    }

    private void pausarSerpiente() {
        System.out.println("Evento: Pausar Serpiente");
    }

    private void terminarSerpiente() {
        System.out.println("Evento: Terminar Serpiente");
    }

    // EVENTOS CAZADOR
    private void iniciarCazador() {
        System.out.println("Evento: Iniciar Cazador");
    }

    private void pausarCazador() {
        System.out.println("Evento: Pausar Cazador");
    }

    private void terminarCazador() {
        System.out.println("Evento: Terminar Cazador");
    }

    // EVENTOS REBOTADOR
    private void iniciarRebotador() {
        System.out.println("Evento: Iniciar Rebotador");
    }

    private void pausarRebotador() {
        System.out.println("Evento: Pausar Rebotador");
    }

    private void terminarRebotador() {
        System.out.println("Evento: Terminar Rebotador");
    }

    // EVENTOS CLIMA
    private void iniciarClima() {
        System.out.println("Evento: Iniciar Clima");
    }

    private void pausarClima() {
        System.out.println("Evento: Pausar Clima");
    }

    private void terminarClima() {
        System.out.println("Evento: Terminar Clima");
    }

    // EVENTOS LIMPIADOR
    private void iniciarLimpiador() {
        System.out.println("Evento: Iniciar Limpiador");
    }

    private void pausarLimpiador() {
        System.out.println("Evento: Pausar Limpiador");
    }

    private void terminarLimpiador() {
        System.out.println("Evento: Terminar Limpiador");
    }

    // ACCESO A LA MATRIZ
    public JLabel[][] getMatrizLabels() {
        return matriz;
    }
}
