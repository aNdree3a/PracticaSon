package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloLimpiador extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloLimpiador.class.getName());

    private static final int LIMITE = 50;
    private static final int TAMANO_BLOQUE = 5;

    private final JLabel[][] matriz;

    private volatile boolean ejecutando = true;
    private volatile boolean pausado = false;

    private boolean limpiezaActivada = false;

    private int bloqueFila = 0;
    private int bloqueColumna = 0;

    public HiloLimpiador(JLabel[][] matriz) {
        this.matriz = Objects.requireNonNull(matriz);

        if (matriz.length == 0 || matriz[0] == null
                || matriz[0].length == 0) {
            throw new IllegalArgumentException(
                    "La matriz no puede estar vacía.");
        }
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                esperarSiEstaPausado();

                if (ejecutando) {
                    revisarYLimpiar();
                    Thread.sleep(500);
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

    private void revisarYLimpiar() throws InterruptedException {
        try {
            SwingUtilities.invokeAndWait(this::procesarMatriz);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE,
                    "Error durante la limpieza de la matriz", e);
        }
    }

    private void procesarMatriz() {
        int modificadas = contarCeldasModificadas();
        int total = obtenerTotalCeldas();
        int porcentaje = modificadas * 100 / total;

        LOGGER.log(Level.INFO,
                "Limpiador -> Celdas modificadas: {0}%",
                porcentaje);

        verificarLimpieza(porcentaje);
    }

    private int obtenerTotalCeldas() {
        int total = 0;

        for (JLabel[] fila : matriz) {
            total += fila.length;
        }

        return total;
    }

    private int contarCeldasModificadas() {
        int contador = 0;

        for (int fila = 0; fila < matriz.length; fila++) {
            contador += contarCeldasDeFila(fila);
        }

        return contador;
    }

    private int contarCeldasDeFila(int fila) {
        int contador = 0;

        for (int columna = 0; columna < matriz[fila].length; columna++) {

            if (celdaModificada(matriz[fila][columna])) {
                contador++;
            }
        }

        return contador;
    }

    private boolean celdaModificada(JLabel celda) {
        boolean textoModificado = !"-".equals(celda.getText());
        boolean colorModificado = !Color.BLACK.equals(celda.getForeground());
        boolean fondoModificado = !Color.WHITE.equals(celda.getBackground());

        return textoModificado || colorModificado || fondoModificado;
    }

    private void verificarLimpieza(int porcentaje) {
        if (porcentaje > LIMITE && !limpiezaActivada) {
            limpiarBloque();
            limpiezaActivada = true;

            LOGGER.info(
                    "Limpiador -> Se limpió un bloque de la matriz.");
        }

        if (porcentaje <= LIMITE) {
            limpiezaActivada = false;
        }
    }

    private void limpiarBloque() {
        int inicioFila = bloqueFila * TAMANO_BLOQUE;
        int inicioColumna = bloqueColumna * TAMANO_BLOQUE;

        limpiarFilasDelBloque(inicioFila, inicioColumna);
        avanzarBloque();
    }

    private void limpiarFilasDelBloque(
            int inicioFila, int inicioColumna) {

        int finFila = Math.min(
                inicioFila + TAMANO_BLOQUE, matriz.length);

        for (int fila = inicioFila; fila < finFila; fila++) {
            limpiarCeldasDeFila(fila, inicioColumna);
        }
    }

    private void limpiarCeldasDeFila(
            int fila, int inicioColumna) {

        int finColumna = Math.min(
                inicioColumna + TAMANO_BLOQUE,
                matriz[fila].length);

        for (int columna = inicioColumna; columna < finColumna; columna++) {

            restaurarCelda(matriz[fila][columna]);
        }
    }

    private void restaurarCelda(JLabel celda) {
        celda.setText("-");
        celda.setForeground(Color.BLACK);
        celda.setBackground(Color.WHITE);
        celda.setVisible(true);
    }

    private void avanzarBloque() {
        int bloquesColumnas = (matriz[0].length + TAMANO_BLOQUE - 1)
                / TAMANO_BLOQUE;

        int bloquesFilas = (matriz.length + TAMANO_BLOQUE - 1)
                / TAMANO_BLOQUE;

        bloqueColumna++;

        if (bloqueColumna >= bloquesColumnas) {
            bloqueColumna = 0;
            bloqueFila++;
        }

        if (bloqueFila >= bloquesFilas) {
            bloqueFila = 0;
        }
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