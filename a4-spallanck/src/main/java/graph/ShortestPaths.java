//Author: Sophie Pallanck
//Date: 6/3/20
/*Purpose: This program parses a graph and
returns information regarding the shortest paths between
specified nodes in the graph. */
package graph;

import heap.Heap;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node,PathData> paths;

    /** Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * backpointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.*/
    public void compute(Node origin) {
        paths = new HashMap<Node,PathData>();
        Heap<Node, Double> frontier = new Heap<>();
        frontier.add(origin, 0.0);
        paths.put(origin, new PathData(0.0, null));
        while (frontier.size() != 0) {
            Node f = frontier.poll();
            for (Node w : f.getNeighbors().keySet()) {
                double fD = paths.get(f).distance;
                double weight = f.getNeighbors().get(w);
                if (!paths.containsKey(w)) { //haven't seen before
                    paths.put(w, new PathData((fD + weight), f));
                    frontier.add(w, (fD + weight));
                } else if ((fD +  weight) < paths.get(w).distance) {
                    PathData temp = paths.get(w);
                    temp.distance = (fD + weight);
                    temp.previous = f;
                    paths.put(w, temp);
                    frontier.changePriority(w, (fD + weight));
                }
            }
        }
    }

    /** Returns the length  the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public double shortestPathLength(Node destination) {
        PathData temp = paths.get(destination);
        if (temp != null) {
            return temp.distance;
        }
        return Double.POSITIVE_INFINITY;
    }

    /** Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public LinkedList<Node> shortestPath(Node destination) {
        PathData temp = paths.get(destination);
        if (temp != null) {
            LinkedList<Node> path = new LinkedList<>();
            path.addFirst(destination);
            Node tmpN = destination;
            while (paths.get(tmpN) != null) {
                if (paths.get(tmpN).previous != null) { //don't add dest twice if it = origin
                    path.addFirst(paths.get(tmpN).previous);
                }
                tmpN = paths.get(tmpN).previous;
            }
            return path;
        }
        return null;
    }

    /** Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }

    /** Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a DB1B CSV file with
     * flight data. See GraphParser, BasicParser, and DB1BParser for more.*/
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    /** Takes command line arguments to access and parse a graph 
     * and comute the shortests paths associated with the given user
     * input
     * Precondition: User input is well formed and valid */
    public static void main(String[] args) {
        // read command line args
        String fileType = args[0];
        String fileName = args[1];
        String origCode = args[2];
        
        String destCode = null;
        if (args.length == 4) {
            destCode = args[3];
        }
        
        // parse a graph with the given type and filename
        Graph graph;
        try {
            graph = parseGraph(fileType, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName);
            return;
        }
        graph.report();
        ShortestPaths sp = new ShortestPaths();
        /* error checking to make sure user does not specify
        a node not contained in the graph */
        if (!graph.getNodes().containsKey(origCode)) {
            System.out.println("The origin node specified is not contained within graph");
            return;
        }
        Node origin = graph.getNode(origCode);
        sp.compute(origin);
        /* if a destination is specified, prints the path
        between origin and destination as well as the length of
        that path, or tells the user there is no path between 
        the origin and destination */
        if (args.length == 4) {
            Node dest = graph.getNode(destCode);
            LinkedList<Node> pathLetter = sp.shortestPath(dest);
            Double length = sp.shortestPathLength(dest);
            if (pathLetter == null) {
                System.out.println("No path exists between these nodes.");
                return;
            }
            for (Node a : pathLetter) {
                System.out.print(a.getId() + " ");
            }
            System.out.println(length);
        } else {
            /* If no destination is specified, prints all nodes reachable from
            the origin and the distance between them */
            System.out.println("Shortest paths from " + origCode + ":");
            for (Node a : sp.paths.keySet()) {
                double lengthP = sp.paths.get(a).distance;
                String code = a.getId();
                System.out.println(code + ": " + lengthP);
            }
        }
    }
}
