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

    // Test for Feature 1: Parsing a DOT graph file
    @Test
    public void testParseGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected-parse.txt")).trim());
        String actualOutput = normalize(parser.toString().trim());
        assertEquals(expectedOutput, actualOutput, "The parsed graph should match the expected output.");
    }

    // Test for Feature 2: Adding a single node
    @Test
    public void testAddNode() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNode(new Node("Z"));
        Files.write(Paths.get("src/test/resources/output_add_node.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_node.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_node.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding node 'Z'.");
    }

    // Test for Feature 2: Adding multiple nodes
    @Test
    public void testAddMultipleNodes() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNodes(new Node[]{new Node("X"), new Node("Y")});
        Files.write(Paths.get("src/test/resources/output_add_nodes.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_nodes.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_nodes.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding nodes 'X' and 'Y'.");
    }

    // Test for Feature 3: Adding an edge
    @Test
    public void testAddEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addEdge(nodeA, nodeB);
        Files.write(Paths.get("src/test/resources/output_add_edge.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_edge.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_edge.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding an edge from 'A' to 'B'.");
    }

    // Test for Feature 4: Outputting the graph to a DOT file
    @Test
    public void testOutputDOTGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Node nodeC = new Node("C");
        parser.addNode(nodeC);
        parser.addEdge(new Node("A"), nodeC);
        parser.outputDOTGraph("src/test/resources/output_graph.dot");
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_output.dot")).replace("\r\n", "\n"));
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_graph.dot")).replace("\r\n", "\n"));
        assertEquals(expectedOutput, actualOutput, "The DOT file should match the expected structure after updates.");
    }

    // Test for Feature 4: Outputting the graph to a PNG file
    @Test
    public void testOutputGraphics() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Node nodeD = new Node("D");
        Node nodeH = new Node("H");
        parser.addNode(nodeD);
        parser.addNode(nodeH);
        parser.addEdge(new Node("B"), nodeD);
        parser.addEdge(nodeD, nodeH);
        parser.addEdge(nodeH, new Node("A"));
        String pngOutputPath = "src/test/resources/output_graph.png";
        parser.outputGraphics(pngOutputPath, "png");
        File outputFile = new File(pngOutputPath);
        assertTrue(outputFile.exists(), "The PNG file should be created.");
    }

    // New Tests for Removal APIs

    // Test removing a single node
    @Test
    public void testRemoveNode() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Node nodeA = new Node("A");
        parser.removeNode(nodeA);
        assertFalse(parser.getGraph().containsVertex(nodeA), "Node 'A' should be removed.");
        assertFalse(parser.getGraph().containsEdge(nodeA, new Node("B")), "Edge from 'A' to 'B' should also be removed.");
    }

    // Test removing multiple nodes
    @Test
    public void testRemoveMultipleNodes() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        parser.removeNodes(new Node[]{nodeA, nodeB});
        assertFalse(parser.getGraph().containsVertex(nodeA), "Node 'A' should be removed.");
        assertFalse(parser.getGraph().containsVertex(nodeB), "Node 'B' should be removed.");
        assertFalse(parser.getGraph().containsEdge(nodeA, nodeB), "Edge from 'A' to 'B' should be removed.");
        assertFalse(parser.getGraph().containsEdge(nodeB, new Node("C")), "Edge from 'B' to 'C' should also be removed.");
    }

    // Test removing a single edge
    @Test
    public void testRemoveEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.removeEdge(new Node("A"), new Node("B"));
        assertFalse(parser.getGraph().containsEdge(new Node("A"), new Node("B")), "Edge from 'A' to 'B' should be removed.");
    }

    // Test removing a non-existent node (should throw exception)
    @Test
    public void testRemoveNonExistentNode() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.removeNode(new Node("Z"));
        });
        assertEquals("Node Z does not exist in the graph.", exception.getMessage());
    }

    // Test removing a non-existent edge (should throw exception)
    @Test
    public void testRemoveNonExistentEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.removeEdge(new Node("A"), new Node("C"));
        });
        assertEquals("Edge from A to C does not exist in the graph.", exception.getMessage());
    }

    // Test finding a path between two connected nodes using BFS
    @Test
    public void testGraphSearchBFSPathExists() throws IOException {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addNode(nodeC);
        parser.addNode(nodeD);

        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);
        parser.addEdge(nodeC, nodeD);

        Path path = parser.GraphSearch(nodeA, nodeD, GraphParser.Algorithm.BFS);

        assertNotNull(path, "A path should exist from 'A' to 'D' using BFS.");
        assertEquals("A -> B -> C -> D", path.toString(), "The path should match the expected sequence.");
    }

    // Test returning null if there is no path between two nodes using BFS
    @Test
    public void testGraphSearchBFSNoPath() throws IOException {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addNode(nodeC);
        parser.addNode(nodeD);

        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);

        Path path = parser.GraphSearch(nodeA, nodeD, GraphParser.Algorithm.BFS);

        assertNull(path, "No path should exist from 'A' to 'D' using BFS as they are not connected.");
    }

    // Test searching from a node to itself using BFS (should return a path with a single node)
    @Test
    public void testGraphSearchBFSSameNode() throws IOException {
        Node nodeA = new Node("A");

        parser.addNode(nodeA);

        Path path = parser.GraphSearch(nodeA, nodeA, GraphParser.Algorithm.BFS);

        assertNotNull(path, "A path should exist from 'A' to itself using BFS.");
        assertEquals("A", path.toString(), "The path should contain only 'A'.");
    }

    // Test finding a path between two connected nodes using DFS
    @Test
    public void testGraphSearchDFSPathExists() throws IOException {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addNode(nodeC);
        parser.addNode(nodeD);

        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);
        parser.addEdge(nodeC, nodeD);

        Path path = parser.GraphSearch(nodeA, nodeD, GraphParser.Algorithm.DFS);

        assertNotNull(path, "A path should exist from 'A' to 'D' using DFS.");
        assertEquals("A -> B -> C -> D", path.toString(), "The path should match the expected sequence.");
    }

    // Test returning null if there is no path between two nodes using DFS
    @Test
    public void testGraphSearchDFSNoPath() throws IOException {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");

        parser.addNode(nodeA);
        parser.addNode(nodeB);
        parser.addNode(nodeC);
        parser.addNode(nodeD);

        parser.addEdge(nodeA, nodeB);
        parser.addEdge(nodeB, nodeC);

        Path path = parser.GraphSearch(nodeA, nodeD, GraphParser.Algorithm.DFS);

        assertNull(path, "No path should exist from 'A' to 'D' using DFS as they are not connected.");
    }

    // Test searching from a node to itself using DFS (should return a path with a single node)
    @Test
    public void testGraphSearchDFSSameNode() throws IOException {
        Node nodeA = new Node("A");

        parser.addNode(nodeA);

        Path path = parser.GraphSearch(nodeA, nodeA, GraphParser.Algorithm.DFS);

        assertNotNull(path, "A path should exist from 'A' to itself using DFS.");
        assertEquals("A", path.toString(), "The path should contain only 'A'.");
    }
}
