package exceptions;

import model.UIMessages;

public class InvalidCredentials extends ApplicationException {

	private static final long serialVersionUID = 1L;
	
	public InvalidCredentials() {
		super(UIMessages.EX_INVALID_CREDENTIALS.getKey());
	}
}
