import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GraphParser {

    private Graph<String, DefaultEdge> graph;

    public GraphParser() {
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    // Feature 1: Parse a DOT graph file
    public void parseGraph(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.contains("->")) {
                String[] parts = line.split("->");
                String source = parts[0].trim();
                String target = parts[1].replace(";", "").trim();
                addNode(source);
                addNode(target);
                addEdge(source, target);
            }
        }
    }

    // Add the main method for testing
    public static void main(String[] args) throws IOException {
        GraphParser parser = new GraphParser();

        // Parsing a sample DOT file
        parser.parseGraph("input.dot");

        // Print the graph details
        System.out.println(parser.toString());
    }
}
