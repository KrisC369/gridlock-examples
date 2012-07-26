package be.kuleuven.cs.gridlock.examples;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.geo.coordinates.Coordinates;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import be.kuleuven.cs.gridlock.utilities.graph.GraphBuilder;
import be.kuleuven.cs.gridlock.utilities.graph.GraphFactory;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a network loader that can read in comma separated text files.
 *
 * The network definition consists of three parts:
 * 1. A list of links
 * 2. A list of nodes
 * 3. A list of routes
 *
 * Each of these parts should be in a separate file.
 *
 * Each line in the links file defines one one directional link.  The format of
 * the line should be the following:
 * <originref>,<destinationref>,<length>,<speed>,<capacity>
 *
 * Where
 *  <originref> is the node reference of the links origin
 *  <destinationref> is the node reference of the links destination
 *  <length> is the links length in km
 *  <speed> is the links maximum speed in km/h
 *  <capacity> is the links capacity in vehicles/h
 *
 * Each line in the nodes file defines one node.  The format of the line should
 * be the following:
 * <noderef>,<x>,<y>
 *
 * Where
 *  <noderef> is the node reference
 *  <x> is the node's x coordinate
 *  <y> is the node's y coordinate
 *
 * Each line in the routes file defines one possible route in the network.  The
 * format of the line should be the following:
 * <originref>,<noderef>*,<destinationref>
 *
 * Where
 *  <originref> is the node reference of the routes origin
 *  <destinationref> is the node reference of the routes destination
 *  <noderef>* is an optional comma separated list of nodes on the route
 *
 * The MatLabLoader extends the GraphFactory service.  The composition is done
 * using the Java Service Loader and is configured in
 * src/main/resources/META-INF/services/be.kuleuven.cs.gridlock.utilities.graph.GraphFactory
 *
 * The MatLabLoader will be used during the simulation bootstrap process if its
 * {@link #canBuildService(be.kuleuven.cs.gridlock.configuration.Configuration)}
 * returns true.  This method will return true if the nodes and links file
 * pointers are configured.
 *
 * @author Rutger Claes <rutger.claes@cs.kuleuven.be>
 */
public class MatLabLoader implements GraphFactory {

    // These are the configuration keys that will point to the relevant network files
    public static final String MATLAB_LINK_LOCATION_KEY = "simulation.model.network.cib.matlab.links";
    public static final String MATLAB_NODE_LOCATION_KEY = "simulation.model.network.cib.matlab.nodes";
    public static final String MATALB_ROUTE_LOCATION_KEY = "simulation.model.network.cib.matlab.routes";

    @Override
    public Graph<NodeReference, LinkReference> buildService( Configuration configuration ) {
        GraphBuilder<NodeReference,LinkReference> builder = new GraphBuilder<NodeReference,LinkReference>();

        try {
            String nodeLocation = configuration.getString( MATLAB_NODE_LOCATION_KEY, null );
            BufferedReader nodeReader = new BufferedReader( new InputStreamReader( MatLabLoader.class.getResourceAsStream( nodeLocation ) ) );

            Map<Long,NodeReference> nodes = new HashMap<Long, NodeReference>();
            Map<NodeReference,Coordinates> coordinates = new HashMap<NodeReference, Coordinates>();

            String nextLine = null;

                while( ( nextLine = nodeReader.readLine() ) != null ) {
                    String[] pieces = nextLine.split( ",", 3 );
                    Coordinates location = Coordinates.coordinatesAt( Double.parseDouble( pieces[1] ), Double.parseDouble( pieces[1] ) );
                    Long nodeId = Long.parseLong( pieces[0] );
                    nodes.put( nodeId, new NodeReference( nodeId ) );
                    coordinates.put( nodes.get( nodeId ), location );
                    builder.buildNode( nodes.get( nodeId ), "location", location );
                }

            String linkLocation = configuration.getString( MATLAB_LINK_LOCATION_KEY, null );
            BufferedReader linkReader = new BufferedReader( new InputStreamReader( MatLabLoader.class.getResourceAsStream( linkLocation ) ) );
            while( ( nextLine = linkReader.readLine() ) != null ) {
                Map<String,Object> attributes = new HashMap<String,Object>();

                String[] pieces = nextLine.split( ",", 5 );
                NodeReference origin = nodes.get( Long.parseLong( pieces[0] ) );
                NodeReference destination = nodes.get( Long.parseLong( pieces[1] ) );
                Float length = Float.parseFloat( pieces[2] );
                Integer speed = Integer.parseInt( pieces[3] );
                Integer capacity = Integer.parseInt( pieces[4] );

                attributes.put( "lanes", 1 );
                attributes.put( "length", length );
                attributes.put( "capacity", capacity );
                attributes.put( "speed", speed );
                attributes.put( "oneway", true );
                attributes.put( "coordinates", Arrays.asList( coordinates.get( origin ), coordinates.get( destination ) ) );

                String id = "" + origin + ":" + destination;
                builder.buildEdgeWithMap( new LinkReference( id ), origin, destination, attributes );
            }
        } catch( IOException ex ) {
            Logger.getLogger( MatLabLoader.class.getName() ).log( Level.SEVERE, null, ex );
        }

        return builder.getGraph();
    }

    @Override
    public boolean canBuildService( Configuration configuration ) {
        return configuration.getString( MATLAB_NODE_LOCATION_KEY, null ) != null &&
                configuration.getString( MATLAB_LINK_LOCATION_KEY, null ) != null;
    }
}