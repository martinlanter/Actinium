package ch.ethz.inf.vs.actinium.jscoap;

/**
 * If an object in JavaScript (Rhino) is of type AbstractJavaScriptResource, it
 * is possible to add further methods and fields in JavaScript. However, in this
 * case it is not possible to use any other than the empty constructor. If the
 * consturctor Resource(String) is required, use JavaScriptResource.
 * 
 * @author Martin Lanter
 */
public abstract class AbstractJavaScriptResource extends JavaScriptResource {

	/**
	 * It is not possible to have any other than the empty constructor in an
	 * abstract class in JavaScript (Rhino). Do not add any other constructors.
	 */
	public AbstractJavaScriptResource() {
		super(null);
	}

}
