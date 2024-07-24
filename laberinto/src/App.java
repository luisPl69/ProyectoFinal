import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String filasStr = JOptionPane.showInputDialog("Ingrese el número de filas:");
                String columnasStr = JOptionPane.showInputDialog("Ingrese el número de columnas:");

                int filas = Integer.parseInt(filasStr);
                int columnas = Integer.parseInt(columnasStr);

                LaberintoModel modelo = new LaberintoModel(filas, columnas);
                LaberintoView vista = new LaberintoView(filas, columnas);
                LaberintoController controlador = new LaberintoController(modelo, vista);
                controlador.iniciar();
            }
        });
    }
}

