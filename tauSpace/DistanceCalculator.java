// Import Py4J classes
import py4j.GatewayServer;

import beast.evolution.tree.Tree;
import beast.util.NexusParser;

import java.io.StringReader;
import java.util.List;

public class DistanceCalculator {

    public double[] computeDistances(String nexusData) {
        try {
            // Parse the nexus-formatted data from the provided string
            NexusParser parser = new NexusParser();
            StringReader stringReader = new StringReader(nexusData);
            parser.parseFile("memory", stringReader);
            List<Tree> trees = parser.trees;

            // Convert the trees to TauTree format
            TauTree[] tauTrees = new TauTree[trees.size()];
            for (int i = 0; i < trees.size(); i++) {
                tauTrees[i] = new TauTree(trees.get(i));
            }

            // Compute all pairwise distances between the trees
            int numDistances = (tauTrees.length * (tauTrees.length - 1)) / 2;
            double[] distances = new double[numDistances];
            int index = 0;
            for (int i = 0; i < tauTrees.length; i++) {
                for (int j = i + 1; j < tauTrees.length; j++) {
                    double distance = Geodesic.geodesic(tauTrees[i], tauTrees[j], 0.5).geoLength;
                    distances[index++] = distance;
                }
            }

            return distances;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // Start the Py4J GatewayServer
        DistanceCalculator app = new DistanceCalculator();
        GatewayServer server = new GatewayServer(app);
        server.start();
        System.out.println("Gateway Server Started");
    }
}
