package ch.ethz.inf.vs.actinium;

import java.net.SocketException;

import ch.ethz.inf.vs.actinium.cfg.Config;
import ch.ethz.inf.vs.actinium.install.InstallResource;
import ch.ethz.inf.vs.actinium.plugnplay.AbstractApp;
import coap.CodeRegistry;
import coap.GETRequest;
import coap.Option;
import coap.OptionNumberRegistry;
import coap.Request;
import demonstrationServer.resources.MirrorResource;
import endpoint.LocalEndpoint;
import endpoint.LocalResource;
import endpoint.Resource;

/**
 * Actinium (Ac)
 * <p>
 * An Actinium app-server creates a structure of CoAP resources to run arbitrary
 * JavaScript apps on it. Most Notably an InstallResource to install new apps
 * and an AppResource which holds the instances of those apps.
 * <p>
 * An app or its subresources must not block the receiver thread of Californium.
 * Therefore every app has its own thread for handling requests. If a requests
 * has an app or a subresource of an app as target the server passes the request
 * to this app, which's receiver thread then will handle the request.
 * 
 * @author Martin Lanter
 */
public class AcServer extends LocalEndpoint {

	// appserver's configuration
	private Config config;

	// AppManager to figure out to which apps a request belongs
	private AppManager manager;
	
	// resource that holds the stats for all app instances
	private StatsResource stats;
	
	/**
	 * Constructs a new AppServer with the specified config.
	 * @param config the app server's config.
	 * @throws SocketException if the Socket is blocked.
	 */
	public AcServer(Config config) throws SocketException {
		this.config = config;
		
		this.manager = new AppManager(config);
		
		AppResource appres = new AppResource(config, manager);
		InstallResource insres = new InstallResource(config, manager);

		this.addResource(appres);
		this.addResource(insres);

		this.addResource(
				config.createConfigResource(config.getProperty(Config.CONFIG_RESOURCE_ID)));
		
		this.stats = new StatsResource(config, manager);
		this.addResource(stats);
		appres.startApps();
		
		this.addResource(new MirrorResource());
	}

	/**
	 * Catch any exceptions throws inside handleRequest. Otherwise an exception
	 * will halt the ReiceiverThread and stop Californium.
	 */
	@Override
	public void handleRequest(Request request) {
		try {
			// record message
			String resid = getResourceIdentifier(request);
			Resource resource = getResource(resid);
			if (resource!=null)
				stats.record(request, resource);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			// deliver message to receiver
			deliverRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// from super.handleRequest with special treat for subresources of apps
	private void deliverRequest(Request request) {
		if (request != null) {

			// retrieve resource identifier
			String resourceIdentifier = getResourceIdentifier(request);

			// lookup resource
			LocalResource resource = getResource(resourceIdentifier);

			// check if resource available
			if (resource != null) {

				String appname = getAppName(resource);
				if (appname!=null) { // request for a subresource of an app
					// invoke request handler of the app the resource belongs to
					/*
					 * An app or its subresources must not block the receiver
					 * thread. Therefore every app has its own thread for
					 * handling requests.
					 */
					AbstractApp app = manager.getApp(appname);
					app.deliverRequestToSubResource(request, resource);
					
				} else { // request not for a subresource of an app
					// invoke request handler of the resource
					request.dispatch(resource);
				}

				// check if resource is to be observed
				if (request instanceof GETRequest
						&& request.hasOption(OptionNumberRegistry.OBSERVE)) {
					// establish new observation relationship
					resource.addObserveRequest((GETRequest) request);

				} else if (resource.isObserved(request.endpointID())) {
					// terminate observation relationship on that resource
					resource.removeObserveRequest(request.endpointID());
				}

			} else {
				// resource does not exist
				System.out.printf("[%s] Resource not found: '%s'\n", getClass()
						.getName(), resourceIdentifier);

				request.respond(CodeRegistry.RESP_NOT_FOUND);
			}
		}
	}
	
	/**
	 * Returns the name of the app instance to which the specified resource
	 * belongs to or null if it corresponds to no app instance.
	 * 
	 * @param res the resource.
	 * @return the app instance it belongs to or null.
	 */
	private String getAppName(Resource res) {
		// path in this form: /apps/running/appname/...
		String path = res.getResourcePath();
		String[] parts = path.split("/"); // parts[0] is ""
		
		String idapps = config.getProperty(Config.APPS_RESOURCE_ID);
		String idrun = config.getProperty(Config.RUNNING_RESOURCE_ID);
		
		if (parts.length>3 && parts[1].equals(idapps) && parts[2].equals(idrun)) {
			return parts[3];
		} else {
			return null;
		}
	}

	/**
	 * Returns the resource identifier path to the target in the specified
	 * request.
	 * 
	 * @param request the request
	 * @return the resource identifier path to the request's target.
	 */
	private static String getResourceIdentifier(Request request) {
		return Option.join(request.getOptions(OptionNumberRegistry.URI_PATH), "/");
	}
	
	/**
	 * Setups a new config and a new app server.
	 * @param args no arguments required
	 */
	public static void main(String[] args) {
		try {
			Config config = new Config();
			AcServer server = new AcServer(config);
			
			System.out.println("App Server listening on port "+server.port());
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
