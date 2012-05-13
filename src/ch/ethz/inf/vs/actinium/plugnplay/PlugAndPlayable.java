package ch.ethz.inf.vs.actinium.plugnplay;

import ch.ethz.inf.vs.californium.coap.RequestHandler;

/**
 * PlugAndPlayable is the interface for all apps. Apps must be able to start,
 * shutdown, restart and have a name.
 * 
 * @author Martin Lanter
 */
public interface PlugAndPlayable extends RequestHandler, Runnable {

	// start the application in a new thread
	public void start();
	
	// shut the application down
	public void shutdown();
	
	// restart the application
	public void restart();
	
	// return the name of this application
	public String getName();
	
}
