import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class GraphParserTest {

    private GraphParser parser;

    @BeforeEach
    public void setUp() {
        parser = new GraphParser();
    }

    // Utility method to normalize output for comparison
    private String normalize(String input) {
        return input.replaceAll("\\s+", "");
    }

    // Test for parsing a DOT graph file
    @Test
    public void testParseGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected-parse.txt")).trim());
        String actualOutput = normalize(parser.toString().trim());
        assertEquals(expectedOutput, actualOutput, "The parsed graph should match the expected output.");
    }

    // Test for adding a single node
    @Test
    public void testAddNode() throws IOException {
        parser.addNode(new Node("Z"));
        assertTrue(parser.getGraph().containsVertex(new Node("Z")), "The graph should contain the newly added node 'Z'.");
    }

    // Test for adding multiple nodes
    @Test
    public void testAddMultipleNodes() throws IOException {
        Node[] nodes = {new Node("X"), new Node("Y")};
        parser.addNodes(nodes);
        assertTrue(parser.getGraph().containsVertex(new Node("X")), "The graph should contain the newly added node 'X'.");
        assertTrue(parser.getGraph().containsVertex(new Node("Y")), "The graph should contain the newly added node 'Y'.");
    }

    // Test for adding an edge
    @Test
    public void testAddEdge() throws IOException {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addEdge(nodeA, nodeB);
        assertTrue(parser.getGraph().containsEdge(nodeA, nodeB), "The graph should contain the edge from 'A' to 'B'.");
    }

    // Test for outputting the graph to a DOT file
    @Test
    public void testOutputDOTGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.outputDOTGraph("src/test/resources/output_graph.dot");
        File outputFile = new File("src/test/resources/output_graph.dot");
        assertTrue(outputFile.exists(), "The DOT file should be created.");
    }

    // Test for outputting the graph to a PNG file
    @Test
    public void testOutputGraphics() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.outputGraphics("src/test/resources/output_graph.png", "png");
        File outputFile = new File("src/test/resources/output_graph.png");
        assertTrue(outputFile.exists(), "The PNG file should be created.");
    }

    // Test for removing a single node
    @Test
    public void testRemoveNode() {
        Node nodeA = new Node("A");
        parser.addNode(nodeA);
        parser.removeNode(nodeA);
        assertFalse(parser.getGraph().containsVertex(nodeA), "The node 'A' should be removed from the graph.");
    }

    // Test for removing multiple nodes
    @Test
    public void testRemoveMultipleNodes() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        parser.addNodes(new Node[]{nodeA, nodeB});
        parser.removeNodes(new Node[]{nodeA, nodeB});
        assertFalse(parser.getGraph().containsVertex(nodeA), "The node 'A' should be removed from the graph.");
        assertFalse(parser.getGraph().containsVertex(nodeB), "The node 'B' should be removed from the graph.");
    }

    // Test for removing an edge
    @Test
    public void testRemoveEdge() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addEdge(nodeA, nodeB);
        parser.removeEdge(nodeA, nodeB);
        assertFalse(parser.getGraph().containsEdge(nodeA, nodeB), "The edge from 'A' to 'B' should be removed.");
    }

    // Test for finding a path using BFS
    @Test
    public void testGraphSearchBFS() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        parser.addNodes(new Node[]{nodeA, nodeB, nodeC, nodeD});
        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);
        parser.addEdge(nodeC, nodeD);
        Path path = parser.graphSearch(nodeA, nodeD, GraphParser.Algorithm.BFS);
        assertNotNull(path, "A path should exist from 'A' to 'D' using BFS.");
        assertEquals("A -> B -> C -> D", path.toString(), "The path should match the expected output.");
    }

    // Test for finding a path using DFS
    @Test
    public void testGraphSearchDFS() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        parser.addNodes(new Node[]{nodeA, nodeB, nodeC, nodeD});
        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);
        parser.addEdge(nodeC, nodeD);
        Path path = parser.graphSearch(nodeA, nodeD, GraphParser.Algorithm.DFS);
        assertNotNull(path, "A path should exist from 'A' to 'D' using DFS.");
        assertEquals("A -> B -> C -> D", path.toString(), "The path should match the expected output.");
    }

    // Test for dynamic strategy selection (Strategy Pattern)
    @Test
    public void testGraphSearchStrategyPattern() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        parser.addNodes(new Node[]{nodeA, nodeB, nodeC, nodeD});
        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);
        parser.addEdge(nodeC, nodeD);

        // Use BFS
        Path bfsPath = parser.graphSearch(nodeA, nodeD, GraphParser.Algorithm.BFS);
        assertNotNull(bfsPath, "A path should exist from 'A' to 'D' using BFS.");
        assertEquals("A -> B -> C -> D", bfsPath.toString(), "The BFS path should match the expected output.");

        // Use DFS
        Path dfsPath = parser.graphSearch(nodeA, nodeD, GraphParser.Algorithm.DFS);
        assertNotNull(dfsPath, "A path should exist from 'A' to 'D' using DFS.");
        assertEquals("A -> B -> C -> D", dfsPath.toString(), "The DFS path should match the expected output.");
    }
}
