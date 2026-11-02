import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class fun {

    static class Node {
        int id;
        double value;
        boolean statuse;
        String name;
        String timestamp;

        Node(int id, double value, boolean statuse, String name, String timestamp) {
            this.id = id;
            this.value = value;
            this.statuse = statuse;
            this.name = name;
            this.timestamp = timestamp;
        }

    }

    public static void nodep(List<Node> node) {
        for (Node n : node) {
            System.out.println(n.id);
            System.out.println(n.value);
            System.out.println(n.statuse);
            System.out.println(n.name);
            System.out.println(n.timestamp);

            System.out.println();
        }
    }

    public static void main(String ages[]) {
        String csvfile = "fun.csv";
        List<Node> nodes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvfile))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] tokens = line.split(",");

                int ID = Integer.parseInt(tokens[0].trim());
                double VAL = Double.parseDouble(tokens[1].trim());
                boolean STA = Boolean.parseBoolean(tokens[2].trim());
                String NAME = tokens[3].trim();
                String TIME = tokens[4].trim();

                Node n = new Node(ID, VAL, STA, NAME, TIME);
                nodes.add(n);
            }

        } catch (IOException e) {
            System.out.println("Failed to read CSV file");
        }

        nodep(nodes);

    }
}
