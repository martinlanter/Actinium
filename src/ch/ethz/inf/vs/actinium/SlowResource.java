package ch.ethz.inf.vs.actinium;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

public class SlowResource extends LocalResource {

	private int counter = 0;
	
	public SlowResource() {
		super("slow");
	}
	
	@Override
	public void performPOST(POSTRequest request) {
		
		try {
			System.out.println("Bearbeite request "+request.getMID());
			Thread.sleep(5000);
			
			request.respond(CodeRegistry.RESP_CONTENT, "counter = "+counter);
			counter++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
