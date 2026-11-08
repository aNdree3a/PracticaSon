package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloRebotador extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloRebotador.class.getName());

    private final JLabel[][] matriz;

    private volatile boolean ejecutando = true;
    private volatile boolean pausado = false;

    private int fila = 0;
    private int columna = 0;

    private int direccionFila = 1;
    private int direccionColumna = 1;

    private int filaAnterior = -1;
    private int columnaAnterior = -1;

    public HiloRebotador(JLabel[][] matriz) {
        if (matriz == null || matriz.length == 0
                || matriz[0] == null || matriz[0].length == 0) {
            throw new IllegalArgumentException(
                    "La matriz no puede estar vacía.");
        }

        this.matriz = matriz;
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                esperarSiEstaPausado();

                if (ejecutando) {
                    mover();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private synchronized void esperarSiEstaPausado()
            throws InterruptedException {
        while (pausado && ejecutando) {
            wait();
        }
    }

    private void mover() throws InterruptedException {
        try {
            SwingUtilities.invokeAndWait(() -> {
                if (filaAnterior >= 0 && columnaAnterior >= 0) {
                    JLabel anterior = matriz[filaAnterior][columnaAnterior];

                    if ("@".equals(anterior.getText())) {
                        anterior.setText("-");
                        anterior.setForeground(Color.BLACK);
                    }
                }

                JLabel actual = matriz[fila][columna];
                actual.setText("@");
                actual.setForeground(Color.BLACK);
            });
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE,
                    "Error al mover el símbolo del rebotador", e);
        }

        filaAnterior = fila;
        columnaAnterior = columna;

        calcularSiguientePosicion();
    }

    private void calcularSiguientePosicion() {
        int siguienteFila = fila + direccionFila;
        int siguienteColumna = columna + direccionColumna;

        if (siguienteFila < 0 || siguienteFila >= matriz.length) {
            direccionFila *= -1;
        }

        if (siguienteColumna < 0
                || siguienteColumna >= matriz[0].length) {
            direccionColumna *= -1;
        }

        fila += direccionFila;
        columna += direccionColumna;
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void continuar() {
        pausado = false;
        notifyAll();
    }

    public synchronized void terminar() {
        ejecutando = false;
        pausado = false;
        notifyAll();
        interrupt();
    }
}