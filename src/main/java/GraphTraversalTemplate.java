import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public abstract class GraphTraversalTemplate {
    protected Graph<Node, DefaultEdge> graph;

    public GraphTraversalTemplate(Graph<Node, DefaultEdge> graph) {
        this.graph = graph;
    }

    // Template method defining the steps of traversal
    public final Path traverse(Node source, Node destination) {
        if (!graph.containsVertex(source) || !graph.containsVertex(destination)) {
            throw new IllegalArgumentException("One or both nodes do not exist.");
        }

        initializeTraversal(source);
        performTraversal(source, destination);
        return buildPath(source, destination);
    }

    // Abstract steps for differing behaviors
    protected abstract void initializeTraversal(Node source);
    protected abstract void performTraversal(Node source, Node destination);
    protected abstract Node getPredecessor(Node node);

    // Common method to build the path
    protected Path buildPath(Node source, Node destination) {
        Path path = new Path();
        Node step = destination;

        while (step != null) {
            path.addNode(step);
            step = getPredecessor(step);
        }

        Collections.reverse(path.getNodes());
        return path;
    }
}
