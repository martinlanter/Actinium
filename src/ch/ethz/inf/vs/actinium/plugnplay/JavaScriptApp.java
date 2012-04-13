package ch.ethz.inf.vs.actinium.plugnplay;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import xmlhttp.XMLHttpRequest;
import ch.ethz.inf.vs.actinium.cfg.AppConfig;
import ch.ethz.inf.vs.actinium.cfg.AppType;
import ch.ethz.inf.vs.actinium.jscoap.CoAPConstants;
import ch.ethz.inf.vs.actinium.jscoap.JavaScriptResource;
import coap.CodeRegistry;
import coap.DELETERequest;
import coap.GETRequest;
import coap.POSTRequest;
import coap.PUTRequest;

/**
 * JavaScriptApp executes apps written in JavaScript using Rhino.
 * 
 * @author Martin Lanter
 */
public class JavaScriptApp extends AbstractApp implements CoAPConstants {

	// The app's configuration
	private AppConfig appcfg; // Note: appcfg can be null if has ben started SimpleAppServer
	
	// The thred that executes the app
	private Thread thread;
	
	// The JavaScript environment for this app (e.g. variables)
	private ScriptableObject scope;
	
	// The object "app", that is accessible by JavaScript
	private JavaScriptAccess jsaccess;

	/*
	 * Functions onget, onpost, onput, ondelete can be assigned to from within
	 * JavaScript by e.g. "app.root.onget = ..."
	 */
	public Function onget, onpost, onput, ondelete; // for JavaScript scripts
	
	// The handler, that makes JavaScript execute requests on "app.root"
	private JSRequestHandler requestHandler;
	
	// Java Packages, that are imported automatically for Rhino
	private static String[] defaultpackages = {
		"java.lang",
		"java.util",
		"java.io",
		"java.net",
		"java.text",
		"coap", // Response, CodeRegistry
		"ch.ethz.inf.vs.actinium.jscoap", // CoAPRequest
		"ch.ethz.inf.vs.actinium.jscoap.jserror" // CoAPRequest RequestErrorException
	};

	/**
	 * Constructs a new JavaScriptApp with the given appcofnig
	 * @param appconfig the configuration for this app
	 */
	public JavaScriptApp(AppConfig appconfig) {
		super(appconfig);
		this.appcfg = appconfig;
		this.requestHandler = new JSRequestHandler();
		appconfig.getObservable().addObserver(this);
	}
	
	@Override
	protected synchronized void startImpl() {
		thread = new Thread(this,"JavsScript "+getName());
		thread.start();
	}
	
	@Override
	protected synchronized void restartImpl() {
		if (started) {
			thread.interrupt();
			cleanup();
		}
		started = false;
		start();
	}
	
	@Override
	protected synchronized void shutdownImpl() {
		thread.interrupt();
		cleanup(); // call app.onunload in JavaScript
	}

	/**
	 * First, runs the app's JavaScript code. Second executes all incoming
	 * requests for the app and its subresources.
	 */
	@Override
	public void run() {
		try {
			// Load JavaScript code
			String path = appcfg.getProperty(AppConfig.DIR_PATH) +
			appcfg.getProperty(AppConfig.APP) +
			"." + AppType.getAppSuffix(appcfg.getProperty(AppConfig.TYPE));
		
			File file = new File(path);
			Scanner scanner = new Scanner(file).useDelimiter("\\Z");
			String code = "";
			if (scanner.hasNext())
		    	code = scanner.next();
		    scanner.close();

		    // execute code
		    execute(code);

		    // Start receiving requests for this app
		    super.receiveMessages();
		    
        } catch (Exception e) {
        	System.err.println("Exception while executing "+getName());
        	e.printStackTrace();
        }
	}

