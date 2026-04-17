package exceptions;

import model.UIMessages;

public class AlreadyExists extends ApplicationException {

	private static final long serialVersionUID = 1L;
	
	public AlreadyExists() {
		super(UIMessages.EX_ALREADY_EXISTS.getKey());
	}

}
