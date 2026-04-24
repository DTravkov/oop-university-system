package exceptions;

import model.enumeration.UIMessages;

public class AlreadyExists extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AlreadyExists(Object... args) {
        super(UIMessages.ERR_ALREADY_EXISTS, args);
    }
}
