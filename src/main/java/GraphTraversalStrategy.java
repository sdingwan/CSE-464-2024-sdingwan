import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface GraphTraversalStrategy {
    Path traverse(Graph<Node, DefaultEdge> graph, Node source, Node destination);
}
