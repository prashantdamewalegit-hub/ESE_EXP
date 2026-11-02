import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class exp8 {

    // Representation of a synchronization event record
    static class SyncRecord {
        String clientId;
        long t0SendMs;     // Client timestamp when sending request
        long tServerMs;    // Time server timestamp when responding
        long t1RecvMs;     // Client timestamp when response is received

        SyncRecord(String clientId, long t0SendMs, long tServerMs, long t1RecvMs) {
            this.clientId = clientId;
            this.t0SendMs = t0SendMs;
            this.tServerMs = tServerMs;
            this.t1RecvMs = t1RecvMs;
        }
    }

    public static void main(String[] args) {
        String csvFile = "cristian_clocks.csv";

        System.out.println("==========================================================================");
        System.out.println("        CRISTIAN'S CLOCK SYNCHRONIZATION ALGORITHM SIMULATOR              ");
        System.out.println("==========================================================================");
        System.out.println("Formulae:");
        System.out.println("  1. Round-Trip Time (RTT)          = T1 - T0");
        System.out.println("  2. Estimated Current Server Time  = T_server + (RTT / 2)");
        System.out.println("  3. Local Clock Offset Adjustment  = Estimated Server Time - T1");
        System.out.println("--------------------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // Skip CSV header line

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");

                String clientId = tokens[0].trim();
                long t0 = Long.parseLong(tokens[1].trim());
                long tServer = Long.parseLong(tokens[2].trim());
                long t1 = Long.parseLong(tokens[3].trim());

                SyncRecord record = new SyncRecord(clientId, t0, tServer, t1);

                // Step 1: Calculate Round-Trip Time (RTT)
                long rtt = record.t1RecvMs - record.t0SendMs;

                // Step 2: Estimate current server time (one-way transit = RTT / 2)
                long estimatedServerTime = record.tServerMs + (rtt / 2);

                // Step 3: Compute clock adjustment offset
                long offset = estimatedServerTime - record.t1RecvMs;

                // Step 4: Step-by-step terminal output
                System.out.println("\n[CLIENT SESSION: " + record.clientId + "]");
                System.out.println("  -> T0 (Request Dispatched) : " + record.t0SendMs + " ms");
                System.out.println("  -> T_server (Server Stamp) : " + record.tServerMs + " ms");
                System.out.println("  -> T1 (Response Received)  : " + record.t1RecvMs + " ms");
                System.out.println("  -> Calculated RTT (T1 - T0): " + rtt + " ms");
                System.out.println("  -> One-Way Transit (RTT / 2): " + (rtt / 2) + " ms");
                System.out.println("  -> Estimated Server Time   : " + record.tServerMs + " + " + (rtt / 2) 
                        + " = " + estimatedServerTime + " ms");
                System.out.printf("  -> [ADJUSTMENT] Local Clock Offset: %+d ms (New Synced Clock: %d ms)\n", 
                        offset, (record.t1RecvMs + offset));
            }

        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
        }

        System.out.println("\n==========================================================================");
        System.out.println("               CLOCK SYNCHRONIZATION COMPLETED                            ");
        System.out.println("==========================================================================");
    }
}