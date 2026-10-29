
package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloSerpiente extends Thread {

    private final JLabel[][] matriz;

    private volatile boolean ejecutando = true;
    private volatile boolean pausado = false;

    private int filaActual;
    private int columnaActual;
    private int numeroActual;

    public HiloSerpiente(JLabel[][] matriz) {
        this(matriz, 0, 0, 1);
    }

    public HiloSerpiente(
            JLabel[][] matriz,
            int fila,
            int columna,
            int numero) {

        this.matriz = matriz;
        this.filaActual = fila;
        this.columnaActual = columna;
        this.numeroActual = numero;
    }

    @Override
    public void run() {
        int filas = matriz.length;
        int columnas = matriz[0].length;

        while (ejecutando && filaActual < filas) {

            synchronized (this) {
                while (pausado && ejecutando) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (!ejecutando) {
                            return;
                        }
                    }
                }
            }

            if (!ejecutando) {
                break;
            }

            escribirNumero(
                    filaActual,
                    columnaActual,
                    numeroActual);

            numeroActual++;

            // Avanzar en zigzag
            if (filaActual % 2 == 0) {
                columnaActual++;

                if (columnaActual >= columnas) {
                    filaActual++;
                    columnaActual = columnas - 1;
                }
            } else {
                columnaActual--;

                if (columnaActual < 0) {
                    filaActual++;
                    columnaActual = 0;
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                if (!ejecutando) {
                    break;
                }
            }
        }
    }

    private void escribirNumero(int fila, int columna, int numero) {
        SwingUtilities.invokeLater(() -> {
            matriz[fila][columna].setText(String.valueOf(numero));
            matriz[fila][columna].setForeground(Color.BLUE);
            matriz[fila][columna].setHorizontalAlignment(
                    JLabel.CENTER);
        });
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

    public int getFilaActual() {
        return filaActual;
    }

    public int getColumnaActual() {
        return columnaActual;
    }

    public int getNumeroActual() {
        return numeroActual;
    }
}
