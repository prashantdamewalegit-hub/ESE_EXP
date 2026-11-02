import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class exp9 {

    static class Node {
        int id;
        int nextId;
        boolean isActive;
        int priorityRank;

        Node(int id, int nextId, boolean isActive, int priorityRank) {
            this.id = id;
            this.nextId = nextId;
            this.isActive = isActive;
            this.priorityRank = priorityRank;
        }
    }

    // Helper: Finds a node in the list by its ID
    private static Node findNode(int id, List<Node> nodes) {
        for (Node n : nodes) {
            if (n.id == id) {
                return n;
            }
        }
        return null;
    }

    // Helper: Traverses next pointers, skipping inactive nodes
    private static int getNextActiveNode(int startId, List<Node> nodes) {
        Node current = findNode(startId, nodes);
        int nextId = current.nextId;
        Node nextNode = findNode(nextId, nodes);

        while (nextNode != null && !nextNode.isActive) {
            System.out.println("     [BYPASS] Node " + nextId + " is inactive. Skipping to next in ring...");
            nextId = nextNode.nextId;
            nextNode = findNode(nextId, nodes);
        }
        return nextId;
    }

    public static void main(String[] args) {
        String csvFile = "ring_nodes.csv";
        List<Node> ring = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("              RING LEADER ELECTION ALGORITHM SIMULATION                   ");
        System.out.println("==========================================================================");

        // Step 1: Ingest ring topology from CSV into a List
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",", -1);
                int id = Integer.parseInt(tokens[0].trim());
                int nextId = Integer.parseInt(tokens[1].trim());
                boolean isActive = Boolean.parseBoolean(tokens[2].trim());
                int priority = Integer.parseInt(tokens[3].trim());

                ring.add(new Node(id, nextId, isActive, priority));
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        // Display initial ring topology
        System.out.println("\n[RING TOPOLOGY]");
        for (Node n : ring) {
            System.out.printf("Node %d -> Successor: Node %d | Active: %-5s | Priority: %d\n",
                    n.id, n.nextId, n.isActive, n.priorityRank);
        }

        // Find the node with the highest ID (assumed coordinator)
        Node highestNode = ring.get(0);
        for (Node n : ring) {
            if (n.id > highestNode.id) {
                highestNode = n;
            }
        }

        // Check if the highest node is down
        if (!highestNode.isActive) {
            System.out.println("\n[FAILURE DETECTED] Coordinator Node " + highestNode.id + " is INACTIVE/CRASHED!");
        } else {
            System.out.println("\nCoordinator Node " + highestNode.id + " is active. No election required.");
            return;
        }

        // Pick the first active node as the initiator
        int initiatorId = -1;
        for (Node n : ring) {
            if (n.isActive) {
                initiatorId = n.id;
                break;
            }
        }

        if (initiatorId == -1) {
            System.out.println("No active nodes available in the ring.");
            return;
        }

        System.out.println("\n[ELECTION INITIATED] Active Node " + initiatorId + " starts the ring election.");

        // Step 2: Phase 1 - Circulating Election Token
        System.out.println("\n--- PHASE 1: CIRCULATING ELECTION TOKEN ---");
        List<Integer> candidateList = new ArrayList<>();
        candidateList.add(initiatorId);

        int currentId = initiatorId;
        while (true) {
            int nextActiveId = getNextActiveNode(currentId, ring);

            if (nextActiveId == initiatorId) {
                System.out.println("  -> [TOKEN RETURNED] Message returned to initiator Node " + initiatorId
                        + " with accumulated active IDs: " + candidateList);
                break;
            }

            System.out.println("  -> [TOKEN FORWARD] Node " + currentId + " forwards token to active Node "
                    + nextActiveId);
            candidateList.add(nextActiveId);
            currentId = nextActiveId;
        }

        // Step 3: Highest ID selection
        int newCoordinatorId = Collections.max(candidateList);
        System.out.println("\n==========================================================================");
        System.out.println("Initiator Node " + initiatorId + " evaluates candidates: " + candidateList);
        System.out.println("[NEW LEADER ELECTED] Highest Active Identifier is Node " + newCoordinatorId + "!");
        System.out.println("==========================================================================");

        // Step 4: Phase 2 - Circulating Coordinator Announcement
        System.out.println("\n--- PHASE 2: CIRCULATING COORDINATOR ANNOUNCEMENT ---");
        currentId = initiatorId;
        while (true) {
            int nextActiveId = getNextActiveNode(currentId, ring);
            if (nextActiveId == initiatorId) {
                System.out.println("  -> [ANNOUNCEMENT COMPLETE] Node " + initiatorId
                        + " verified all active nodes acknowledged Coordinator Node " + newCoordinatorId);
                break;
            }
            System.out.println("  -> [ANNOUNCEMENT] Node " + currentId + " notifies Node "
                    + nextActiveId + " that Node " + newCoordinatorId + " is COORDINATOR");
            currentId = nextActiveId;
        }

        System.out.println("\n[FINAL LEADER ELECTION STATE]");
        System.out.println("Active Cluster Coordinator: Node " + newCoordinatorId);
        System.out.println("==========================================================================");
    }
}