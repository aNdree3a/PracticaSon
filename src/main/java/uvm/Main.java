package uvm;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaMatrizHilos ventana = new VentanaMatrizHilos();
            ventana.setVisible(true);
        });
    }
}