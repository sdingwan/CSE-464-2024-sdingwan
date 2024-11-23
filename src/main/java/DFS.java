import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class DFS extends GraphTraversalTemplate {
    private Map<Node, Node> predecessors;
    private Stack<Node> stack;
    private Set<Node> visited;

    public DFS(Graph<Node, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected void initializeTraversal(Node source) {
        predecessors = new HashMap<>();
        stack = new Stack<>();
        visited = new HashSet<>();
        stack.push(source);
        predecessors.put(source, null);
        visited.add(source);
    }

    @Override
    protected void performTraversal(Node source, Node destination) {
        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (current.equals(destination)) {
                return;
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
    }

    @Override
    protected Node getPredecessor(Node node) {
        return predecessors.get(node);
    }
}
