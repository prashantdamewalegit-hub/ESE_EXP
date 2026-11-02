import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class exp1 {

    // Simulated remote service implementation
    static class RemoteService {
        public static double computeSquare(double x) {
            return x * x;
        }

        public static long factorial(int n) {
            long result = 1;
            for (int i = 2; i <= n; i++) {
                result *= i;
            }
            return result;
        }

        public static String toBinary(int n) {
            return Integer.toBinaryString(n);
        }

        public static double cube(double x) {
            return x * x * x;
        }
    }

    // Client Stub / Dispatcher Simulator
    public static void main(String[] args) {
        String csvFile = "rpc_calls.csv";
        String line = "";

        System.out.println("==========================================================================");
        System.out.println("          RPC / RMI CLIENT-SERVER DISPATCHER SIMULATION                   ");
        System.out.println("==========================================================================");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read header line
            String header = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                // Parsing CSV row: call_id, client_name, method_name, param_value, timestamp
                String[] tokens = line.split(",");
                String callId = tokens[0].trim();
                String clientName = tokens[1].trim();
                String methodName = tokens[2].trim();
                String paramStr = tokens[3].trim();
                String timestamp = tokens[4].trim();

                // Step 1: Marshalling & Client Stub Invocation
                System.out.println(
                        "\n[MARSHAL] Packaging call [ID: " + callId + "] from " + clientName + " at " + timestamp);
                System.out.println("[DISPATCH] Client invoking method -> " + methodName + "(" + paramStr + ")");

                // Step 2: Server Skeleton Unmarshalling & Procedure Dispatch
                System.out.print("[SERVER EXECUTION] Processing parameter " + paramStr + " -> ");
                String result = "";

                try {
                    double paramVal = Double.parseDouble(paramStr);

                    switch (methodName) {
                        case "computeSquare":
                            result = String.valueOf(RemoteService.computeSquare(paramVal));
                            break;
                        case "factorial":
                            result = String.valueOf(RemoteService.factorial((int) paramVal));
                            break;
                        case "toBinary":
                            result = RemoteService.toBinary((int) paramVal);
                            break;
                        case "cube":
                            result = String.valueOf(RemoteService.cube(paramVal));
                            break;
                        default:
                            result = "ERROR: Procedure not found";
                            break;
                    }
                } catch (Exception e) {
                    result = "ERROR: Invalid parameter format";
                }

                // Step 3: Return Unmarshalled Result to Client
                System.out.println("Executed successfully.");
                System.out.println("[RETURN] Result printed to terminal: " + result);
            }

        } catch (IOException e) {
            System.err.println("Failed to read CSV dataset: " + e.getMessage());
        }

        System.out.println("\n==========================================================================");
        System.out.println("               DISPATCHER EXECUTION COMPLETED                             ");
        System.out.println("==========================================================================");
    }
}