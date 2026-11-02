import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class exp3 {

    static class NodeClock {
        String nodeId;
        long clockTimeSec;
        boolean isFaulty;
        long offset;

        NodeClock(String nodeId, long clockTimeSec, boolean isFaulty) {
            this.nodeId = nodeId;
            this.clockTimeSec = clockTimeSec;
            this.isFaulty = isFaulty;
            this.offset = 0;
        }
    }

    public static void main(String[] args) {
        String csvFile = "node_clocks.csv";
        List<NodeClock> nodes = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("        BERKELEY PHYSICAL CLOCK SYNCHRONIZATION ALGORITHM                ");
        System.out.println("==========================================================================");

        // Step 1: Read and poll node clock times from CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                String nodeId = tokens[0].trim();
                long clockTime = Long.parseLong(tokens[1].trim());
                boolean isFaulty = Boolean.parseBoolean(tokens[2].trim());

                nodes.add(new NodeClock(nodeId, clockTime, isFaulty));
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        System.out.println("\n[STEP 1: TIME DAEMON POLLING]");
        for (NodeClock node : nodes) {
            System.out.println("Polled " + node.nodeId + " -> Local Clock Time: " + node.clockTimeSec + "s");
        }

        // Step 2: Identify and discard faulty outliers
        System.out.println("\n[STEP 2: OUTLIER FILTERING]");
        List<NodeClock> validNodes = new ArrayList<>();
        long thresholdSec = 100; // Define acceptable drift window relative to Master (first node)
        long baseTime = nodes.get(0).clockTimeSec;

        for (NodeClock node : nodes) {
            long diff = Math.abs(node.clockTimeSec - baseTime);
            if (node.isFaulty || diff > thresholdSec) {
                System.out.println("[OUTLIER DISCARDED] " + node.nodeId + " (Time: " + node.clockTimeSec 
                        + "s) drifted beyond threshold or marked faulty.");
            } else {
                validNodes.add(node);
                System.out.println("[VALID NODE] " + node.nodeId + " within acceptable sync window.");
            }
        }

        if (validNodes.isEmpty()) {
            System.out.println("No valid nodes available to compute average.");
            return;
        }

        // Step 3: Compute fault-tolerant average time
        long sum = 0;
        for (NodeClock node : validNodes) {
            sum += node.clockTimeSec;
        }
        long averageTime = Math.round((double) sum / validNodes.size());

        System.out.println("\n[STEP 3: AVERAGE CALCULATION]");
        System.out.println("Valid Nodes Count : " + validNodes.size());
        System.out.println("Synchronized Average Clock Time: " + averageTime + "s");

        // Step 4: Calculate relative adjustments (offsets)
        System.out.println("\n[STEP 4: RELATIVE ADJUSTMENT PER NODE]");
        System.out.println("Formula: Offset = Synchronized Average Time - Local Node Time");
        System.out.println("--------------------------------------------------------------------------");
        for (NodeClock node : nodes) {
            if (node.isFaulty || Math.abs(node.clockTimeSec - baseTime) > thresholdSec) {
                System.out.printf("%-10s | Status: FAULTY   | No adjustment sent (Node ignored)\n", node.nodeId);
            } else {
                node.offset = averageTime - node.clockTimeSec;
                long adjustedTime = node.clockTimeSec + node.offset;
                System.out.printf("%-10s | Original: %ds | Offset: %+3ds | New Synced Time: %ds\n",
                        node.nodeId, node.clockTimeSec, node.offset, adjustedTime);
            }
        }

        System.out.println("==========================================================================");
        System.out.println("             CLOCK SYNCHRONIZATION COMPLETED                              ");
        System.out.println("==========================================================================");
    }
}