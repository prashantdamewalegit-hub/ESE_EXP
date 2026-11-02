import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class exp10 {

    // Representation of a row payload sent to a worker rank
    static class TaskPayload {
        int workerRank;
        int rowIndex;
        int[] rowData;
        int[][] matrixB;

        TaskPayload(int workerRank, int rowIndex, int[] rowData, int[][] matrixB) {
            this.workerRank = workerRank;
            this.rowIndex = rowIndex;
            this.rowData = rowData;
            this.matrixB = matrixB;
        }
    }

    // Representation of a computed row result gathered by Master
    static class GatherPayload {
        int workerRank;
        int rowIndex;
        int[] computedRow;

        GatherPayload(int workerRank, int rowIndex, int[] computedRow) {
            this.workerRank = workerRank;
            this.rowIndex = rowIndex;
            this.computedRow = computedRow;
        }
    }

    // Worker process task (simulates MPI worker rank execution)
    static class WorkerRankTask implements Callable<GatherPayload> {
        private final TaskPayload payload;

        WorkerRankTask(TaskPayload payload) {
            this.payload = payload;
        }

        @Override
        public GatherPayload call() {
            int n = payload.matrixB.length;
            int[] resultRow = new int[n];

            System.out.println("  [RANK " + payload.workerRank + "] Processing Row R" + payload.rowIndex 
                    + " " + Arrays.toString(payload.rowData));

            // Dot product: C[i][j] = sum_k (A[i][k] * B[k][j])
            for (int col = 0; col < n; col++) {
                int sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += payload.rowData[k] * payload.matrixB[k][col];
                }
                resultRow[col] = sum;
            }

            return new GatherPayload(payload.workerRank, payload.rowIndex, resultRow);
        }
    }

    public static void main(String[] args) {
        String csvFile = "matrix.csv";
        List<int[]> matrixAList = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("     MPI PARALLEL MATRIX MULTIPLICATION (SCATTER-GATHER SIMULATION)       ");
        System.out.println("==========================================================================");

        // Step 1: Ingest Matrix A from CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip CSV header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                int[] row = new int[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    row[i - 1] = Integer.parseInt(tokens[i].trim());
                }
                matrixAList.add(row);
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        int n = matrixAList.size();
        int[][] matrixA = new int[n][n];
        for (int i = 0; i < n; i++) {
            matrixA[i] = matrixAList.get(i);
        }

        // Define Matrix B (Static 4x4 matrix for multiplication)
        int[][] matrixB = {
            {1, 2, 3, 1},
            {0, 1, 2, 3},
            {2, 0, 1, 1},
            {1, 3, 0, 2}
        };

        // Display Inputs
        System.out.println("\n[MASTER] Input Matrix A (" + n + "x" + n + "):");
        printMatrix(matrixA);

        System.out.println("\n[MASTER] Multiplier Matrix B (" + n + "x" + n + ") [BROADCAST TO WORKERS]:");
        printMatrix(matrixB);

        // Step 2: MPI Scatter Phase (Master partitions and dispatches rows to worker ranks)
        System.out.println("\n--- MPI SCATTER PHASE: MASTER DISPATCHES ROWS ---");
        int numWorkers = n; // 1 worker rank per row
        ExecutorService workerPool = Executors.newFixedThreadPool(numWorkers);
        List<Future<GatherPayload>> futures = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int workerRank = i + 1; // Rank 0 is Master; Workers are Ranks 1..N
            System.out.println("[MPI_SCATTER] Master -> Rank " + workerRank + " : Row R" + i + " " + Arrays.toString(matrixA[i]));
            TaskPayload payload = new TaskPayload(workerRank, i, matrixA[i], matrixB);
            futures.add(workerPool.submit(new WorkerRankTask(payload)));
        }

        // Step 3: MPI Gather Phase (Master gathers partial computed rows)
        System.out.println("\n--- MPI GATHER PHASE: MASTER REASSEMBLES RESULTS ---");
        int[][] matrixC = new int[n][n];

        try {
            for (Future<GatherPayload> future : futures) {
                GatherPayload result = future.get();
                matrixC[result.rowIndex] = result.computedRow;
                System.out.println("[MPI_GATHER] Master received computed Row R" + result.rowIndex 
                        + " from Rank " + result.workerRank + " -> " + Arrays.toString(result.computedRow));
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Execution failed: " + e.getMessage());
        } finally {
            workerPool.shutdown();
        }

        // Step 4: Display Final Matrix C
        System.out.println("\n==========================================================================");
        System.out.println("                 FINAL RESULTANT MATRIX C (A x B)                         ");
        System.out.println("==========================================================================");
        printMatrix(matrixC);
        System.out.println("==========================================================================");
    }

    private static void printMatrix(int[][] mat) {
        for (int[] row : mat) {
            System.out.print("  [ ");
            for (int val : row) {
                System.out.printf("%4d ", val);
            }
            System.out.println("]");
        }
    }
}