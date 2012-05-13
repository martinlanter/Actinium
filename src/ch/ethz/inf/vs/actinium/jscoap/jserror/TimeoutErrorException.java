package ch.ethz.inf.vs.actinium.jscoap.jserror;

public class TimeoutErrorException extends RequestErrorException {

	public TimeoutErrorException() {
		super();
	}

	public TimeoutErrorException(String message) {
		super(message);
	}

	public TimeoutErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutErrorException(Throwable cause) {
		super(cause);
	}
	
}
