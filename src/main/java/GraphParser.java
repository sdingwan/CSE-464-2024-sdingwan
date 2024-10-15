import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphParser {

    private Graph<String, DefaultEdge> graph;

    public GraphParser() {
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    // Getter for the graph to use in tests
    public Graph<String, DefaultEdge> getGraph() {
        return this.graph;
    }

    // Feature 1: Parse a DOT graph file
    public void parseGraph(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            line = line.trim();  // Remove extra spaces around the line
            if (line.contains("->")) {
                // Split the line into source and target, handling any extra spaces
                String[] parts = line.split("->");
                String source = parts[0].trim();
                String target = parts[1].replace(";", "").trim();

                // Add nodes and the edge
                addNode(source);
                addNode(target);
                addEdge(source, target);
            }
        }
    }



    // Feature 2: Adding nodes
    public void addNode(String label) {
        if (!graph.containsVertex(label)) {
            graph.addVertex(label);
        }
    }

    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    // Feature 3: Adding edges
    public void addEdge(String srcLabel, String dstLabel) {
        if (!graph.containsEdge(srcLabel, dstLabel)) {
            graph.addEdge(srcLabel, dstLabel);
        }
    }

    // Feature 4: Output the graph to a DOT file
    public void outputDOTGraph(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        for (String vertex : graph.vertexSet()) {
            sb.append("\t").append(vertex).append(";\n");
        }
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            sb.append("\t").append(source).append(" -> ").append(target).append(";\n");
        }
        sb.append("}\n");

        Files.write(Paths.get(filePath), sb.toString().getBytes());
    }

    // Feature 4: Output the graph as a PNG file
    public void outputGraphics(String filePath, String format) throws IOException {
        MutableGraph g = mutGraph("example").setDirected(true);
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            g.add(mutNode(source).addLink(mutNode(target)));
        }

        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(filePath));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph: \n");
        sb.append("Nodes: ").append(graph.vertexSet().size()).append("\n");
        sb.append("Edges: ").append(graph.edgeSet().size()).append("\n");

        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            sb.append(source).append(" -> ").append(target).append("\n");
        }

        for (String vertex : graph.vertexSet()) {
            boolean isIsolated = graph.edgesOf(vertex).isEmpty();
            if (isIsolated) {
                sb.append(vertex).append("\n");
            }
        }

        return sb.toString();
    }

}
