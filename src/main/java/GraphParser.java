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
    private Graph<Node, DefaultEdge> graph;

    public enum Algorithm {
        BFS, DFS
    }

    public GraphParser() {
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    public Graph<Node, DefaultEdge> getGraph() {
        return this.graph;
    }

    public void parseGraph(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            line = line.trim();
            if (line.contains("->")) {
                String[] parts = line.split("->");
                Node source = new Node(parts[0].trim());
                Node target = new Node(parts[1].replace(";", "").trim());
                addNode(source);
                addNode(target);
                addEdge(source, target);
            }
        }
    }

    // Refactor 1: Encapsulate Node Addition Logic
    // Why: Avoid repetitive null checks and simplify node addition logic.
    private void ensureNodeExists(Node node) {
        if (!graph.containsVertex(node)) {
            graph.addVertex(node);
        }
    }

    public void addNode(Node node) {
        ensureNodeExists(node);
    }

    public void addNodes(Node[] nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }

    // Refactor 2: Encapsulate Edge Addition Logic
    // Why: Simplify edge addition and handle checks for vertex existence in one place.
    private void ensureEdgeExists(Node source, Node target) {
        if (!graph.containsEdge(source, target)) {
            if (graph.containsVertex(source) && graph.containsVertex(target)) {
                graph.addEdge(source, target);
            }
        }
    }

    public void addEdge(Node source, Node target) {
        ensureEdgeExists(source, target);
    }

    public void outputDOTGraph(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        for (Node vertex : graph.vertexSet()) {
            sb.append("\t").append(vertex).append(";\n");
        }
        for (DefaultEdge edge : graph.edgeSet()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);
            sb.append("\t").append(source).append(" -> ").append(target).append(";\n");
        }
        sb.append("}\n");
        Files.write(Paths.get(filePath), sb.toString().getBytes());
    }

    public void outputGraphics(String filePath, String format) throws IOException {
        MutableGraph g = mutGraph("example").setDirected(true);
        for (DefaultEdge edge : graph.edgeSet()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);
            g.add(mutNode(source.toString()).addLink(mutNode(target.toString())));
        }
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(filePath));
    }

    // Refactor 3: Improve Variable Names for Clarity
    // Why: Enhance code readability by using descriptive variable names.
    public Path graphSearch(Node sourceNode, Node destinationNode, Algorithm algo) {
        return algo == Algorithm.BFS ? graphSearchBFS(sourceNode, destinationNode) : graphSearchDFS(sourceNode, destinationNode);
    }

    private Path graphSearchBFS(Node sourceNode, Node destinationNode) {
        if (!graph.containsVertex(sourceNode) || !graph.containsVertex(destinationNode)) {
            throw new IllegalArgumentException("One or both of the nodes do not exist in the graph.");
        }

        Map<Node, Node> predecessors = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(sourceNode);
        predecessors.put(sourceNode, null);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(destinationNode)) {
                return buildPath(sourceNode, destinationNode, predecessors);
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!predecessors.containsKey(neighbor)) {
                    queue.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            }
        }

        return null;
    }

    private Path graphSearchDFS(Node sourceNode, Node destinationNode) {
        if (!graph.containsVertex(sourceNode) || !graph.containsVertex(destinationNode)) {
            throw new IllegalArgumentException("One or both of the nodes do not exist in the graph.");
        }

        Stack<Node> stack = new Stack<>();
        Map<Node, Node> predecessors = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        stack.push(sourceNode);
        predecessors.put(sourceNode, null);
        visited.add(sourceNode);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (current.equals(destinationNode)) {
                return buildPath(sourceNode, destinationNode, predecessors);
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            }
        }

        return null;
    }

    // Refactor 4: Extract Method (Build Path)
    // Why: Reduce duplication in BFS and DFS by creating a reusable method.
    private Path buildPath(Node sourceNode, Node destinationNode, Map<Node, Node> predecessors) {
        Path path = new Path();
        Node step = destinationNode;

        while (step != null) {
            path.addNode(step);
            step = predecessors.get(step);
        }

        Collections.reverse(path.getNodes());
        return path;
    }

    public void removeNode(Node node) {
        if (!graph.containsVertex(node)) {
            throw new IllegalArgumentException("Node " + node + " does not exist in the graph.");
        }
        graph.removeVertex(node);
    }

    public void removeNodes(Node[] nodes) {
        for (Node node : nodes) {
            removeNode(node);
        }
    }

    public void removeEdge(Node source, Node target) {
        DefaultEdge edge = graph.getEdge(source, target);
        if (edge == null) {
            throw new IllegalArgumentException("Edge from " + source + " to " + target + " does not exist in the graph.");
        }
        graph.removeEdge(edge);
    }

    // Refactor 5: Simplify toString Method
    // Why: Improve maintainability by using Java streams for iteration.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph: \n");
        sb.append("Nodes: ").append(graph.vertexSet().size()).append("\n");
        sb.append("Edges: ").append(graph.edgeSet().size()).append("\n");

        graph.edgeSet().forEach(edge -> {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);
            sb.append(source).append(" -> ").append(target).append("\n");
        });

        graph.vertexSet().stream()
                .filter(vertex -> graph.edgesOf(vertex).isEmpty())
                .forEach(vertex -> sb.append(vertex).append("\n"));

        return sb.toString();
    }
}
