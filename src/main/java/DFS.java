import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class DFS implements GraphTraversalStrategy {

    @Override
    public Path traverse(Graph<Node, DefaultEdge> graph, Node source, Node destination) {
        // Validate if the source and destination nodes exist
        if (!graph.containsVertex(source) || !graph.containsVertex(destination)) {
            throw new IllegalArgumentException("One or both nodes do not exist in the graph.");
        }

        // Initialize data structures for DFS traversal
        Map<Node, Node> predecessors = new HashMap<>();
        Stack<Node> stack = new Stack<>();
        Set<Node> visited = new HashSet<>();
        stack.push(source);
        predecessors.put(source, null);
        visited.add(source);

        // Perform DFS traversal
        while (!stack.isEmpty()) {
            Node current = stack.pop();

            // If destination is reached, build the path
            if (current.equals(destination)) {
                return buildPath(destination, predecessors);
            }

            // Process neighbors
            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            }
        }

        // Return null if no path exists
        return null;
    }

    // Helper method to build the path from the predecessors map
    private Path buildPath(Node destination, Map<Node, Node> predecessors) {
        Path path = new Path();
        Node step = destination;

        // Trace back from the destination to the source using the predecessors map
        while (step != null) {
            path.addNode(step);
            step = predecessors.get(step);
        }

        // Reverse the path to get the correct order
        Collections.reverse(path.getNodes());
        return path;
    }
}
