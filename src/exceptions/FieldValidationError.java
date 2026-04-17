package exceptions;

public class FieldValidationError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FieldValidationError() {
		super("Required fields of the model are not provided");
	}
	public FieldValidationError(String msg) {
		super(msg);
	}

	
}
