import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class exp5 {

    // Representation of a workload job
    static class Job {
        String jobId;
        int computeUnits;
        String assignedWorker;
        String status;

        Job(String jobId, int computeUnits, String assignedWorker, String status) {
            this.jobId = jobId;
            this.computeUnits = computeUnits;
            this.assignedWorker = assignedWorker;
            this.status = status;
        }
    }

    // Worker node tracking active load
    static class WorkerNode {
        String workerId;
        int activeLoadUnits;
        int jobCount;

        WorkerNode(String workerId) {
            this.workerId = workerId;
            this.activeLoadUnits = 0;
            this.jobCount = 0;
        }

        void assignJob(int units) {
            this.activeLoadUnits += units;
            this.jobCount++;
        }

        void reset() {
            this.activeLoadUnits = 0;
            this.jobCount = 0;
        }
    }

    public static void main(String[] args) {
        String csvFile = "workload.csv";
        List<Job> jobList = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("            DYNAMIC LOAD BALANCING SIMULATOR (RR & LEAST-LOAD)            ");
        System.out.println("==========================================================================");

        // Step 1: Ingest batch of incoming jobs from CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                String jobId = tokens[0].trim();
                int units = Integer.parseInt(tokens[1].trim());
                String assignedWorker = tokens[2].trim();
                String status = tokens[3].trim();

                jobList.add(new Job(jobId, units, assignedWorker, status));
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        System.out.println("\n[INCOMING WORKLOAD QUEUE]");
        for (Job j : jobList) {
            System.out.printf("Job ID: %-5s | Required Compute Units: %-3d | Initial Status: %s\n",
                    j.jobId, j.computeUnits, j.status);
        }
        System.out.println("--------------------------------------------------------------------------");

        // Define worker cluster (e.g., 3 worker nodes)
        List<WorkerNode> workers = Arrays.asList(
                new WorkerNode("Worker_A"),
                new WorkerNode("Worker_B"),
                new WorkerNode("Worker_C")
        );

        // Run Strategy 1: Round-Robin Scheduling
        simulateRoundRobin(new ArrayList<>(jobList), workers);

        // Reset workers
        for (WorkerNode w : workers) w.reset();

        // Run Strategy 2: Dynamic Least-Connections / Least-Loaded Scheduling
        simulateLeastLoaded(jobList, workers);
    }

    // Strategy 1: Round-Robin (Cyclic assignment)
    private static void simulateRoundRobin(List<Job> jobs, List<WorkerNode> workers) {
        System.out.println("\n>>> STRATEGY 1: ROUND-ROBIN (STATIC ROTATION)");
        int rrIndex = 0;
        int numWorkers = workers.size();

        for (Job job : jobs) {
            WorkerNode selectedWorker = workers.get(rrIndex);
            selectedWorker.assignJob(job.computeUnits);
            job.assignedWorker = selectedWorker.workerId;
            job.status = "ALLOCATED";

            System.out.printf("  [ASSIGNED] %-5s (%2d units) -> %-8s | RR Worker Load: %2d units (%d jobs)\n",
                    job.jobId, job.computeUnits, selectedWorker.workerId, selectedWorker.activeLoadUnits, selectedWorker.jobCount);

            rrIndex = (rrIndex + 1) % numWorkers;
        }

        printClusterSummary(workers, "Round-Robin");
    }

    // Strategy 2: Dynamic Least-Loaded (Routes to node with lowest cumulative units)
    private static void simulateLeastLoaded(List<Job> jobs, List<WorkerNode> workers) {
        System.out.println("\n>>> STRATEGY 2: DYNAMIC LEAST-CONNECTIONS / LEAST-LOADED SCHEDULING");

        for (Job job : jobs) {
            // Find worker with minimum active compute units
            WorkerNode bestWorker = workers.get(0);
            for (WorkerNode w : workers) {
                if (w.activeLoadUnits < bestWorker.activeLoadUnits) {
                    bestWorker = w;
                }
            }

            bestWorker.assignJob(job.computeUnits);
            job.assignedWorker = bestWorker.workerId;
            job.status = "ALLOCATED";

            System.out.printf("  [BALANCED] %-5s (%2d units) -> %-8s | Updated Node Load: %2d units (%d jobs)\n",
                    job.jobId, job.computeUnits, bestWorker.workerId, bestWorker.activeLoadUnits, bestWorker.jobCount);
        }

        printClusterSummary(workers, "Dynamic Least-Loaded");
    }

    private static void printClusterSummary(List<WorkerNode> workers, String strategyName) {
        System.out.println("\n[SUMMARY: " + strategyName.toUpperCase() + "]");
        System.out.println("--------------------------------------------------------------------------");
        int totalUnits = 0;
        for (WorkerNode w : workers) {
            System.out.printf("Node ID: %-10s | Total Compute Units: %-3d | Total Jobs: %-2d\n",
                    w.workerId, w.activeLoadUnits, w.jobCount);
            totalUnits += w.activeLoadUnits;
        }
        System.out.println("Cluster Total Load: " + totalUnits + " units processed.");
        System.out.println("--------------------------------------------------------------------------");
    }
}