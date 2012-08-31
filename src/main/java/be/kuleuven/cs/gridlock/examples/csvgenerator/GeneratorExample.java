package be.kuleuven.cs.gridlock.examples.csvgenerator;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.FileConfigurationHelper;
import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.coordination.VehicleCoordination;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.events.EventFilter;
import be.kuleuven.cs.gridlock.simulation.events.EventListener;
import be.kuleuven.cs.gridlock.traffic.AbstractTrafficGenerator;
import be.kuleuven.cs.gridlock.traffic.TrafficSourceRecorder;
import be.kuleuven.cs.gridlock.traffic.csv.CSVTrafficSourceRecorder;
import be.kuleuven.cs.gridlock.examples.SimpleAverageEventListener;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import be.kuleuven.cs.gridlock.utilities.graph.GraphFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;

/**
 *
 * @author Kristof Coninx <kristof.coninx@student.kuleuven.be>
 */
public class GeneratorExample {

    public static final String CSV_GENERATOR_LOCATION_KEY = "traffic.source.csv.file";
    private SimulationContext context;
    private AbstractTrafficGenerator generator;
    private TrafficSourceRecorder recorder;
    private EventListener listener2;

    public static void main(String[] args) {
        GeneratorExample example = new GeneratorExample();
        example.setup();
        example.run();
    }

    private void setup() {
        Configuration configuration = FileConfigurationHelper.load(GeneratorExample.class.getResource("/matlab/configuration2.properties"));
        RandomData random = new RandomDataImpl(new MersenneTwister(configuration.getLong("gridlock.random.seed", 0)));
        Graph<NodeReference, LinkReference> graph = ServiceFactory.Helper.load(GraphFactory.class, configuration);

        try {
            generator = new SimpleEvenlyDistributedTrafficGenerator(graph, random, configuration);
            //generator = new SimpleRandomDistributedTrafficGenerator(graph, random, configuration);
            String destinationLoc = configuration.getString(CSV_GENERATOR_LOCATION_KEY, null);
            File file = new File(GeneratorExample.class.getResource(destinationLoc).toURI());
            if (!file.getAbsoluteFile().exists()) {
                file.createNewFile();
            }
            recorder = new CSVTrafficSourceRecorder(new FileOutputStream(file));
            this.context = SimulationContext.loadSimulationContext(configuration);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneratorExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            Logger.getLogger(GeneratorExample.class.getName()).log(Level.SEVERE, null, e);
        } catch (URISyntaxException e) {
            Logger.getLogger(GeneratorExample.class.getName()).log(Level.SEVERE, null, e);
        }

        this.context.addSimulationComponent(new VehicleCoordination());

        //OPTIONAL: similar to MatlabExample
        EventListener listener = new SimpleAverageEventListener();
        EventFilter[] filters = new EventFilter[]{new EventFilter.CatchAll()};
        this.context.getEventController().registerListener(listener, filters);
        listener2 = listener;
    }

    private void run() {
        generator.record(recorder);
        this.context.getSimulation().run();
        double avgSpeed = ((SimpleAverageEventListener) listener2).getAverage();
        System.out.println("Average speed for cars: " + avgSpeed + " m/s");
        System.out.println("Average speed for cars: " + (avgSpeed * 3.6) + " km/h"); //convert back to km/h
    }
}