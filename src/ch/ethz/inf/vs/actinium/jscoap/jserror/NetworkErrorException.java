package ch.ethz.inf.vs.actinium.jscoap.jserror;


public class NetworkErrorException extends RequestErrorException {

	public NetworkErrorException() {
		super();
	}

	public NetworkErrorException(String message) {
		super(message);
	}

	public NetworkErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkErrorException(Throwable cause) {
		super(cause);
	}
	
}
