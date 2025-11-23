import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MatrizInversa {

    public static void main(String[] args) {
        Path archivoEntrada = Paths.get("input.txt");
        Path archivoSalida = Paths.get("output.txt");

        try {
            // 1. LEER DATOS Y CREAR LA MATRIZ
            double[][] matriz = leerMatriz(archivoEntrada);
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("--- MATRIZ ORIGINAL ---\n");
            reporte.append(matrizToString(matriz)).append("\n");

            // 2. VERIFICACIONES MATEMÁTICAS
            if (matriz.length != matriz[0].length) {
                throw new Exception("La matriz no es cuadrada. No tiene inversa.");
            }

            double det = determinante(matriz);
            reporte.append("Determinante: ").append(det).append("\n\n");

            if (det == 0) {
                reporte.append("ERROR: El determinante es 0. La matriz NO tiene inversa.");
            } else {
                // 3. CALCULAR INVERSA
                double[][] inversa = calcularInversa(matriz, det);
                
                reporte.append("--- MATRIZ INVERSA ---\n");
                reporte.append(matrizToString(inversa));
            }

            // 4. ESCRIBIR RESULTADO
            Files.write(archivoSalida, reporte.toString().getBytes());
            System.out.println("¡Cálculo completado! Revisa 'output.txt'");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LÓGICA MATEMÁTICA ---

    public static double[][] calcularInversa(double[][] matriz, double det) {
        int n = matriz.length;
        double[][] adjunta = calcularAdjunta(matriz);
        double[][] inversa = new double[n][n];

        // La inversa es la Adjunta dividida por el Determinante
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inversa[i][j] = adjunta[i][j] / det;
            }
        }
        return inversa;
    }

    public static double[][] calcularAdjunta(double[][] matriz) {
        int n = matriz.length;
        double[][] adjunta = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // El signo se alterna (+ - + -)
                int signo = ((i + j) % 2 == 0) ? 1 : -1;
                
                // Obtenemos la submatriz eliminando fila i y columna j
                double[][] subMatriz = crearSubMatriz(matriz, i, j);
                
                // NOTA IMPORTANTE: Para la adjunta, transponemos los índices.
                // El valor calculado para (i,j) se guarda en (j,i)
                adjunta[j][i] = signo * determinante(subMatriz);
            }
        }
        return adjunta;
    }

    // Función recursiva para el determinante
    public static double determinante(double[][] matriz) {
        int n = matriz.length;
        if (n == 1) return matriz[0][0];
        if (n == 2) return (matriz[0][0] * matriz[1][1]) - (matriz[0][1] * matriz[1][0]);

        double det = 0;
        for (int j = 0; j < n; j++) {
            double[][] subMatriz = crearSubMatriz(matriz, 0, j);
            int signo = (j % 2 == 0) ? 1 : -1;
            det += signo * matriz[0][j] * determinante(subMatriz);
        }
        return det;
    }

    // Helper para eliminar una fila y una columna (necesario para cofactores)
    public static double[][] crearSubMatriz(double[][] matriz, int filaExcluida, int colExcluida) {
        int n = matriz.length;
        double[][] nuevaMatriz = new double[n - 1][n - 1];
        int r = -1;
        for (int i = 0; i < n; i++) {
            if (i == filaExcluida) continue;
            r++;
            int c = -1;
            for (int j = 0; j < n; j++) {
                if (j == colExcluida) continue;
                nuevaMatriz[r][++c] = matriz[i][j];
            }
        }
        return nuevaMatriz;
    }

    // --- PERSISTENCIA Y UTILIDADES ---

    public static double[][] leerMatriz(Path archivo) throws IOException {
        List<String> lineas = Files.readAllLines(archivo);
        int filas = lineas.size();
        int cols = lineas.get(0).split(",").length;
        
        double[][] matriz = new double[filas][cols];
        
        for (int i = 0; i < filas; i++) {
            String[] valores = lineas.get(i).split(",");
            for (int j = 0; j < cols; j++) {
                matriz[i][j] = Double.parseDouble(valores[j].trim());
            }
        }
        return matriz;
    }

    public static String matrizToString(double[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (double[] fila : matriz) {
            sb.append("[ ");
            for (double valor : fila) {
                // Formateamos a 2 decimales para que se vea bonito
                sb.append(String.format("%.2f", valor)).append(" ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}