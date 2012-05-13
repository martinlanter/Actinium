package ch.ethz.inf.vs.actinium.jscoap.jserror;

public class RequestErrorException extends RuntimeException {

	public RequestErrorException() {
		super();
	}

	public RequestErrorException(String message) {
		super(message);
	}

	public RequestErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestErrorException(Throwable cause) {
		super(cause);
	}
	
}
