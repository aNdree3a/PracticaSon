
package uvm;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloLimpiador extends Thread {

    private final JButton[][] matriz;
    private final String[][] textoOriginal;
    private final Color[][] colorOriginal;

    private volatile boolean ejecutando = true;
    private boolean pausado = false;

    public HiloLimpiador(JButton[][] matriz) {
        if (matriz == null || matriz.length == 0
                || matriz[0] == null || matriz[0].length == 0) {
            throw new IllegalArgumentException(
                "La matriz no puede estar vacía."
            );
        }

        this.matriz = matriz;

        int filas = matriz.length;
        int columnas = matriz[0].length;

        textoOriginal = new String[filas][columnas];
        colorOriginal = new Color[filas][columnas];

        // Guardar el estado inicial de cada botón
        for (int i = 0; i < filas; i++) {
            if (matriz[i] == null || matriz[i].length != columnas) {
                throw new IllegalArgumentException(
                    "La matriz debe ser rectangular."
                );
            }

            for (int j = 0; j < columnas; j++) {
                if (matriz[i][j] == null) {
                    throw new IllegalArgumentException(
                        "Hay una celda sin inicializar."
                    );
                }

                textoOriginal[i][j] = matriz[i][j].getText();
                colorOriginal[i][j] = matriz[i][j].getForeground();
            }
        }

        setName("Hilo-Limpiador");
    }

    public synchronized void terminar() {
        ejecutando = false;
        pausado = false;
        notifyAll();
        interrupt();
    }

    public synchronized void pausarHilo() {
        pausado = true;
    }

    public synchronized void continuarHilo() {
        pausado = false;
        notifyAll();
    }

    private synchronized void esperarSiPausado()
            throws InterruptedException {
        while (pausado && ejecutando) {
            wait();
        }
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                esperarSiPausado();

                if (!ejecutando) {
                    break;
                }

                revisarMatriz();

                Thread.sleep(500);

            } catch (InterruptedException e) {
                if (!ejecutando) {
                    break;
                }
            }
        }
    }

    private void revisarMatriz() throws InterruptedException {
        final int filas = matriz.length;
        final int columnas = matriz[0].length;

        final int[] modificadas = {0};

        // Leer los botones en el hilo de eventos de Swing
        SwingUtilities.invokeAndWait(() -> {
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    JButton celda = matriz[i][j];

                    boolean textoCambiado =
                        !celda.getText().equals(textoOriginal[i][j]);

                    boolean colorCambiado =
                        !celda.getForeground().equals(colorOriginal[i][j]);

                    if (textoCambiado || colorCambiado) {
                        modificadas[0]++;
                    }
                }
            }
        });

        int totalCeldas = filas * columnas;

        // Limpiar cuando más del 50 % esté modificado
        if (modificadas[0] > totalCeldas / 2.0) {
            limpiarBloque();
        }
    }

    private void limpiarBloque() throws InterruptedException {
        int filas = matriz.length;
        int columnas = matriz[0].length;

        // Seleccionar el bloque central
        int inicioFila = filas / 4;
        int finFila = (filas * 3) / 4;
        int inicioColumna = columnas / 4;
        int finColumna = (columnas * 3) / 4;

        SwingUtilities.invokeAndWait(() -> {
            for (int i = inicioFila; i < finFila; i++) {
                for (int j = inicioColumna; j < finColumna; j++) {
                    matriz[i][j].setText(textoOriginal[i][j]);
                    matriz[i][j].setForeground(colorOriginal[i][j]);
                }
            }
        });
    }
}