package ch.ethz.inf.vs.actinium.cfg;

/**
 * This is a class for that the endities can be used as String without any
 * further computation (in contrary to an enum). It is also easy to introduce
 * new types, which only are not visible here.
 * 
 * @author Martin Lanter
 */
public final class AppType {

	// prevent instantiation
	private AppType() {}
	
	public static final String JAVASCRIPT = "javascript";
	public static final String UNKNOWN = "unknown";
	
	public static String getAppSuffix(String type) {
		if (JAVASCRIPT.equalsIgnoreCase(type))
			return "js";
		else
			throw new IllegalArgumentException("Unknown app type "+type);
	}
}
