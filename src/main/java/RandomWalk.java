import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class RandomWalk extends GraphTraversalTemplate {
    private final Random random = new Random();
    private Node current;

    public RandomWalk(Graph<Node, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected void initializeTraversal(Node source) {
        current = source;
        System.out.println("Initializing traversal. Starting at node: " + source);
    }

    @Override
    protected void performTraversal(Node source, Node destination) {
        Path path = new Path();
        Set<Node> visited = new HashSet<>();
        path.addNode(current);
        visited.add(current);

        while (!current.equals(destination)) {
            List<Node> neighbors = new ArrayList<>();
            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }

            if (neighbors.isEmpty()) {
                System.out.println("No more unvisited neighbors. Random walk failed.");
                return; // Exit without marking success
            }

            current = neighbors.get(random.nextInt(neighbors.size()));
            path.addNode(current);
            visited.add(current);
            System.out.println("visiting Path{nodes=" + path.getNodes() + "}");
        }

        System.out.println("Random walk succeeded. Path: " + path);
    }


    @Override
    protected Node getPredecessor(Node node) {
        return null; // Predecessors are not required for this implementation.
    }
}
