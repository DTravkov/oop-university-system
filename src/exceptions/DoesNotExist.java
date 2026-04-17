package exceptions;

public class DoesNotExist extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DoesNotExist() {
		super("This record does not exist");
	}
	public DoesNotExist(String message) {
		super(message);
	}
}
