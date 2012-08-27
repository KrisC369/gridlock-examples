/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.cs.gridlock.examples;

import be.kuleuven.cs.gridlock.simulation.events.Event;
import be.kuleuven.cs.gridlock.simulation.events.EventListener;
import java.util.Map;

/**
 * Simple EventListenerImplementation capable of returning average speed of all vehicles combined.
 * @author Kristof Coninx <kristof.coninx@student.kuleuven.be>
 */
public class SimpleAverageEventListener implements EventListener {

    public SimpleAverageEventListener() {
    }
    private double total = 0;
    private long count = 0;

    public void notifyOf(Event event) {
        if (event.getType().equalsIgnoreCase("vehicle:speed") || event.getType().equalsIgnoreCase("vehicle:update:speed")) {
            Map<String, Object> atts = event.getAttributes();
            try {
                total += ((Double) atts.get("speed"));
                count++;
            } catch (Exception e) {
            }
        }
    }

    public double getAverage() {
        if (count != 0) {
            return total / count;
        }
        return 0;
    }
}