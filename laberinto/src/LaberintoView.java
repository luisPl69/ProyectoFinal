import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class LaberintoView {
    private JFrame frame;
    private JButton[][] botones;
    private JButton finalizarEdicionBtn;
    private JPanel panel;
    private int filas;
    private int columnas;

    public LaberintoView(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        frame = new JFrame("Laberinto");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        panel = new JPanel(new GridLayout(filas, columnas));
        botones = new JButton[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setBackground(Color.white);
                botones[i][j].setOpaque(true);
                botones[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                panel.add(botones[i][j]);
            }
        }
        frame.add(panel, BorderLayout.CENTER);

        finalizarEdicionBtn = new JButton("Finalizar Edición");
        frame.add(finalizarEdicionBtn, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void agregarBotonFinalizarEdicion(ActionListener listener) {
        finalizarEdicionBtn.addActionListener(listener);
    }

    public JButton[][] obtenerBotones() {
        return botones;
    }

    public void actualizarBoton(int fila, int columna, Color color) {
        botones[fila][columna].setBackground(color);
    }

    public String solicitarEntrada(String mensaje) {
        return JOptionPane.showInputDialog(frame, mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(frame, mensaje);
    }

    // Método para actualizar la vista con el recorrido completo y el camino más rápido
    public void actualizarLaberinto(Map<String, List<Celda>> paths) {
        // Primero, limpiamos el laberinto
        limpiarLaberinto();

        List<Celda> recorridoCompleto = paths.get("recorridoCompleto");
        List<Celda> caminoMasRapido = paths.get("caminoMasRapido");

        
        if (recorridoCompleto != null) {
            for (Celda celda : recorridoCompleto) {
                actualizarBoton(celda.getFila(), celda.getColumna(), Color.GREEN);
            }
        }

        
        if (caminoMasRapido != null) {
            for (Celda celda : caminoMasRapido) {
                actualizarBoton(celda.getFila(), celda.getColumna(), Color.PINK);
            }
        }
    }

    // Método para limpiar el laberinto, restaurando el color de fondo original
    private void limpiarLaberinto() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (botones[i][j].getBackground() == Color.GREEN ||
                    botones[i][j].getBackground() == Color.PINK) {
                    botones[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
}
