import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class exp2 {

    static class Node {
        int id;
        boolean isAlive;
        boolean isCoordinator;

        Node(int id, boolean isAlive, boolean isCoordinator) {
            this.id = id;
            this.isAlive = isAlive;
            this.isCoordinator = isCoordinator;
        }
    }

    public static void main(String[] args) {
        String csvFile = "bully_cluster.csv";
        List<Node> nodes = new ArrayList<>();
        Node currentCoordinator = null;

        System.out.println("==========================================================================");
        System.out.println("             BULLY LEADER ELECTION ALGORITHM SIMULATION                   ");
        System.out.println("==========================================================================");

        // Step 1: Read CSV and populate the list
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",", -1);
                int id = Integer.parseInt(tokens[0].trim());
                boolean isAlive = Boolean.parseBoolean(tokens[1].trim());
                boolean isCoord = Boolean.parseBoolean(tokens[2].trim());

                Node node = new Node(id, isAlive, isCoord);
                nodes.add(node);

                if (isCoord) {
                    currentCoordinator = node;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        // Display initial cluster state
        System.out.println("\n[CLUSTER STATUS]");
        for (Node node : nodes) {
            System.out.println("Node " + node.id + " -> Alive: " + node.isAlive + ", Coordinator: " + node.isCoordinator);
        }

        // Step 2: Check if coordinator crashed
        if (currentCoordinator != null && !currentCoordinator.isAlive) {
            System.out.println("\n[FAILURE DETECTED] Coordinator Node " + currentCoordinator.id + " has CRASHED!");
        } else {
            System.out.println("\n[STATUS] Coordinator is functioning normally.");
            return;
        }

        // Step 3: Find first active node to initiate election
        Node initiator = null;
        for (Node node : nodes) {
            if (node.isAlive && !node.isCoordinator) {
                initiator = node;
                break;
            }
        }

        if (initiator == null) {
            System.out.println("No active nodes available to initiate election.");
            return;
        }

        System.out.println("\n[ELECTION INITIATED] Node " + initiator.id + " notices failure and starts election.");
        runElection(initiator.id, nodes);
    }

    private static void runElection(int senderId, List<Node> nodes) {
        Node highestAliveNode = null;

        for (Node node : nodes) {
            if (node.id > senderId) {
                System.out.println("[ELECTION] Node " + senderId + " sends ELECTION message to Node " + node.id);
                if (node.isAlive) {
                    System.out.println("[OK RESPONSE] Node " + node.id + " responds OK to Node " + senderId);
                    highestAliveNode = node; // Tracks the highest alive node as we loop
                } else {
                    System.out.println("[TIMEOUT] Node " + node.id + " is unresponsive.");
                }
            }
        }

        // If higher active nodes responded, the highest takes over the election
        if (highestAliveNode != null) {
            System.out.println("\n[TAKEOVER] Higher active Node " + highestAliveNode.id + " takes over election.");
            runElection(highestAliveNode.id, nodes);
        } else {
            // No higher node is alive; sender declares itself coordinator
            declareCoordinator(senderId, nodes);
        }
    }

    private static void declareCoordinator(int newLeaderId, List<Node> nodes) {
        System.out.println("\n==========================================================================");
        System.out.println("[COORDINATOR BROADCAST] Node " + newLeaderId + " elects itself as the new COORDINATOR!");
        System.out.println("==========================================================================");

        for (Node node : nodes) {
            if (node.id == newLeaderId) {
                node.isCoordinator = true;
            } else {
                node.isCoordinator = false;
                if (node.isAlive) {
                    System.out.println("[BROADCAST] Node " + newLeaderId + " sends COORDINATOR announcement to Node " + node.id);
                }
            }
        }

        System.out.println("\n[FINAL CLUSTER STATE]");
        for (Node node : nodes) {
            System.out.println("Node " + node.id + " -> Alive: " + node.isAlive + ", Coordinator: " + node.isCoordinator);
        }
    }
}