	/**
	 * Executes the given JavaScript code.
	 * @param code the JavaScript code
	 */
	public void execute(String code) {
		String name = getName();
		code = prependDefaultImporterCode(code);
		
		Context cx = Context.enter();
		cx.addActivationName(name);
        try {
        	// initialize JavaScrip environmetn for the app: scope (variables)
        	scope = cx.initStandardObjects();
    		Scriptable s = new ImporterTopLevel(cx); // makes it possible to import packages inside JS
    		scope.setPrototype(s);
            
    		// add two global functions dump and addSubResource
            String[] names = { "dump", "addSubResource"};
            scope.defineFunctionProperties(names, JavaScriptStaticAccess.class,
                                           ScriptableObject.DONTENUM);

            try {
            	// Add AJAX' XMLHttpRequest to JavaScript
            	ScriptableObject.defineClass(scope,	XMLHttpRequest.class);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
            
			// Add object "app" to JavaScript
            jsaccess = new JavaScriptAccess();
			Object wrappedOut = Context.javaToJS(jsaccess, scope);
			ScriptableObject.putProperty(scope, "app", wrappedOut);
            
			// Execute code
			cx.evaluateString(scope, code, name, 1, null);

        } catch (WrappedException e) {
        	Throwable cause = e.getCause();
        	if (cause!=null && cause instanceof InterruptedException) {
        		// this was a controlled shutdown, e.g. with app.stop()
        		System.out.println("JavaScript app "+getName()+" has been interrupted");
        	} else {
        		// Don't catch this exception but throw it again
        		System.err.println("Exception in JavaScript app "+getName()+": "+e.getMessage());
        		throw e;
        	}
        
        } finally {
            Context.exit();
        }
	}
	
	@Override
	public long getRunningThreadId() {
		if (thread!=null)
			return thread.getId();
		else return -1;
	}
	
	@Override
	public void changed() {
		super.changed();
	}
	
	@Override
	public void performGET(GETRequest request) {
		if (appcfg.getBool(AppConfig.ENABLE_REQUEST_DELIVERY))
			requestHandler.performGET(request);
		else request.respond(CodeRegistry.RESP_FORBIDDEN, "Request delivery has been disabled for this app");
	}

	@Override
	public void performPUT(PUTRequest request) {
		if (appcfg.getBool(AppConfig.ENABLE_REQUEST_DELIVERY))
			requestHandler.performPUT(request);
		else request.respond(CodeRegistry.RESP_FORBIDDEN, "Request delivery has been disabled for this app");
	}

	@Override
	public void performPOST(POSTRequest request) {
		if (appcfg.getBool(AppConfig.ENABLE_REQUEST_DELIVERY))
			requestHandler.performPOST(request);
		else request.respond(CodeRegistry.RESP_FORBIDDEN, "Request delivery has been disabled for this app");
	}

	@Override
	public void performDELETE(DELETERequest request) {
		if (appcfg.getBool(AppConfig.ENABLE_REQUEST_DELIVERY))
			requestHandler.performDELETE(request);
		else request.respond(CodeRegistry.RESP_FORBIDDEN, "Request delivery has been disabled for this app");
	}

	/**
	 * Calls the function onunload in the JavaScript script if defined.
	 */
	private void cleanup() {
		Function onunload = jsaccess.onunload;
		if (onunload!=null) {
		    System.out.println("Call cleanup function onunload in script "+getName());
			try {
				Context cx = Context.enter();
				Scriptable scope = onunload.getParentScope();
				onunload.call(cx, scope, Context.toObject(jsaccess, scope), new Object[0]);
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				Context.exit();
			}
		} else {
		    System.out.println("No cleanup function in script "+getName()+" defined");
		}
	}
	
	/**
	 * Creates commands for importing the default Java packages.
	 */
	private String prependDefaultImporterCode(String code) {
		StringBuffer buffer = new StringBuffer();
		for (String pckg:defaultpackages) {
			buffer.append("importPackage(Packages."+pckg+");");
		}
		return buffer.toString() + code;
	}

	/**
	 * JavaScriptAccess is the class for the object "app", that is accessible
	 * from within JavaScript. It contains "app.root", the resource root and
	 * "app.dump" and "app.error" for printing to the standart output streams
	 * and further functions.
	 */
	public class JavaScriptAccess implements CoAPConstants {
		public JavaScriptApp root = JavaScriptApp.this; // "app.root"
		
		public Function onunload = null; // "app.onunload = ..."
		
		/**
		 * Prints to the standard output stream
		 */
		public void dump(Object... args) { // "app.dump("hello world");
			if (isOutputAllowed()) {
				String output = convertToOutput(args);
				JavaScriptApp.this.printOutput(output);
			}
		}
		
		/**
		 * Prints to the standart error output stream
		 */
		public void error(Object... args) {
			if (isErrorOutputAllowed()) {
				String output = convertToOutput(args);
				JavaScriptApp.this.printErrorOutput(output);
			}
		}
		
		/**
		 * Concatenates the arguments
		 */
		private String convertToOutput(Object[] args) {
			StringBuffer buffer = new StringBuffer();
			if (args.length>0 && args[0]!=null && !"".equals(args[0])) {
				// add "\n"s (newline) from first arg to buffer
				String arg0 = args[0].toString();
				for (int nl=0; arg0.charAt(nl)=='\n'; nl++)
					buffer.append('\n');
			}
			for (int i = 0; i < args.length; i++) {
				if (i == 0) {
					buffer.append(getName()+":\t");
					String str = args[0].toString().replaceFirst("\n*",""); // remove "\n" at beginning
					buffer.append(str.replace("\n", "\n\t"));
				} else {
					buffer.append("\t");
					buffer.append(args[i].toString().replace("\n", "\n\t"));
				}
			}
			return buffer.toString();
		}
		
		/**
		 * Stops the app.
		 */
		public void shutdown(Object... args) {
			JavaScriptApp.this.shutdown();
		}
		
		/**
		 * Restarts the app.
		 */
		public void restart(Object... args) {
			JavaScriptApp.this.restart();
		}
		
		/**
		 * Sleeps for the given amount of milliseconds 
		 */
		public void sleep(long millis) throws InterruptedException {
			Thread.sleep(millis);
		}
		
		/**
		 * Returns the current time in milliseconds.
		 * @return the current time in milliseconds
		 */
		public long getTime() {
			return System.currentTimeMillis();
		}

		/**
		 * Returns the current value of the most precise available system timer,
		 * in nanoseconds.
		 * @return the current value of the most precise available system timer,
		 *         in nanoseconds.
		 */
		public long getNanoTime() {
			return System.nanoTime();
		}
		
		/**
		 * Returns the corresponding value to the key, if found or null.
		 * @param key the key
		 * @return the corresponding value to the key, if found or null.
		 */
		public String getProperty(String key) {
			return appcfg.getProperty(key);
		}

		/**
		 * Returns the corresponding value to the key, if found or the given
		 * default value.
		 * 
		 * @param key the key
		 * @return the corresponding value to the key, if found or the given
		 *         default value.
		 */
		public String getProperty(String key, String dflt) {
			return appcfg.getProperty(key, dflt);
		}
	}

	/*
	 * Gives this resource the same abilities as JavaScriptResource that the
	 * scripts use internally without actually inheriting from it.
	 */
	private class JSRequestHandler extends JavaScriptResource {
		
		@Override
		public Function getOnget() {
			return JavaScriptApp.this.onget;
		}

		@Override
		public Function getOnpost() {
			return JavaScriptApp.this.onpost;
		}

		@Override
		public Function getOnput() {
			return JavaScriptApp.this.onput;
		}

		@Override
		public Function getOndelete() {
			return JavaScriptApp.this.ondelete;
		}
		
		// Return the resource for which the request is meant
		@Override
		public Object getThis() {
			return jsaccess.root;
		}
	}
}
