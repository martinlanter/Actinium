package ch.ethz.inf.vs.actinium.jscoap;

import ch.ethz.inf.vs.californium.coap.Request;

/**
 * A Sender is able to send a request and abort it again.
 * 
 * @author Martin Lanter
 */
public interface Sender {

	public void send(Request request);
	
	public void abort();
	
}
