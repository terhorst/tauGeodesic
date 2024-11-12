/*
 * Copyright (C) 2014 Alex Gavryushkin <alex@gavruskin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import beast.evolution.tree.Tree;
import beast.util.NexusParser;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Waiting for nexus-formatted input. Paste your nexus file:");
            StringBuilder inputBuilder = new StringBuilder();
            String line;
            boolean startFound = false;
            int endCount = 0;

            // Read lines until we find '#NEXUS'
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip leading empty lines
                if (!startFound && line.isEmpty()) {
                    continue;
                }

                // Check for the start of the nexus file
                if (!startFound && line.equalsIgnoreCase("#NEXUS")) {
                    startFound = true;
                    inputBuilder.append(line).append("\n");
                    continue;
                }

                // If we haven't found the start yet, skip this line
                if (!startFound) {
                    continue;
                }

                // Accumulate the lines
                inputBuilder.append(line).append("\n");

                // Check for the end of the nexus file
                if (line.equalsIgnoreCase("END;")) {
                    endCount++;
                    if (endCount >= 2) {
                        break;
                    }
                }
            }

            // If EOF is reached, exit the loop
            if (line == null) {
                break;
            }

            // If we didn't find the start or end, prompt the user again
            if (!startFound || endCount < 2) {
                System.err.println("Incomplete nexus file detected. Please ensure the file starts with '#NEXUS' and ends with 'END;'.");
                continue;
            }

            String inputData = inputBuilder.toString();
            // Now parse the inputData using NexusParser
            try {
                NexusParser parser = new NexusParser();
                StringReader stringReader = new StringReader(inputData);

                // Assuming NexusParser has been modified to accept a Reader
                parser.parseFile("memory", stringReader);

                List<Tree> trees = parser.trees;
                System.out.println("The number of trees is " + trees.size() + ".\n");

                // Convert the trees to TauTree format
                TauTree[] tauTrees = new TauTree[trees.size()];
                for (int i = 0; i < trees.size(); i++) {
                    tauTrees[i] = new TauTree(trees.get(i));
                }

                // Compute all pairwise distances between the trees
                System.out.println("Computing pairwise distances...");
                int numTrees = tauTrees.length;
                for (int i = 0; i < numTrees; i++) {
                    for (int j = i + 1; j < numTrees; j++) {
                        double distance = Geodesic.geodesic(tauTrees[i], tauTrees[j], 1.0).geoLength;
                        System.out.println("Distance between tree " + (i + 1) + " and tree " + (j + 1) + " is " + distance);
                    }
                }
                System.out.println("Done computing distances.\n");
            } catch (Exception e) {
                System.err.println("Error parsing input or computing distances: " + e.getMessage());
                e.printStackTrace();
            }
            // Continue to next iteration
        }
    }
}
