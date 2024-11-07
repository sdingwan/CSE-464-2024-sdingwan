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

    public void addNode(Node node) {
        if (!graph.containsVertex(node)) {
            graph.addVertex(node);
        }
    }

    public void addNodes(Node[] nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }

    public void addEdge(Node src, Node dst) {
        if (!graph.containsEdge(src, dst)) {
            if (graph.containsVertex(src) && graph.containsVertex(dst)) {
                graph.addEdge(src, dst);
            }
        }
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

    // New API to remove a single node and its associated edges
    public void removeNode(Node node) {
        if (!graph.containsVertex(node)) {
            throw new IllegalArgumentException("Node " + node + " does not exist in the graph.");
        }
        graph.removeVertex(node);
    }

    // New API to remove multiple nodes
    public void removeNodes(Node[] nodes) {
        for (Node node : nodes) {
            removeNode(node);
        }
    }

    // New API to remove an edge between two specified nodes
    public void removeEdge(Node src, Node dst) {
        DefaultEdge edge = graph.getEdge(src, dst);
        if (edge == null) {
            throw new IllegalArgumentException("Edge from " + src + " to " + dst + " does not exist in the graph.");
        }
        graph.removeEdge(edge);
    }

    // BFS-based GraphSearch API
    public Path GraphSearchBFS(Node src, Node dst) {
        if (!graph.containsVertex(src) || !graph.containsVertex(dst)) {
            throw new IllegalArgumentException("One or both of the nodes do not exist in the graph.");
        }

        Map<Node, Node> predecessors = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(src);
        predecessors.put(src, null);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(dst)) {
                return buildPath(src, dst, predecessors);
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

    // DFS-based GraphSearch API
    public Path GraphSearchDFS(Node src, Node dst) {
        if (!graph.containsVertex(src) || !graph.containsVertex(dst)) {
            throw new IllegalArgumentException("One or both of the nodes do not exist in the graph.");
        }

        Stack<Node> stack = new Stack<>();
        Map<Node, Node> predecessors = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        stack.push(src);
        predecessors.put(src, null);
        visited.add(src);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (current.equals(dst)) {
                return buildPath(src, dst, predecessors);
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

    // Helper method to build the path from src to dst using predecessors map
    private Path buildPath(Node src, Node dst, Map<Node, Node> predecessors) {
        Path path = new Path();
        Node step = dst;

        while (step != null) {
            path.addNode(step);
            step = predecessors.get(step);
        }

        Collections.reverse(path.getNodes());
        return path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph: \n");
        sb.append("Nodes: ").append(graph.vertexSet().size()).append("\n");
        sb.append("Edges: ").append(graph.edgeSet().size()).append("\n");
        for (DefaultEdge edge : graph.edgeSet()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);
            sb.append(source).append(" -> ").append(target).append("\n");
        }
        for (Node vertex : graph.vertexSet()) {
            if (graph.edgesOf(vertex).isEmpty()) {
                sb.append(vertex).append("\n");
            }
        }
        return sb.toString();
    }
}
