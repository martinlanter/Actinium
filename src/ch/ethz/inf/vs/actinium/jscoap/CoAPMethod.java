package ch.ethz.inf.vs.actinium.jscoap;

/**
 * This class is not an enum, for that scripts can use strings like "get",
 * instead of an enum.
 * 
 * @author Martin Lanter
 */
public final class CoAPMethod {

	// prevent initialization
	private CoAPMethod() {}
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	
	
}
