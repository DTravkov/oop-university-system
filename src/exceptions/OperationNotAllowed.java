package exceptions;

import model.enumeration.UIMessage;

public class OperationNotAllowed extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowed(Object... args) {
        super(UIMessage.ERR_OPERATION_NOT_ALLOWED, args);
    }
}
