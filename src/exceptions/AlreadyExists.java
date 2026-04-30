package exceptions;

import model.enumeration.UIMessage;

public class AlreadyExists extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AlreadyExists(Object... args) {
        super(UIMessage.ERR_ALREADY_EXISTS, args);
    }
}
