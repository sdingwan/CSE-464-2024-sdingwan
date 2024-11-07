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
        parser.addNode("Z");
        Files.write(Paths.get("src/test/resources/output_add_node.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_node.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_node.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding node 'Z'.");
    }

    // Test for Feature 2: Adding multiple nodes
    @Test
    public void testAddMultipleNodes() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNodes(new String[]{"X", "Y"});
        Files.write(Paths.get("src/test/resources/output_add_nodes.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_nodes.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_nodes.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding nodes 'X' and 'Y'.");
    }

    // Test for Feature 3: Adding an edge
    @Test
    public void testAddEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNode("A");
        parser.addNode("B");
        parser.addEdge("A", "B");
        Files.write(Paths.get("src/test/resources/output_add_edge.txt"), parser.toString().getBytes());
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_add_edge.txt")).trim());
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_add_edge.txt")).trim());
        assertEquals(expectedOutput, actualOutput, "The graph should match the expected output after adding an edge from 'A' to 'B'.");
    }

    // Test for Feature 4: Outputting the graph to a DOT file
    @Test
    public void testOutputDOTGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNode("C");
        parser.addEdge("A", "C");
        parser.outputDOTGraph("src/test/resources/output_graph.dot");
        String expectedOutput = normalize(Files.readString(Paths.get("src/test/resources/expected_output.dot")).replace("\r\n", "\n"));
        String actualOutput = normalize(Files.readString(Paths.get("src/test/resources/output_graph.dot")).replace("\r\n", "\n"));
        assertEquals(expectedOutput, actualOutput, "The DOT file should match the expected structure after updates.");
    }

    // Test for Feature 4: Outputting the graph to a PNG file
    @Test
    public void testOutputGraphics() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.addNode("D");
        parser.addNode("H");
        parser.addEdge("B", "D");
        parser.addEdge("D", "H");
        parser.addEdge("H", "A");
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
        parser.removeNode("A");
        assertFalse(parser.getGraph().containsVertex("A"), "Node 'A' should be removed.");
        assertFalse(parser.getGraph().containsEdge("A", "B"), "Edge from 'A' to 'B' should also be removed.");
    }

    // Test removing multiple nodes
    @Test
    public void testRemoveMultipleNodes() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.removeNodes(new String[]{"A", "B"});
        assertFalse(parser.getGraph().containsVertex("A"), "Node 'A' should be removed.");
        assertFalse(parser.getGraph().containsVertex("B"), "Node 'B' should be removed.");
        assertFalse(parser.getGraph().containsEdge("A", "B"), "Edge from 'A' to 'B' should be removed.");
        assertFalse(parser.getGraph().containsEdge("B", "C"), "Edge from 'B' to 'C' should also be removed.");
    }

    // Test removing a single edge
    @Test
    public void testRemoveEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        parser.removeEdge("A", "B");
        assertFalse(parser.getGraph().containsEdge("A", "B"), "Edge from 'A' to 'B' should be removed.");
    }

    // Test removing a non-existent node (should throw exception)
    @Test
    public void testRemoveNonExistentNode() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.removeNode("Z");
        });
        assertEquals("Node Z does not exist in the graph.", exception.getMessage());
    }

    // Test removing a non-existent edge (should throw exception)
    @Test
    public void testRemoveNonExistentEdge() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.removeEdge("A", "C");
        });
        assertEquals("Edge from A to C does not exist in the graph.", exception.getMessage());
    }
}
