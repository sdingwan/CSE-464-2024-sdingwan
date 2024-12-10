import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class DFS extends GraphTraversalTemplate {
    private final Map<Node, Node> predecessors = new HashMap<>();
    private final Set<Node> visited = new HashSet<>();

    public DFS(Graph<Node, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected void initializeTraversal(Node source) {
        // Clear previous state
        predecessors.clear();
        visited.clear();
        predecessors.put(source, null);
        System.out.println("Initializing DFS traversal. Starting at node: " + source);
    }

    @Override
    protected void performTraversal(Node source, Node destination) {
        Stack<Node> stack = new Stack<>();
        stack.push(source);
        visited.add(source);

        // Perform DFS traversal
        while (!stack.isEmpty()) {
            Node current = stack.pop();

            // If destination is found, stop traversal
            if (current.equals(destination)) {
                System.out.println("Destination node found: " + destination);
                break;
            }

            // Process neighbors
            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    predecessors.put(neighbor, current);
                    System.out.println("Visiting node: " + neighbor);
                }
            }
        }
    }

    @Override
    protected Node getPredecessor(Node node) {
        // Retrieve the predecessor of the given node
        return predecessors.get(node);
    }
}
