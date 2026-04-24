package exceptions;

import model.enumeration.UIMessages;

public class OperationNotAllowed extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowed(Object... args) {
        super(UIMessages.ERR_OPERATION_NOT_ALLOWED, args);
    }
}
