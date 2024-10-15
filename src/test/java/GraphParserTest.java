import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class GraphParserTest {

    private GraphParser parser;

    // Setup method to initialize the GraphParser object before each test
    @BeforeEach
    public void setUp() {
        parser = new GraphParser();
    }

    // Test Feature 1: Parsing a DOT graph file
    @Test
    public void testParseGraph() throws IOException {
        parser.parseGraph("src/test/resources/sample.dot"); // Input DOT file

        String expectedOutput = Files.readString(Paths.get("src/test/resources/expected-output.txt")).trim();
        String actualOutput = parser.toString().trim().replaceAll("\\s+", " ").replaceAll("\r\n", "\n").replaceAll("\n", " ").trim();

        System.out.println("Expected: \n" + expectedOutput);
        System.out.println("Actual: \n" + actualOutput);

        assertEquals(expectedOutput.replaceAll("\\s+", " ").replaceAll("\r\n", "\n").replaceAll("\n", " ").trim(), actualOutput, "The outputs should match when formatted uniformly.");
    }



    // Test Feature 2: Adding a single node
    @Test
    public void testAddNode() {
        parser.addNode("A");
        parser.addNode("B");
        parser.addNode("A");  // Adding duplicate node

        // Check that the number of nodes is 2 (no duplicates)
        assertEquals(2, parser.getGraph().vertexSet().size());
    }

    // Test Feature 2: Adding a list of nodes
    @Test
    public void testAddNodes() {
        String[] nodes = {"A", "B", "C"};
        parser.addNodes(nodes);

        // Check that all nodes were added
        assertEquals(3, parser.getGraph().vertexSet().size());
    }

    // Test Feature 3: Adding an edge
    @Test
    public void testAddEdge() {
        parser.addNode("A");
        parser.addNode("B");
        parser.addEdge("A", "B");
        parser.addEdge("A", "B");  // Adding duplicate edge

        // Check that only 1 edge was added (no duplicates)
        assertEquals(1, parser.getGraph().edgeSet().size());
    }

    // Test Feature 4: Output the graph to DOT file
    @Test
    public void testOutputDOTGraph() throws IOException {
        // Adding nodes
        parser.addNode("A");
        parser.addNode("B");
        parser.addNode("C");
        parser.addNode("D");

        // Adding edges
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addEdge("C", "D");

        // Output the graph to a DOT file
        String outputPath = "src/test/resources/output.dot";
        parser.outputDOTGraph(outputPath);

        // Read the expected and actual DOT file content
        String expectedDOT = Files.readString(Paths.get("src/test/resources/expected.dot"));
        String actualDOT = Files.readString(Paths.get(outputPath));

        // Normalize newline characters across different environments
        expectedDOT = expectedDOT.replace("\r\n", "\n");
        actualDOT = actualDOT.replace("\r\n", "\n");

        // Assertion to check if the contents are the same
        assertEquals(expectedDOT, actualDOT, "The DOT files should match expected structure.");
    }


    // Test Feature 4: Output the graph to PNG file
    @Test
    public void testOutputGraphics() throws IOException {
        parser.addNode("A");
        parser.addNode("B");
        parser.addEdge("A", "B");

        // Output the graph to a PNG file
        parser.outputGraphics("src/test/resources/output.png", "png");

        // Verify that the PNG file was created
        File outputFile = new File("src/test/resources/output.png");
        assertTrue(outputFile.exists());
    }

    // Additional Test: Ensure correct graph string representation (toString)
    @Test
    public void testGraphToString() {
        parser.addNode("A");
        parser.addNode("B");
        parser.addEdge("A", "B");

        String expectedString = "Graph: \nNodes: 2\nEdges: 1\nA -> B\n";
        assertEquals(expectedString, parser.toString());
    }
}
