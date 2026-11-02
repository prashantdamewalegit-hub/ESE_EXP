import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class fun2 {

    static class Node {
        int id;
        long value;
        String name;

        Node(int id, long value, String name) {
            this.id = id;
            this.value = value;
            this.name = name;
        }

        static void pr() {
            System.out.println("hello");
        }
    }

    public static void main(String ages[]) {

        String file = "fun2.csv";
        List<Node> nodes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] tokens = line.split(",");
                int id = Integer.parseInt(tokens[0].trim());
                int value = Integer.parseInt(tokens[1].trim());
                String name = tokens[1].trim();

                Node n = new Node(id, value, name);
                nodes.add(n);
            }
        } catch (IOException e) {
            System.out.println("Enable to read");
        }

        nodes.get(0).pr();
        System.out.println(nodes.get(0).name);

    }
}
