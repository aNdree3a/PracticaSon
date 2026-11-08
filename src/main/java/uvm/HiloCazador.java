package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloCazador extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloCazador.class.getName());

    private final JLabel[][] matriz;

    private volatile boolean ejecutando = true;
    private volatile boolean pausado = false;

    private int cazados = 0;

    public HiloCazador(JLabel[][] matriz) {
        if (matriz == null || matriz.length == 0) {
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
                recorrerMatriz();
                Thread.sleep(100);
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

    private void recorrerMatriz() throws InterruptedException {
        for (int fila = 0; fila < matriz.length && ejecutando; fila++) {

            recorrerFila(fila);
        }
    }

    private void recorrerFila(int fila) throws InterruptedException {
        for (int columna = 0; columna < matriz[fila].length && ejecutando; columna++) {

            esperarSiEstaPausado();

            if (ejecutando && contieneObjetivo(fila, columna)) {
                cazarObjetivo(matriz[fila][columna]);
            }
        }
    }

    private boolean contieneObjetivo(int fila, int columna)
            throws InterruptedException {

        AtomicReference<Boolean> encontrado = new AtomicReference<>(false);

        ejecutarEnSwing(() -> {
            String texto = matriz[fila][columna].getText();
            encontrado.set(texto != null && texto.contains("@"));
        });

        return encontrado.get();
    }

    private void cazarObjetivo(JLabel celda)
            throws InterruptedException {

        Color colorOriginal = obtenerColor(celda);

        cambiarColor(celda, Color.RED);
        Thread.sleep(300);

        cambiarVisibilidad(celda, false);
        Thread.sleep(300);

        mostrarCelda(celda);
        Thread.sleep(400);

        reemplazarObjetivo(celda, colorOriginal);
        registrarCaptura();
    }

    private Color obtenerColor(JLabel celda)
            throws InterruptedException {

        AtomicReference<Color> color = new AtomicReference<>(Color.BLACK);

        ejecutarEnSwing(() -> color.set(celda.getForeground()));

        return color.get();
    }

    private void cambiarColor(JLabel celda, Color color)
            throws InterruptedException {

        ejecutarEnSwing(() -> celda.setForeground(color));
    }

    private void cambiarVisibilidad(JLabel celda, boolean visible)
            throws InterruptedException {

        ejecutarEnSwing(() -> celda.setVisible(visible));
    }

    private void mostrarCelda(JLabel celda)
            throws InterruptedException {

        ejecutarEnSwing(() -> {
            celda.setVisible(true);
            celda.setForeground(Color.RED);
        });
    }

    private void reemplazarObjetivo(
            JLabel celda, Color colorOriginal)
            throws InterruptedException {

        ejecutarEnSwing(() -> {
            String texto = celda.getText();

            if (texto != null) {
                celda.setText(texto.replace("@", "X"));
            }

            celda.setForeground(colorOriginal);
        });
    }

    private void ejecutarEnSwing(Runnable tarea)
            throws InterruptedException {

        try {
            SwingUtilities.invokeAndWait(tarea);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE,
                    "Error al actualizar la matriz", e);
        }
    }

    private void registrarCaptura() {
        cazados++;

        LOGGER.log(Level.INFO,
                "Cazador -> Total de '@' capturados: {0}",
                cazados);
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void reanudar() {
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