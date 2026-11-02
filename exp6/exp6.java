import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class exp6 {

    static class EventRecord {
        String eventId;
        int[] timestamp;

        EventRecord(String eventId, int[] timestamp) {
            this.eventId = eventId;
            this.timestamp = timestamp.clone();
        }

        String getVectorString() {
            return Arrays.toString(timestamp);
        }
    }

    // Converts "P1" -> 0, "P2" -> 1, "P3" -> 2
    static int getIndex(String p) {
        switch (p) {
            case "P1": return 0;
            case "P2": return 1;
            case "P3": return 2;
            default:   return -1;
        }
    }

    public static void main(String[] args) {
        String csvFile = "vector_events.csv";

        int[][] currentClocks = new int[3][3];
        int[][] messageChannel = new int[3][3]; // messageChannel[targetIndex] stores incoming vector

        List<EventRecord> recordedEvents = new ArrayList<>();

        System.out.println("==========================================================================");
        System.out.println("             VECTOR CLOCKS & CAUSALITY DETECTION SIMULATOR                ");
        System.out.println("==========================================================================");
        System.out.println("Process Vector Format: [P1, P2, P3]\n");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",", -1);
                String eventId = tokens[0].trim();
                String procId = tokens[1].trim();
                String action = tokens[2].trim().toUpperCase();
                String targetProc = tokens[3].trim();

                int pIdx = getIndex(procId);

                // Rule 1: Local tick
                currentClocks[pIdx][pIdx]++;

                // Rules 2 & 3: Handle SEND / RECV / INTERNAL
                if ("SEND".equals(action)) {
                    int targetIdx = getIndex(targetProc);
                    if (targetIdx != -1) {
                        messageChannel[targetIdx] = currentClocks[pIdx].clone();
                    }
                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> Action: SEND to "
                            + targetProc + " | Clock: " + Arrays.toString(currentClocks[pIdx]));

                } else if ("RECV".equals(action)) {
                    int[] incomingVector = messageChannel[pIdx];
                    for (int k = 0; k < 3; k++) {
                        currentClocks[pIdx][k] = Math.max(currentClocks[pIdx][k], incomingVector[k]);
                    }
                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> Action: RECV from "
                            + targetProc + " | Clock: " + Arrays.toString(currentClocks[pIdx]));

                } else {
                    System.out.println("[EVENT " + eventId + "] Process " + procId + " -> Action: INTERNAL"
                            + " | Clock: " + Arrays.toString(currentClocks[pIdx]));
                }

                recordedEvents.add(new EventRecord(eventId, currentClocks[pIdx]));
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
            return;
        }

        // Causality and Concurrency Analysis
        System.out.println("\n==========================================================================");
        System.out.println("             PAIRWISE CAUSALITY & CONCURRENCY ANALYSIS                    ");
        System.out.println("==========================================================================");

        for (int i = 0; i < recordedEvents.size(); i++) {
            for (int j = i + 1; j < recordedEvents.size(); j++) {
                EventRecord e1 = recordedEvents.get(i);
                EventRecord e2 = recordedEvents.get(j);

                boolean e1LeE2 = isLessOrEqual(e1.timestamp, e2.timestamp);
                boolean e2LeE1 = isLessOrEqual(e2.timestamp, e1.timestamp);

                if (e1LeE2 && !Arrays.equals(e1.timestamp, e2.timestamp)) {
                    System.out.printf("%-4s %-11s  CAUSALLY PRECEDES (->)  %-4s %-11s\n",
                            e1.eventId, e1.getVectorString(), e2.eventId, e2.getVectorString());
                } else if (e2LeE1 && !Arrays.equals(e1.timestamp, e2.timestamp)) {
                    System.out.printf("%-4s %-11s  CAUSALLY PRECEDES (->)  %-4s %-11s\n",
                            e2.eventId, e2.getVectorString(), e1.eventId, e1.getVectorString());
                } else {
                    System.out.printf("%-4s %-11s  IS CONCURRENT (||) WITH  %-4s %-11s\n",
                            e1.eventId, e1.getVectorString(), e2.eventId, e2.getVectorString());
                }
            }
        }
        System.out.println("==========================================================================");
    }

    private static boolean isLessOrEqual(int[] v1, int[] v2) {
        for (int k = 0; k < v1.length; k++) {
            if (v1[k] > v2[k]) {
                return false;
            }
        }
        return true;
    }
}