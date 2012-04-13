package ch.ethz.inf.vs.actinium;

import coap.CodeRegistry;
import coap.POSTRequest;
import endpoint.LocalResource;

public class SlowResource extends LocalResource {

	private int counter = 0;
	
	public SlowResource() {
		super("slow");
	}
	
	@Override
	public void performPOST(POSTRequest request) {
		
		try {
			System.out.println("Bearbeite request "+request.getID());
			Thread.sleep(5000);
			
			request.respond(CodeRegistry.RESP_CONTENT, "counter = "+counter);
			counter++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
