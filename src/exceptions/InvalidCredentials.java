package exceptions;

public class InvalidCredentials extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidCredentials() {
		super("Credentials doesn't match any record");
	}

	public InvalidCredentials(String message) {
		super(message);
	}
}
