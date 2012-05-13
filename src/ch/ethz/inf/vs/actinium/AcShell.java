package ch.ethz.inf.vs.actinium;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Observable;
import java.util.Scanner;

import ch.ethz.inf.vs.actinium.cfg.AppConfig;
import ch.ethz.inf.vs.actinium.plugnplay.JavaScriptApp;

import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;

/**
 * A console based application that simulates an app server and runs a
 * JavaScriptApp specified in the arguments given in the main method.
 * <p>
 * Start the ACtinium shell with
 * <pre>
 * java -cp bin/;lib/js-14.jar;lib/Californium.jar;lib/E4XUtils.jar ch.ethz.inf.vs.actinium.AcShell appserver/installed/storage.js -name ABC
 * </pre>
 * 
 * @author Martin Lanter
 */
public class AcShell extends LocalEndpoint {

	private static final int IDX_FILE = 0;

	public static final int ERR_INIT_FAILED = 1;
	public static final int ERR_NO_FILE_SPECIFIED = 2;
	public static final int ERR_FILE_IO = 3;
	
	private String path;
	private String name;
	
	public AcShell(String path, String name) throws SocketException {
		super();
		this.path = path;
		this.name = name;
	}
	
	public AcShell(String path, String name, int port) throws SocketException {
		super(port);
		this.path = path;
		this.name = name;
	}
	
	private void execute() {
		try {
			File file = new File(path);
			Scanner scanner = new Scanner(file).useDelimiter("\\Z");
		    String code = scanner.next();  
		    scanner.close();
		    
		    if (name==null)
		    	name = file.getName();
		    
		    AppConfig appcfg = new AppConfig();
		    appcfg.setProperty(AppConfig.NAME, name);
		    OneTimeJavaScriptApp app = new OneTimeJavaScriptApp(appcfg);
		    addResource(app);
		    
		    app.execute(code);
		    
		} catch (IOException e) {
			System.err.println("Error while reading file "+path+": "+e.getMessage());
			System.exit(ERR_FILE_IO);
		}
	}

	public static void main(String[] args) {
		if (args.length==0) {
			printInfo();
			System.exit(ERR_NO_FILE_SPECIFIED);
		}
		
		// parse arguments
		try {
			int index = 0; // expected argument
			
			int port = -1;
			String file = null;
			String name = null;
			
			for (int i=0;i<args.length;i++) {
				String arg = args[i]; // currenct arg
				if (arg.startsWith("-")) { // arg is an option
					if (arg.equals("-port")) {
						try {
							port = Integer.parseInt(args[i+1]);
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Invalid port number "+args[i+1], e);
						}
						i = i+1; // skip next arg, which is the port number
					
					} else if (arg.equals("-name")) {
						name = args[i+1];
						i = i+1; // skip next arg, which is the name
					} else {
						System.err.println("Error: unrecognized or incomplete command line.");
						printInfo();
						System.exit(ERR_NO_FILE_SPECIFIED);
					}
					
				} else { // arg is a required argument
					if (index==IDX_FILE) {
						file = arg;
					}
					index++;
				}
			}
			
			if (file==null) {
				System.err.println("No JavaScript file to be launched specified");
				System.exit(ERR_NO_FILE_SPECIFIED);
			}
			
			// setup server and execute code
			AcShell server;
			if (port==-1) server = new AcShell(file, name);
			else server = new AcShell(file, name, port);
			
			System.out.println("Simple App Server listening on port "+server.port());
			server.execute();
			
		} catch (Exception e) {
			System.err.println("Failed to launch AcShell");
			e.printStackTrace();
			System.exit(ERR_INIT_FAILED);
		}
	}
	
	public static void printInfo() {
		System.out.println(
				"Simple App Server" +
				"\n" +
				"\nUsage: SimpleApp [-port] [-name] FILE" +
				"\n  FILE : The JavaScript app to be launched" +
				"\nOptions:" +
				"\n  -port: Listen on specified port (Default: 5683)" +
				"\n  -name: Name of the created Resource (Default: filename)" +
				"\n" + 
				"\nExamples:" +
				"\n  SimpleAppClient myscript.js" +
				"\n  SimpleAppClient -port 62333 myscript.js"
			);
	}
	
	private static class OneTimeJavaScriptApp extends JavaScriptApp {
		
		private OneTimeJavaScriptApp(AppConfig appcfg) {
			super(appcfg);
		}
		
		@Override
		public void start() { throw new UnsupportedOperationException(); }
		
		@Override
		public void restart() { throw new UnsupportedOperationException(); }
		
		@Override
		public void shutdown() { System.exit(0); }
		
		@Override
		public void run() { throw new UnsupportedOperationException(); }
		
		@Override
		public void update(Observable o, Object arg) { throw new UnsupportedOperationException(); }
		
		@Override
		public void execute(String code) {
			super.execute(code);
		}
	}
}
