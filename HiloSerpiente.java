
package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
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

    public HiloSerpiente(JLabel[][] matriz, int fila, int columna, int numero) {
        this.matriz = matriz;
        this.filaActual = fila;
        this.columnaActual = columna;
        this.numeroActual = numero;
    }

    @Override
    public void run() {
        while (ejecutando && filaActual < matriz.length) {
            try {
                esperarSiEstaPausado();

                if (ejecutando && filaActual < matriz.length) {
                    escribirYAvanzar();
                    Thread.sleep(200);
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

    private void escribirYAvanzar() {
        escribirNumero(filaActual, columnaActual, numeroActual);
        numeroActual++;
        avanzarPosicion();
    }

    private void avanzarPosicion() {
        int columnas = matriz[0].length;

        if (filaActual % 2 == 0) {
            avanzarDerecha(columnas);
        } else {
            avanzarIzquierda();
        }
    }

    private void avanzarDerecha(int columnas) {
        columnaActual++;

        if (columnaActual >= columnas) {
            filaActual++;
            columnaActual = columnas - 1;
        }
    }

    private void avanzarIzquierda() {
        columnaActual--;

        if (columnaActual < 0) {
            filaActual++;
            columnaActual = 0;
        }
    }

    private void escribirNumero(int fila, int columna, int numero) {
        SwingUtilities.invokeLater(() -> {
            JLabel celda = matriz[fila][columna];
            celda.setText(String.valueOf(numero));
            celda.setForeground(Color.BLUE);
            celda.setHorizontalAlignment(SwingConstants.CENTER);
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
