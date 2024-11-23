import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class BFS extends GraphTraversalTemplate {
    private Map<Node, Node> predecessors;
    private Queue<Node> queue;

    public BFS(Graph<Node, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected void initializeTraversal(Node source) {
        predecessors = new HashMap<>();
        queue = new LinkedList<>();
        queue.add(source);
        predecessors.put(source, null);
    }

    @Override
    protected void performTraversal(Node source, Node destination) {
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(destination)) {
                return;
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                Node neighbor = graph.getEdgeTarget(edge);
                if (!predecessors.containsKey(neighbor)) {
                    queue.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            }
        }
    }

    @Override
    protected Node getPredecessor(Node node) {
        return predecessors.get(node);
    }
}
