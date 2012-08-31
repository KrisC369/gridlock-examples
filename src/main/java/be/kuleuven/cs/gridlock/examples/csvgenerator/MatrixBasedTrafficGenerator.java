package be.kuleuven.cs.gridlock.examples.csvgenerator;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.traffic.AbstractTrafficGenerator;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import org.apache.commons.math3.random.RandomData;

/**
 *
 * @author Kristof Coninx <kristof.coninx@student.kuleuven.be>
 */
public abstract class MatrixBasedTrafficGenerator extends AbstractTrafficGenerator {

    public static final String GENERATOR_MATRIX_LOCATION_KEY = "traffic.generator.matrix.file";
    private Configuration configuration;

    public MatrixBasedTrafficGenerator(Configuration config) {
        this(null, null, config);
    }

    public MatrixBasedTrafficGenerator(Graph<NodeReference, LinkReference> graph, RandomData random, Configuration configuration) {
        super(graph, random, configuration);
        this.configuration = configuration;
    }

    /**
     * @return the configuration
     */
    protected Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    protected void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}