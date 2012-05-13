package ch.ethz.inf.vs.actinium;


/**
 * Calls the main Method of the AppServer.
 * Main entry points in the app server:
 * 	- ch.ethz.inf.vs.actinium.AcServer.main(): Creates a new AppServer
 *  - ch.ethz.inf.vs.actinium.AcShell.main(): Starts a console based app server with only one app running
 * 
 * @author Martin Lanter
 */
public class Main {

	public static void main(String[] args) {
		AcServer.main(args);
//		AcShell.main(args);
	}
}
