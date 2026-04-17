package exceptions;

import model.UIMessages;

public class DoesNotExist extends ApplicationException {

	private static final long serialVersionUID = 1L;
	
	public DoesNotExist() {
		super(UIMessages.EX_DOES_NOT_EXIST.getKey());
	}
	
}
