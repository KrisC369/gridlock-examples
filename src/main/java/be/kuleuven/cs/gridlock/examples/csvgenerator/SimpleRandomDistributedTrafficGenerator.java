package be.kuleuven.cs.gridlock.examples.csvgenerator;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.FileConfigurationHelper;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.math3.random.RandomData;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 * @credit Rutger Claes <rutger.claes@cs.kuleuven.be>
 */
public class SimpleRandomDistributedTrafficGenerator extends MatrixBasedTrafficGenerator {

    public static final String MATLAB_ROUTE_FILE = "traffic.generator.matrix.routes.file";
    private final Map<NodeReference, Map<NodeReference, Integer>> traffic;

    public SimpleRandomDistributedTrafficGenerator(Configuration config) throws IOException {
        super(config);
        this.traffic = new HashMap<NodeReference, Map<NodeReference, Integer>>();
        this.readMatrix(config);
    }

    public SimpleRandomDistributedTrafficGenerator(Graph<NodeReference, LinkReference> graph, RandomData random, Configuration configuration) throws IOException {
        super(graph, random, configuration);
        this.traffic = new HashMap<NodeReference, Map<NodeReference, Integer>>();
        this.readMatrix(configuration);
    }

    private void readMatrix(Configuration configuration) throws IOException {
        String fileLocation = configuration.getString(GENERATOR_MATRIX_LOCATION_KEY, null);
        InputStream input = FileConfigurationHelper.open(fileLocation, this.getClass());
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] pieces = line.split(",", 3);
            NodeReference origin = NodeReference.parseNode(pieces[0]);
            NodeReference destination = NodeReference.parseNode(pieces[1]);
            int count = Integer.parseInt(pieces[2]);

            this.registerTraffic(origin, destination, count);
        }
    }

    public void registerTraffic(NodeReference origin, NodeReference destination, int count) {
        if (!this.traffic.containsKey(origin)) {
            this.traffic.put(origin, new HashMap<NodeReference, Integer>());
        }

        this.traffic.get(origin).put(destination, count);
    }

    @Override
    protected void generateHourlyTraffic(int hour, SortedMap<Long, List<VehicleInsertion>> insertions) {
        for (NodeReference origin : this.traffic.keySet()) {
            for (NodeReference destination : this.traffic.get(origin).keySet()) {
                int count = this.traffic.get(origin).get(destination);

                for (int i = 0; i < count; i++) {
                    long time = hour * 3600 * 1000;
                    time += random.nextLong(0, 3600 * 1000 - 1);

                    if (!insertions.containsKey(time)) {
                        insertions.put(time, new LinkedList<VehicleInsertion>());
                    }

                    insertions.get(time).add(new VehicleInsertion(origin, destination));
                }
            }
        }
    }
}