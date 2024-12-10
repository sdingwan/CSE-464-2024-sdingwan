import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class BFS extends GraphTraversalTemplate {
    private final Map<Node, Node> predecessors = new HashMap<>();

    public BFS(Graph<Node, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected void initializeTraversal(Node source) {
        // Clear any previous state
        predecessors.clear();
        predecessors.put(source, null);
        System.out.println("Initializing BFS traversal. Starting at node: " + source);
    }

    @Override
    protected void performTraversal(Node source, Node destination) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(source);

        // Perform BFS traversal
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // If destination is reached, stop traversal
            if (current.equals(destination)) {
                System.out.println("Destination node found: " + destination);
                break;
            }

            // Process neighbors
            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!predecessors.containsKey(neighbor)) {
                    queue.add(neighbor);
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
