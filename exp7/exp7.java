import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class  exp7{

    static class Event {
        String eventId;
        String processId;
        String eventType;
        String messageTo;
        int logicalTimestamp;

        Event(String eventId, String processId, String eventType, String messageTo) {
            this.eventId = eventId;
            this.processId = processId;
            this.eventType = eventType;
            this.messageTo = messageTo;
            this.logicalTimestamp = 0;
        }
    }

    // Maps "P1" -> 0, "P2" -> 1, "P3" -> 2
    static int getIndex(String p) {
        switch (p) {
            case "P1": return 0;
            case "P2": return 1;
            case "P3": return 2;
            default:   return -1;
        }
    }

    public static void main(String[] args) {
        String csvFile = "events.csv";

        // Process clocks: index 0 = P1, 1 = P2, 2 = P3
        int[] clocks = new int[3];

        // Store transmitted message timestamps indexed by target process
        int[] messageBuffer = new int[3];

        List<Event> eventList = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("          LAMPORT'S LOGICAL CLOCKS & TOTAL ORDERING SIMULATOR             ");
        System.out.println("==========================================================================");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",", -1);
                String eventId = tokens[0].trim();
                String procId = tokens[1].trim();
                String type = tokens[2].trim().toUpperCase();
                String targetProc = tokens[3].trim();

                Event event = new Event(eventId, procId, type, targetProc);
                int pIdx = getIndex(procId);

                if ("SEND".equals(type)) {
                    // Local event rule: L = L + 1
                    clocks[pIdx]++;
                    event.logicalTimestamp = clocks[pIdx];

                    // Piggyback timestamp for the target receiver
                    int targetIdx = getIndex(targetProc);
                    if (targetIdx != -1) {
                        messageBuffer[targetIdx] = clocks[pIdx];
                    }

                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> SEND to "
                            + targetProc + " | Clock updated: L = " + clocks[pIdx]);

                } else if ("RECV".equals(type)) {
                    // Receive rule: L = max(L, T_msg) + 1
                    int msgTimestamp = messageBuffer[pIdx];
                    int updatedL = Math.max(clocks[pIdx], msgTimestamp) + 1;
                    clocks[pIdx] = updatedL;
                    event.logicalTimestamp = updatedL;

                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> RECV from "
                            + targetProc + " [T_msg = " + msgTimestamp + "] | Clock updated: max("
                            + (updatedL - 1) + ", " + msgTimestamp + ") + 1 = " + updatedL);

                } else { // INTERNAL
                    clocks[pIdx]++;
                    event.logicalTimestamp = clocks[pIdx];

                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> INTERNAL"
                            + " | Clock updated: L = " + clocks[pIdx]);
                }

                eventList.add(event);
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        // Total Causal Order: sort by timestamp ascending, tie-break by process ID
        eventList.sort((e1, e2) -> {
            if (e1.logicalTimestamp != e2.logicalTimestamp) {
                return Integer.compare(e1.logicalTimestamp, e2.logicalTimestamp);
            }
            return e1.processId.compareTo(e2.processId);
        });

        // Print final total order table
        System.out.println("\n==========================================================================");
        System.out.println("              FINAL TOTAL CAUSAL ORDER OF ALL EVENTS                      ");
        System.out.println("==========================================================================");
        System.out.println("Order Rule: (L(E_A) < L(E_B)) OR (L(E_A) == L(E_B) AND Proc(E_A) < Proc(E_B))");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-6s | %-10s | %-10s | %-16s | %-12s\n",
                "Rank", "Event ID", "Process", "Lamport Timestamp", "Action");
        System.out.println("--------------------------------------------------------------------------");

        int rank = 1;
        for (Event e : eventList) {
            System.out.printf("%-6d | %-10s | %-10s | L = %-12d | %s\n",
                    rank++, e.eventId, e.processId, e.logicalTimestamp, e.eventType);
        }
        System.out.println("==========================================================================");
    }
}