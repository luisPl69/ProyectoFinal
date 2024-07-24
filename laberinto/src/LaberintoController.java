import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

public class LaberintoController {
    private LaberintoModel modelo;
    private LaberintoView vista;
    private boolean modoEdicion = true;
    private final Color colorMuro = Color.black;
    private final Color colorPiso = Color.white;
    private final Color colorJugador = Color.green;
    private final Color colorMeta = Color.yellow;
    private final Color colorRecorrido = Color.green; // Color del recorrido
    private String tipoRecorridoSeleccionado; // Variable para almacenar el tipo de recorrido seleccionado

    public LaberintoController(LaberintoModel modelo, LaberintoView vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.agregarBotonFinalizarEdicion(new FinalizarEdicionListener());
        agregarActionListeners();
    }

    public void iniciar() {
        modoEdicion = true;
        limpiarMuroYRecorrido(); // Asegúrate de limpiar el laberinto al iniciar la edición
    }

    private void agregarActionListeners() {
        JButton[][] botones = vista.obtenerBotones();
        for (int i = 0; i < botones.length; i++) {
            for (int j = 0; j < botones[i].length; j++) {
                botones[i][j].addActionListener(new BotonListener());
            }
        }
    }

    class FinalizarEdicionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            modoEdicion = false;
            int jugadorFila = Integer.parseInt(vista.solicitarEntrada("Ingrese la fila inicial del jugador:"));
            int jugadorColumna = Integer.parseInt(vista.solicitarEntrada("Ingrese la columna inicial del jugador:"));
            vista.actualizarBoton(jugadorFila, jugadorColumna, colorJugador);
            modelo.setJugadorPosicion(jugadorFila, jugadorColumna);

            int metaFila = Integer.parseInt(vista.solicitarEntrada("Ingrese la fila de la meta:"));
            int metaColumna = Integer.parseInt(vista.solicitarEntrada("Ingrese la columna de la meta:"));
            vista.actualizarBoton(metaFila, metaColumna, colorMeta);
            modelo.setCelda(metaFila, metaColumna, 3);

            // Solicitar el tipo de recorrido y mostrar el camino
            elegirMetodoRecorrido();
        }
    }

    private void elegirMetodoRecorrido() {
        String[] opciones = {"Recursivo", "Con Cache", "BFS", "DFS"};
        tipoRecorridoSeleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione el tipo de recorrido:",
                "Tipo de Recorrido", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        List<Celda> camino = null;

        switch (tipoRecorridoSeleccionado) {
            case "Recursivo":
                camino = modelo.findPathRecursively();
                break;
            case "Con Cache":
                camino = modelo.findPathWithCache();
                break;
            case "BFS":
                camino = modelo.findPathBFS();
                break;
            case "DFS":
                camino = modelo.findPathDFS();
                break;
        }

        if (camino != null && !camino.isEmpty()) {
            mostrarRecorrido(camino);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un camino.");
            limpiarMuroYRecorrido(); // Limpiar muros, meta y jugador si no se encuentra un camino
            iniciar(); // Reiniciar modo edición para permitir agregar nuevos muros
        }
    }

    private void mostrarRecorrido(List<Celda> camino) {
        Timer timer = new Timer(300, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < camino.size()) {
                    Celda celda = camino.get(index);
                    vista.actualizarBoton(celda.getFila(), celda.getColumna(), colorRecorrido);
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();
                    // Mostrar las coordenadas del recorrido y el tipo de recorrido seleccionado
                    StringBuilder recorridoInfo = new StringBuilder("Tipo de recorrido: " + tipoRecorridoSeleccionado + "\nCoordenadas del recorrido:\n");
                    for (Celda celda : camino) {
                        recorridoInfo.append("[").append(celda.getFila()).append(", ").append(celda.getColumna()).append("]\n");
                    }
                    JOptionPane.showMessageDialog(null, recorridoInfo.toString(), "Recorrido Completo", JOptionPane.INFORMATION_MESSAGE);
                    limpiarRecorrido();
                    elegirMetodoRecorrido(); // Permitir al usuario seleccionar otro método
                }
            }
        });
        timer.start();
    }

    private void limpiarRecorrido() {
        int filas = vista.obtenerBotones().length;
        int columnas = vista.obtenerBotones()[0].length;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton boton = vista.obtenerBotones()[i][j];
                if (boton.getBackground().equals(colorRecorrido)) {
                    vista.actualizarBoton(i, j, colorPiso); // Restaurar al color de piso
                    modelo.setCelda(i, j, 0); // Restaurar el valor en el modelo
                }
            }
        }
    }

    private void limpiarMuroYRecorrido() {
        int filas = vista.obtenerBotones().length;
        int columnas = vista.obtenerBotones()[0].length;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton boton = vista.obtenerBotones()[i][j];
                if (boton.getBackground().equals(colorMuro)) {
                    vista.actualizarBoton(i, j, colorPiso); // Restaurar al color de piso
                    modelo.setCelda(i, j, 0); // Restaurar el valor en el modelo
                } else if (boton.getBackground().equals(colorMeta)) {
                    vista.actualizarBoton(i, j, colorPiso); // Restaurar al color de piso
                    modelo.setCelda(i, j, 0); // Restaurar el valor en el modelo
                } else if (boton.getBackground().equals(colorJugador)) {
                    vista.actualizarBoton(i, j, colorPiso); // Restaurar al color de piso
                    modelo.setJugadorPosicion(-1, -1); // Reiniciar la posición del jugador
                }
            }
        }
    }

    class BotonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton botonPushado = (JButton) e.getSource();
            int[] coordenadas = obtenerCoordenadasBoton(botonPushado);
            int fila = coordenadas[0];
            int columna = coordenadas[1];
            Color botonColor = botonPushado.getBackground();

            if (modoEdicion) {
                if (botonColor.equals(colorPiso)) {
                    vista.actualizarBoton(fila, columna, colorMuro);
                    modelo.setCelda(fila, columna, 1);
                } else if (botonColor.equals(colorMuro)) {
                    vista.actualizarBoton(fila, columna, colorPiso);
                    modelo.setCelda(fila, columna, 0);
                }
            } else {
                // En modo no edición, no permitir el movimiento manual
                if (botonColor.equals(colorMuro)) {
                    vista.mostrarMensaje("Esto es un muro");
                } else if (botonColor.equals(colorJugador)) {
                    vista.mostrarMensaje("Esto ya es el jugador");
                }
            }
        }

        private int[] obtenerCoordenadasBoton(JButton botonPushado) {
            for (int i = 0; i < vista.obtenerBotones().length; i++) {
                for (int j = 0; j < vista.obtenerBotones()[0].length; j++) {
                    if (vista.obtenerBotones()[i][j].equals(botonPushado)) {
                        return new int[]{i, j};
                    }
                }
            }
            return new int[]{-1, -1};
        }
    }
}
