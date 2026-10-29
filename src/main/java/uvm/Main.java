package uvm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static HiloSerpiente serpiente;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaMatrizHilos ventana = new VentanaMatrizHilos();

            conectarBotones(ventana);

            ventana.setVisible(true);
        });
    }

    private static void conectarBotones(VentanaMatrizHilos ventana) {

        List<Component> componentes = obtenerComponentes(ventana.getContentPane());

        JPanel panelSerpiente = null;

        // Buscar el panel de Hilo Serpiente
        for (Component componente : componentes) {
            if (componente instanceof JPanel) {
                JPanel panel = (JPanel) componente;

                if (panel.getBorder() instanceof TitledBorder) {
                    TitledBorder borde = (TitledBorder) panel.getBorder();

                    if ("Hilo Serpiente".equalsIgnoreCase(
                            borde.getTitle())) {
                        panelSerpiente = panel;
                        break;
                    }
                }
            }
        }

        if (panelSerpiente == null) {
            System.out.println(
                    "No se encontró el panel Hilo Serpiente.");
            return;
        }

        JButton botonIniciar = null;
        JButton botonPausa = null;
        JButton botonTerminar = null;

        // Buscar los tres botones
        for (Component componente : obtenerComponentes(panelSerpiente)) {

            if (componente instanceof JButton) {
                JButton boton = (JButton) componente;
                String texto = boton.getText();

                if ("Iniciar".equalsIgnoreCase(texto)) {
                    botonIniciar = boton;
                } else if ("Pausa".equalsIgnoreCase(texto)) {
                    botonPausa = boton;
                } else if ("Terminar".equalsIgnoreCase(texto)) {
                    botonTerminar = boton;
                }
            }
        }

        if (botonIniciar == null ||
                botonPausa == null ||
                botonTerminar == null) {

            System.out.println(
                    "No se encontraron todos los botones.");
            return;
        }

        // Quitar los eventos originales
        for (ActionListener listener : botonIniciar.getActionListeners()) {
            botonIniciar.removeActionListener(listener);
        }

        for (ActionListener listener : botonPausa.getActionListeners()) {
            botonPausa.removeActionListener(listener);
        }

        for (ActionListener listener : botonTerminar.getActionListeners()) {
            botonTerminar.removeActionListener(listener);
        }

        // BOTÓN INICIAR
        botonIniciar.addActionListener(e -> {

            if (serpiente == null || !serpiente.isAlive()) {

                JLabel[][] matriz = ventana.getMatrizLabels();

                // Limpiar la matriz
                for (int fila = 0; fila < matriz.length; fila++) {
                    for (int columna = 0; columna < matriz[fila].length; columna++) {

                        matriz[fila][columna].setText("-");
                        matriz[fila][columna].setForeground(
                                Color.BLUE);
                    }
                }

                // Crear un hilo nuevo desde el número 1
                serpiente = new HiloSerpiente(matriz);
                serpiente.start();

            } else {
                // Si está pausado, continuar
                serpiente.continuar();
            }
        });

        // BOTÓN PAUSA
        botonPausa.addActionListener(e -> {
            if (serpiente != null && serpiente.isAlive()) {
                serpiente.pausar();
            }
        });

        // BOTÓN TERMINAR
        botonTerminar.addActionListener(e -> {
            if (serpiente != null) {
                serpiente.terminar();
                serpiente = null;
            }
        });
    }

    // Obtener los componentes de la ventana
    private static List<Component> obtenerComponentes(
            Container contenedor) {

        List<Component> componentes = new ArrayList<>();

        for (Component componente : contenedor.getComponents()) {
            componentes.add(componente);

            if (componente instanceof Container) {
                componentes.addAll(
                        obtenerComponentes((Container) componente));
            }
        }

        return componentes;
    }
}
