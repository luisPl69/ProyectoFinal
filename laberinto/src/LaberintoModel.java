import java.util.*;

public class LaberintoModel {
    private int[][] mapa;
    private int[] jugadorPosicion = {-1, -1};

    public LaberintoModel(int filas, int columnas) {
        this.mapa = new int[filas][columnas];
        // Inicializar el laberinto con pisos (0)
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                this.mapa[i][j] = 0;
            }
        }
    }

    public int[][] getMapa() {
        return mapa;
    }

    public void setCelda(int fila, int columna, int valor) {
        this.mapa[fila][columna] = valor;
    }

    public int[] getJugadorPosicion() {
        return jugadorPosicion;
    }

    public void setJugadorPosicion(int fila, int columna) {
        this.jugadorPosicion[0] = fila;
        this.jugadorPosicion[1] = columna;
    }

    // Método recursivo simple
    public Map<String, List<Celda>> findPathRecursively() {
        List<Celda> visited = new ArrayList<>();
        List<Celda> fullPath = new ArrayList<>();
        int[][] gridCopy = copyGrid(mapa); // Copia del mapa para evitar modificaciones destructivas
        findPath(gridCopy, jugadorPosicion[0], jugadorPosicion[1], visited, fullPath);
        Map<String, List<Celda>> results = new HashMap<>();
        results.put("caminoFinal", visited);
        results.put("recorridoCompleto", fullPath);
        return results;
    }

    private boolean findPath(int[][] grid, int row, int col, List<Celda> visited, List<Celda> fullPath) {
        if (row == grid.length - 1 && col == grid[0].length - 1) {
            visited.add(new Celda(row, col));
            fullPath.add(new Celda(row, col));
            return true;
        }

        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != 0) {
            return false;
        }

        grid[row][col] = -1; // Marcar como visitado
        visited.add(new Celda(row, col));
        fullPath.add(new Celda(row, col));

        if (findPath(grid, row + 1, col, visited, fullPath) || findPath(grid, row - 1, col, visited, fullPath)
                || findPath(grid, row, col + 1, visited, fullPath) || findPath(grid, row, col - 1, visited, fullPath)) {
            return true;
        }

        grid[row][col] = 0; // Desmarcar si no se encontró un camino
        visited.remove(visited.size() - 1);
        return false;
    }

    private int[][] copyGrid(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    // Método aplicando cache (programación dinámica)
    public Map<String, List<Celda>> findPathWithCache() {
        List<Celda> visited = new ArrayList<>();
        List<Celda> fullPath = new ArrayList<>();
        Boolean[][] cache = new Boolean[mapa.length][mapa[0].length];
        int[][] gridCopy = copyGrid(mapa); // Copia del mapa
        findPathWithCache(gridCopy, jugadorPosicion[0], jugadorPosicion[1], visited, fullPath, cache);
        Map<String, List<Celda>> results = new HashMap<>();
        results.put("caminoFinal", visited);
        results.put("recorridoCompleto", fullPath);
        return results;
    }

    private boolean findPathWithCache(int[][] grid, int row, int col, List<Celda> visited, List<Celda> fullPath, Boolean[][] cache) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != 0) {
            return false;
        }

        if (row == grid.length - 1 && col == grid[0].length - 1) {
            visited.add(new Celda(row, col));
            fullPath.add(new Celda(row, col));
            return true;
        }

        if (cache[row][col] != null) {
            return cache[row][col];
        }

        visited.add(new Celda(row, col));
        fullPath.add(new Celda(row, col));
        grid[row][col] = -1; // Marcar como visitado

        boolean result = findPathWithCache(grid, row + 1, col, visited, fullPath, cache)
                || findPathWithCache(grid, row - 1, col, visited, fullPath, cache)
                || findPathWithCache(grid, row, col + 1, visited, fullPath, cache)
                || findPathWithCache(grid, row, col - 1, visited, fullPath, cache);

        cache[row][col] = result;

        if (!result) {
            visited.remove(visited.size() - 1); // Eliminar del camino si no conduce a la meta
        }

        return result;
    }

    // Método utilizando BFS
    public Map<String, List<Celda>> findPathBFS() {
        List<Celda> path = new ArrayList<>();
        List<Celda> fullPath = new ArrayList<>();
        boolean[][] visitedGrid = new boolean[mapa.length][mapa[0].length];
        Queue<int[]> queue = new LinkedList<>();
        Map<Integer, int[]> parent = new HashMap<>();

        int[] start = jugadorPosicion;
        queue.add(start);
        visitedGrid[start[0]][start[1]] = true;

        // Direcciones de movimiento (arriba, abajo, izquierda, derecha)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int row = pos[0];
            int col = pos[1];
            fullPath.add(new Celda(row, col));

            // Verificar si se ha alcanzado la meta
            if (mapa[row][col] == 3) {
                // Reconstruir el camino desde el destino hasta el origen
                while (row != start[0] || col != start[1]) {
                    path.add(new Celda(row, col));
                    int[] parentPos = parent.get(row * mapa[0].length + col);
                    row = parentPos[0];
                    col = parentPos[1];
                }
                path.add(new Celda(start[0], start[1]));
                java.util.Collections.reverse(path); // Revertir el camino para obtener el camino desde el inicio a la meta
                Map<String, List<Celda>> results = new HashMap<>();
                results.put("caminoFinal", path);
                results.put("recorridoCompleto", fullPath);
                return results;
            }

            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];

                if (newRow >= 0 && newRow < mapa.length && newCol >= 0 && newCol < mapa[0].length
                        && !visitedGrid[newRow][newCol] && mapa[newRow][newCol] != 1) {
                    queue.add(new int[]{newRow, newCol});
                    visitedGrid[newRow][newCol] = true;
                    parent.put(newRow * mapa[0].length + newCol, new int[]{row, col});
                }
            }
        }
        Map<String, List<Celda>> results = new HashMap<>();
        results.put("caminoFinal", path);
        results.put("recorridoCompleto", fullPath);
        return results; // Retorna la lista vacía si no hay camino
    }

    // Método DFS
    public Map<String, List<Celda>> findPathDFS() {
        List<Celda> path = new ArrayList<>();
        List<Celda> fullPath = new ArrayList<>();
        boolean[][] visitedGrid = new boolean[mapa.length][mapa[0].length];
        Stack<int[]> stack = new Stack<>();
        int[][] parent = new int[mapa.length * mapa[0].length][2];

        stack.push(jugadorPosicion);
        visitedGrid[jugadorPosicion[0]][jugadorPosicion[1]] = true;

        int[] directions = {-1, 0, 1, 0, 0, -1, 0, 1}; // Direcciones de movimiento (arriba, abajo, izquierda, derecha)

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int row = pos[0];
            int col = pos[1];
            fullPath.add(new Celda(row, col));

            if (row == mapa.length - 1 && col == mapa[0].length - 1) {
                // Reconstruir el camino desde el destino hasta el origen
                while (row != jugadorPosicion[0] || col != jugadorPosicion[1]) {
                    path.add(new Celda(row, col));
                    int tempRow = parent[row * mapa[0].length + col][0];
                    int tempCol = parent[row * mapa[0].length + col][1];
                    row = tempRow;
                    col = tempCol;
                }
                path.add(new Celda(jugadorPosicion[0], jugadorPosicion[1]));
                java.util.Collections.reverse(path); // Revertir el camino para obtener el camino desde el inicio a la meta
                Map<String, List<Celda>> results = new HashMap<>();
                results.put("caminoFinal", path);
                results.put("recorridoCompleto", fullPath);
                return results;
            }

            for (int i = 0; i < 4; i++) {
                int newRow = row + directions[2 * i];
                int newCol = col + directions[2 * i + 1];

                if (newRow >= 0 && newRow < mapa.length && newCol >= 0 && newCol < mapa[0].length
                        && !visitedGrid[newRow][newCol] && mapa[newRow][newCol] != 1) {
                    stack.push(new int[]{newRow, newCol});
                    visitedGrid[newRow][newCol] = true;
                    parent[newRow * mapa[0].length + newCol][0] = row;
                    parent[newRow * mapa[0].length + newCol][1] = col;
                }
            }
        }
        Map<String, List<Celda>> results = new HashMap<>();
        results.put("caminoFinal", path);
        results.put("recorridoCompleto", fullPath);
        return results;
    }

    // Método para obtener el recorrido completo y el camino más rápido
    public Map<String, Map<String, List<Celda>>> obtenerRecorridoYCamino() {
        Map<String, Map<String, List<Celda>>> resultados = new HashMap<>();
        resultados.put("recursivo", findPathRecursively());
        resultados.put("bfs", findPathBFS());
        resultados.put("dfs", findPathDFS());
        resultados.put("cache", findPathWithCache());
        return resultados;
    }
}
