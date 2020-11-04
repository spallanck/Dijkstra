//Author: Sophie Pallanck
//Date: 6/4/20
/*Purpose: This program provides testing code to ensure
the correctness of the ShortestsPaths file */
package graph;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.LinkedList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortestPathsTest {
    
    /* Performs the necessary gradle-related incantation to get the
    filename of a graph text file in the src/test/resources directory at
    test time.*/
    private String getGraphResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.getPath();
    }
    
    /** Tests on a case where the destination node
    * is not reachable */
    @Test
    public void test00NotInGraph() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 5); //only edge from a --> b
        ShortestPaths sp = new ShortestPaths();
        sp.compute(b);
        LinkedList<Node> abPath = sp.shortestPath(a);
        double abPathLength = sp.shortestPathLength(a);
        assertTrue(true);
        assertEquals(null, abPath);
        assertEquals(Double.POSITIVE_INFINITY, abPathLength, 0);
    }
    
    /* Tests a case with an undirected edge between
    two nodes in a graph */
    @Test
    public void test10DoubleEdge() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 5); // a --> b
        g.addEdge(b, a, 6); // b --> a
        ShortestPaths sp = new ShortestPaths();
        sp.compute(a);
        LinkedList<Node> abPath = sp.shortestPath(b);
        double abPathLength = sp.shortestPathLength(b);
        
        LinkedList<Node> testy = new LinkedList<>();
        testy.addFirst(b);
        testy.addFirst(a);
        assertTrue(true);
        assertEquals(testy, abPath);
        assertEquals(5, abPathLength, 0);
        sp.compute(b);
        abPath = sp.shortestPath(a);
        abPathLength = sp.shortestPathLength(a);
        assertTrue(true);
        assertEquals(6, abPathLength, 0);
    }
    
    /**Tests the case where a better path is
    * found to a destination node */
    @Test
    public void test20FoundBetter() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        Node c = g.getNode("C");
        g.addEdge(a, b, 5);
        g.addEdge(a, c, 1); //part of shorter path
        g.addEdge(c, b, 2); //part of shorter path
        ShortestPaths sp = new ShortestPaths();
        sp.compute(a);
        LinkedList<Node> abPath = sp.shortestPath(b);
        double abPathLength = sp.shortestPathLength(b);
        
        LinkedList<Node> testy = new LinkedList<>();
        testy.addFirst(b);
        testy.addFirst(c);
        testy.addFirst(a);
        assertTrue(true);
        assertEquals(testy, abPath);
        assertEquals(3, abPathLength, 0);
    }
    
    /** Tests the case where origin node
    * has no neighbors */
    @Test
    public void test30OneNode() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        ShortestPaths sp = new ShortestPaths();
        sp.compute(a);
        LinkedList<Node> abPath = sp.shortestPath(b);
        double abPathLength = sp.shortestPathLength(b);
        assertTrue(true);
        assertEquals(null, abPath);
        assertEquals(Double.POSITIVE_INFINITY, abPathLength, 0);
    }
    
    /** Tests on a few nodes from Simple1.txt  */
    @Test
    public void test40Simple1Tests() {
        String fn = getGraphResource("Simple1.txt");
        Graph simple1;
        try {
            simple1 = ShortestPaths.parseGraph("basic", fn);
        } catch (FileNotFoundException e) {
            fail("Could not find graph Simple1.txt");
            return;
        }
        ShortestPaths sp = new ShortestPaths();
        Node s = simple1.getNode("S");
        sp.compute(s);
        LinkedList<Node> abPath = sp.shortestPath(s);
        double abPathLength = sp.shortestPathLength(s);
        LinkedList<Node> testResult = new LinkedList<>();
        testResult.addFirst(s);
        assertEquals(testResult, abPath);
        assertEquals(0, abPathLength, 0);
        Node a = simple1.getNode("A");
        Node c = simple1.getNode("C");
        abPath = sp.shortestPath(a);
        abPathLength = sp.shortestPathLength(a);
        testResult.addLast(c);
        testResult.addLast(a);
        assertEquals(testResult, abPath);
        assertEquals(8, abPathLength, 0);
    }
    
    /** Tests on the sample graph we ran through
    * in lecture */
    @Test
    public void test50LectureGraph() {
        ShortestPaths sp = new ShortestPaths();
        Graph g = new Graph();
        Node one = g.getNode("1");
        Node two = g.getNode("2");
        Node three = g.getNode("3");
        Node four = g.getNode("4");
        Node five = g.getNode("5");
        Node six = g.getNode("6");
        g.addEdge(one, two, 7);
        g.addEdge(one, six, 14);
        g.addEdge(one, three, 9);
        g.addEdge(two, three, 10);
        g.addEdge(three, six, 2);
        g.addEdge(two, four, 15);
        g.addEdge(three, four, 11);
        g.addEdge(six, five, 9);
        g.addEdge(four, five, 6);
        sp.compute(one);
        double abPathLength = sp.shortestPathLength(one);
        assertEquals(0, abPathLength, 0);
        abPathLength = sp.shortestPathLength(two);
        assertEquals(7, abPathLength, 0);
        abPathLength = sp.shortestPathLength(three);
        assertEquals(9, abPathLength, 0);
        abPathLength = sp.shortestPathLength(four);
        assertEquals(20, abPathLength, 0);
        abPathLength = sp.shortestPathLength(five);
        assertEquals(20, abPathLength, 0);
        abPathLength = sp.shortestPathLength(six);
        assertEquals(11, abPathLength, 0);
    }
}