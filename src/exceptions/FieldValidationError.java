package exceptions;

import model.UIMessages;

public class FieldValidationError extends ApplicationException {

	private static final long serialVersionUID = 1L;
	String[] invalidFields = new String[0];
	public FieldValidationError(String... invalidFields) {
		super(UIMessages.EX_FIELD_VALIDATION.getKey());
		this.invalidFields = invalidFields;
	}
	
	@Override
	public String getMessage() {
		String msg = super.getMessage();
		return msg + " " + String.join(", ", invalidFields);
	}

	
}
