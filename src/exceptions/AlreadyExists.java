package exceptions;

public class AlreadyExists extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AlreadyExists() {
		super("This record already exist");
	}
	public AlreadyExists(String message) {
		super(message);
	}
}
