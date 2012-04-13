package ch.ethz.inf.vs.actinium;


/**
 * Calls the main Method of the AppServer.
 * Main entry points in the app server:
 * 	- ch.ethz.inf.vs.appserver.AppServer.main(): Creates a new AppServer
 *  - ch.ethz.inf.vs.appserver.SimpleAppServer.main(): Starts a console based app server with only one app running
 *  - ch.ethz.inf.vs.appserver.client.AppClient.main(): Starts a CoAP Client
 * 
 * @author Martin Lanter
 */
public class Main {

	public static void main(String[] args) {
		AcServer.main(args);
//		AppClient.main(args);
//		SimpleAppServer.main(args);
	}
}
