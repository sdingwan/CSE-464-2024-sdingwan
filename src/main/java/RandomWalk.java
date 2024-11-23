import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class RandomWalk implements GraphTraversalStrategy {
    private final Random random = new Random();

    @Override
    public Path traverse(Graph<Node, DefaultEdge> graph, Node source, Node destination) {
        if (!graph.containsVertex(source) || !graph.containsVertex(destination)) {
            throw new IllegalArgumentException("One or both nodes do not exist.");
        }

        Path path = new Path();
        Set<Node> visited = new HashSet<>();
        Node current = source;

        path.addNode(current);
        visited.add(current);
        System.out.println("visiting Path{" + path + "}");

        while (!current.equals(destination)) {
            // Get neighbors
            List<Node> neighbors = new ArrayList<>();
            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }

            // If no unvisited neighbors, terminate the search
            if (neighbors.isEmpty()) {
                return null; // Random walk failed
            }

            // Pick a random neighbor
            current = neighbors.get(random.nextInt(neighbors.size()));
            path.addNode(current);
            visited.add(current);
            System.out.println("visiting Path{" + path + "}");
        }

        return path; // Random walk succeeded
    }
}
