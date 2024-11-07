import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private List<Node> nodes;

    public Path() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        List<String> labels = new ArrayList<>();
        for (Node node : nodes) {
            labels.add(node.getLabel());
        }
        return String.join(" -> ", labels);
    }
}
