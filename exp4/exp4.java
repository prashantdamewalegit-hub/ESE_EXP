import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class exp4 {

    // Representation of a Task
    static class Task {
        String taskId;
        String threadName;
        int burstMs;
        String priority;
        String status;

        Task(String taskId, String threadName, int burstMs, String priority, String status) {
            this.taskId = taskId;
            this.threadName = threadName;
            this.burstMs = burstMs;
            this.priority = priority;
            this.status = status;
        }
    }

    // Shared resource simulator guarded by Mutex
    static class SharedResourceManager {
        private final ReentrantLock mutex = new ReentrantLock();
        private int sharedProcessedCounter = 0;

        public void processTaskSafely(Task task) {
            System.out.println("[THREAD RUNNING] " + Thread.currentThread().getName() 
                    + " attempting to acquire Lock for Task: " + task.taskId);

            // Step 1: Lock Acquisition (Mutex)
            mutex.lock();
            try {
                System.out.println("  -> [LOCK ACQUIRED] " + Thread.currentThread().getName() 
                        + " entered Critical Section for Task: " + task.taskId);

                // Critical section: modifying shared state and executing task
                sharedProcessedCounter++;
                System.out.println("  -> [PROCESSING] Executing " + task.taskId + " (Priority: " 
                        + task.priority + ", Burst: " + task.burstMs + "ms). Counter: " + sharedProcessedCounter);

                // Simulate burst execution time
                Thread.sleep(task.burstMs);

                task.status = "COMPLETED";
                System.out.println("  -> [COMPLETED] Task " + task.taskId + " execution finished.");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            } finally {
                // Step 2: Lock Release (Crucial inside finally to prevent deadlocks)
                System.out.println("  -> [LOCK RELEASED] " + Thread.currentThread().getName() 
                        + " exiting Critical Section.\n");
                mutex.unlock();
            }
        }
    }

    public static void main(String[] args) {
        String csvFile = "tasks.csv";
        List<Task> taskList = new ArrayList<>();
        SharedResourceManager resourceManager = new SharedResourceManager();

        System.out.println("==========================================================================");
        System.out.println("     MULTITHREADING & MUTEX LOCK SYNCHRONIZATION SIMULATION              ");
        System.out.println("==========================================================================");

        // Ingest task configuration from CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                String taskId = tokens[0].trim();
                String threadName = tokens[1].trim();
                int burstMs = Integer.parseInt(tokens[2].trim());
                String priority = tokens[3].trim();
                String status = tokens[4].trim();

                taskList.add(new Task(taskId, threadName, burstMs, priority, status));
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        System.out.println("\n[LOADED TASKS FROM CSV]");
        for (Task t : taskList) {
            System.out.printf("Task ID: %-4s | Assigned: %-10s | Burst: %-4dms | Priority: %-6s | Status: %s\n",
                    t.taskId, t.threadName, t.burstMs, t.priority, t.status);
        }
        System.out.println("--------------------------------------------------------------------------\n");

        // Create and spawn concurrent worker threads
        List<Thread> threads = new ArrayList<>();
        for (Task task : taskList) {
            Thread worker = new Thread(() -> {
                resourceManager.processTaskSafely(task);
            }, task.threadName + "-" + task.taskId);

            // Optional priority mapping
            if ("HIGH".equalsIgnoreCase(task.priority)) {
                worker.setPriority(Thread.MAX_PRIORITY);
            } else if ("LOW".equalsIgnoreCase(task.priority)) {
                worker.setPriority(Thread.MIN_PRIORITY);
            } else {
                worker.setPriority(Thread.NORM_PRIORITY);
            }

            threads.add(worker);
        }

        // Start all threads concurrently
        for (Thread t : threads) {
            t.start();
        }

        // Await thread completion (join)
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Step 3: Print final completed summary
        System.out.println("==========================================================================");
        System.out.println("                      FINAL COMPLETED TASK LIST                           ");
        System.out.println("==========================================================================");
        for (Task t : taskList) {
            System.out.printf("Task ID: %-4s | Assigned: %-10s | Final Status: %s\n",
                    t.taskId, t.threadName, t.status);
        }
        System.out.println("==========================================================================");
    }
}