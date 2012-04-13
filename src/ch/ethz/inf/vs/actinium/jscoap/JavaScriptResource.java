package ch.ethz.inf.vs.actinium.jscoap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;

import coap.DELETERequest;
import coap.GETRequest;
import coap.POSTRequest;
import coap.PUTRequest;
import coap.Request;
import endpoint.LocalResource;

/**
 * It is not possible to add further methods or fields to this class within
 * JavaScript (Rhino). If this is necessary, use AbstractJavaScriptResource.
 * 
 * @author Martin Lanter
 */
public class JavaScriptResource extends LocalResource implements CoAPConstants {
	// Cannot extend ScriptableObject, because has to extend LocalResource
	// Cannot (reasonably) implement Scriptable, because we then have to implement all 16 methods like ScriptableObject

	public Function onget = null;
	public Function onpost = null;
	public Function onput = null;
	public Function ondelete = null;
	
	public JavaScriptResource() {
		super(null);
	}

	public JavaScriptResource(String resourceIdentifier) {
		super(resourceIdentifier);
	}
	
	public JavaScriptResource(String resourceIdentifier, boolean hidden) {
		super(resourceIdentifier, hidden);
	}
	
	@Override
	public void changed() {
		super.changed();
	}
	
	public Function getOnget() {
		return onget;
	}
	
	public Function getOnpost() {
		return onpost;
	}
	
	public Function getOnput() {
		return onput;
	}
	
	public Function getOndelete() {
		return ondelete;
	}
	
	public Object getThis() {
		return this;
	}
	
	@Override
	public void performGET(GETRequest request) {
		Function onget = getOnget();
		if (onget!=null) {
			performFunction(onget, request);
		} else {
			super.performGET(request);
		}
	}

	@Override
	public void performPOST(POSTRequest request) {
		Function onpost = getOnpost();
		if (onpost!=null) {
			performFunction(onpost, request);
		} else {
			super.performPOST(request);
		}
	}

	@Override
	public void performPUT(PUTRequest request) {
		Function onput = getOnput();
		if (onput!=null) {
			performFunction(onput, request);
		} else {
			super.performPUT(request);
		}
	}

	@Override
	public void performDELETE(DELETERequest request) {
		Function ondelete = getOndelete();
		if (ondelete!=null) {
			performFunction(ondelete, request);
		} else {
			super.performDELETE(request);
		}
	}
	
	private void performFunction(Object object, Request request) {
		NativeFunction fun = (NativeFunction) object;
		try {
			Context cx = Context.enter();
			Scriptable scope = fun.getParentScope();
			Object thisObj = getThis();
			fun.call(cx, scope, Context.toObject(thisObj, scope), new Object[] {request});
		} finally {
			Context.exit();
		}
	}
}
